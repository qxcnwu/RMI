package server;

import Socket.ReceiveMessage;
import Socket.SendMessage;
import org.jetbrains.annotations.Contract;
import pojo.Message;
import pojo.NetAddr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qxc
 * @date 2022 2022/9/19 19:39
 * @version 1.0
 * @PACKAGE server
 */
public class RMICenterImpl implements RMICenter {

    private NetAddr Listen;
    private static RMICenterImpl client;

    @Contract(pure = true)
    public RMICenterImpl(NetAddr Listen){
        this.Listen=Listen;
        startListen();
    }

    /**
     * 客户端接收
     */
    @Contract(pure = true)
    private void startListen(){
        ExecutorService service= Executors.newSingleThreadExecutor();
        service.submit(()->{
            ReceiveMessage.receiveMsg(Listen,false);
        });
    }

    @Override
    public void sendMSG(Message msg) {
        SendMessage.send(msg);
    }

    @Contract(pure = true)
    public static RMICenterImpl getInstance(NetAddr Service){
        synchronized (RMICenterImpl.class){
            if(client==null){
                synchronized (RMICenterImpl.class){
                    client = new RMICenterImpl(Service);
                    return client;
                }
            }
            return client;
        }
    }

    @Contract(pure = true)
    public static RMICenterImpl getInstance(){
        synchronized (RMICenterImpl.class){
            if(client==null){
                throw new RuntimeException("RMI client can not be build");
            }
            return client;
        }
    }
}
