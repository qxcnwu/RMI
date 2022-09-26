package server;

import Socket.ReceiveMessage;
import UTiles.ExectorTools;
import UTiles.Utiles;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Contract;
import pojo.NetAddr;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qxc
 * @date 2022 2022/9/19 19:40
 * @version 1.0
 * @PACKAGE server
 */
public class RMIServerImpl {

    private final String[] pojoPath;
    private final String[] methodPath;
    private final NetAddr service;
    private final int execNum;
    private static RMIServerImpl server;

    @Contract(pure = true)
    public RMIServerImpl(String[] pojoPath, String[] methodPath, NetAddr Service, int execNum){
        this.pojoPath=pojoPath;
        this.methodPath=methodPath;
        this.service=Service;
        this.execNum=execNum;
        scan();
        startService();
        ReceiveMessage.receiveMsg(Service,true);
    }

    @SneakyThrows
    private void scan(){
        for(String s:pojoPath){
            Utiles.ScanPojo(s);
        }
        for(String s:methodPath){
            Utiles.ScanMethod(s);
        }
    }

    /**
     * 启动服务器
     */
    private void startService(){
        ExecutorService service= Executors.newSingleThreadExecutor();
        service.submit(()->{
            final ExectorTools exec = ExectorTools.getInstance(execNum);
            while(true){
                try {
                    exec.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Contract(pure = true)
    public static RMIServerImpl getInstance(String[] pojoPath, String[] methodPath, NetAddr Service, int execNum){
        synchronized (RMIServerImpl.class){
            if(server==null){
                synchronized (RMIServerImpl.class){
                    server = new RMIServerImpl(pojoPath,methodPath,Service,execNum);
                    return server;
                }
            }
            return server;
        }
    }

    @Contract(pure = true)
    public static RMIServerImpl getInstance(){
        synchronized (RMIServerImpl.class){
            if(server==null){
                throw new RuntimeException("RMI can not be build");
            }
            return server;
        }
    }
}
