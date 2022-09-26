package UTiles;

import Socket.SendMessage;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import pojo.Message;
import pojo.NetAddr;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author qxc
 * @Date 2022 2022/9/19 21:54
 * @Version 1.0
 * @PACKAGE UTiles
 */
@Slf4j
public class ExectorTools {
    private final ExecutorService service;
    private final BlockingDeque<Message> q;
    public static ExectorTools exectorTools = null;

    @Contract(pure = true)
    public ExectorTools(int maxThread) {
        this(maxThread, Executors.newFixedThreadPool(maxThread));
    }

    public ExectorTools(int maxThread, ExecutorService executorService) {
        this.service = executorService;
        q = new LinkedBlockingDeque<>(2 * maxThread);
    }

    /**
     * add msg object
     *
     * @param msg
     */
    public void add(Message msg) throws InterruptedException {
        q.put(msg);
    }

    /**
     * deal with Message Add msg in service
     * @throws InterruptedException
     */
    public void run() throws InterruptedException {
        final Message msg = q.take();
        final Future<Message> task = service.submit(new ExecRunable(msg));
        ResultSendTools.getInstance(16).add(task);
    }

    /**
     * single class
     * @param maxThread
     * @return
     */
    @Contract(pure = true)
    public static ExectorTools getInstance(int maxThread){
        if(exectorTools==null){
            synchronized (ExectorTools.class){
                if(exectorTools==null){
                    exectorTools=new ExectorTools(maxThread);
                }
            }
        }
        return exectorTools;
    }
}

/**
 * wait for task result and send to Client
 */
@Slf4j
class ResultSendTools{
    private final ExecutorService service;
    private static ResultSendTools resultSendTools=null;

    public ResultSendTools(int maxThread){
        service=Executors.newFixedThreadPool(maxThread);
    }

    public void add(Future<Message> task){
        service.submit(new SendTask(task));
    }

    @Contract(pure = true)
    public static ResultSendTools getInstance(int maxThread){
        if(resultSendTools==null){
            synchronized (ResultSendTools.class){
                if(resultSendTools==null){
                    resultSendTools=new ResultSendTools(maxThread);
                }
            }
        }
        return resultSendTools;
    }

    static class SendTask implements Runnable{

        private final Future<Message> task;

        @Contract(pure = true)
        public SendTask(Future<Message> task){
            this.task=task;
        }

        @Override
        public void run() {
            try {
                log.info("等待结果");
                Message message = this.task.get();
                SendMessage.send(Utiles.swapNet(message));
            } catch (InterruptedException | ExecutionException e) {
                log.info(e.getMessage()+" wrong send msg answer");
            }
        }
    }

}