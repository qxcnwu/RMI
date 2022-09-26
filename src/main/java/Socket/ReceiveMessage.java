package Socket;

import UTiles.AnswerCache;
import UTiles.ExectorTools;
import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pojo.Message;
import pojo.NetAddr;

import java.nio.charset.StandardCharsets;

/**
 * @Author qxc
 * @Date 2022 2022/9/19 19:51
 * @Version 1.0
 * @PACKAGE Socket
 */
@Slf4j
public class ReceiveMessage {
    /**
     * receive message which send to localhost
     *
     * @param addr
     */
    @Contract(pure = true)
    public static void receiveMsg(NetAddr addr, boolean IsService) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            ChannelFuture channelFuture = serverBootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new UnpackageMsg(IsService))
                    .bind(addr.getPort()).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

/**
 * unpackage msg from string to object
 */
@Slf4j
class UnpackageMsg extends ChannelInitializer<SocketChannel> {

    private final boolean isService;

    public UnpackageMsg(boolean isService) {
        this.isService = isService;
    }

    @Override
    protected void initChannel(@NotNull SocketChannel socketChannel) {
        ChannelPipeline pipeline = socketChannel.pipeline();
        pipeline.addLast(new StringDecoder(StandardCharsets.UTF_8));
        if (isService) {
            pipeline.addLast(new MsgObjectDecoder());
            pipeline.addLast(new DoMain());
        } else {
            pipeline.addLast(new MsgObjectDecoderClient());
        }
    }
}

@Slf4j
class MsgObjectDecoder extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        try {
            String message = (String) msg;
            msg = JSON.parseObject(message, Message.class);
            ctx.fireChannelRead(msg);
        } catch (ClassCastException ca) {
            log.error("change to msg error" + ca.getMessage());
        }
    }
}

@Slf4j
class MsgObjectDecoderClient extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        try {
            String message = (String) msg;
            msg = JSON.parseObject(message, Message.class);
            Message ms = (Message) msg;
            ms.encodeMap();
            AnswerCache.add(ms);
            ctx.fireChannelRead(msg);
        } catch (ClassCastException ca) {
            log.error("change to msg error" + ca.getMessage());
        }
    }
}


/**
 * add task in ExecutorService and done
 */
@Slf4j
class DoMain extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        if (msg == null) {
            log.info("Want to get Message but get null!");
            return;
        }
        try {
            Message ms = (Message) msg;
            ms.encodeMap();
            ExectorTools.getInstance(16).add(ms);
        } catch (ClassCastException ex) {
            log.warn("msg change false");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        Channel channel = ctx.channel();
        if (channel.isActive()) {
            ctx.close();
        }
    }
}
