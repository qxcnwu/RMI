package Socket;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pojo.Message;

import java.nio.charset.StandardCharsets;

/**
 * @Author qxc
 * @Date 2022 2022/9/19 19:45
 * @Version 1.0
 * @PACKAGE Socket
 */
@Slf4j
public class SendMessage {
    @Contract(pure = true)
    public static void send(@NotNull Message msg) {
        msg.decodeMap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        Channel channel = null;
        try {
            channel = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel ch) {
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.weakCachingConcurrentResolver(null)));
                            ch.pipeline().addLast(new SendMessageHandler(msg));
                        }
                    })
                    .connect(msg.getService().getIp(), msg.getService().getPort()).sync().channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            group.shutdownGracefully();
        }
    }
}

@Slf4j
class SendMessageHandler extends ChannelInboundHandlerAdapter {
    private final Message msg;

    public SendMessageHandler(Message msg) {
        this.msg = msg;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final ByteBuf buffer = ctx.alloc().buffer();
        byte[] bytes = JSON.toJSONString(msg).getBytes(StandardCharsets.UTF_8);
        buffer.writeBytes(bytes);
        ctx.writeAndFlush(buffer);
        log.info("Send to " + msg.getService().getIp() + ":" + msg.getService().getPort());
        //发送完成关闭链接
        ctx.channel().close();
    }


    @Override
    public void exceptionCaught(@NotNull ChannelHandlerContext ctx, Throwable cause) {
        log.error("Unexpected exception from downstream.", cause);
        ctx.close();
    }
}