import Annotation.Visited;
import Funtion.ComputeImpl;
import Socket.ReceiveMessage;
import Socket.SendMessage;
import UTiles.AnswerCache;
import UTiles.Exec;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pojo.Message;
import pojo.NetAddr;
import server.RMICenterImpl;
import server.RMIServerImpl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.IntStream;

/**
 * @Author qxc
 * @Date 2022 2022/9/20 15:22
 * @Version 1.0
 * @PACKAGE PACKAGE_NAME
 */
@Slf4j
public class ClientTest {

    private static final String IP="127.0.0.1";

    @BeforeEach
    public void addMethod(){
        final Method[] methods = ComputeImpl.class.getMethods();
        for(Method method:methods){
            final Visited visited = method.getAnnotation(Visited.class);
            if(visited==null){
                continue;
            }
            Exec.addMethod(method);
        }
    }


    @SneakyThrows
    @Test
    public void SendTest(){
        log.info(Exec.getMap()+"");
        new Thread(()->{
            ReceiveMessage.receiveMsg(new NetAddr(IP,23455),false);
        }).start();
        IntStream.range(1,10).parallel().forEach((i)->{
            Message msg=new Message();
            msg.setClient(new NetAddr(IP,23455));
            msg.setService(new NetAddr(IP,34567));
            msg.setMethodHash(i%2==0?"add":"mutiply");
            HashMap<String,Object> map=new HashMap<>();
            map.put("arg0",new int[]{1,2,3,4,5,6,7,8});
            msg.setMap(map);
            SendMessage.send(msg);
            log.info("Send");
        });
        final LinkedBlockingQueue<Message> ans = AnswerCache.getAns();
    }

    @SneakyThrows
    @Test
    public void ImplTest(){
        final RMICenterImpl instance = RMICenterImpl.getInstance(new NetAddr("127.0.0.1", 23455));
        IntStream.range(1,10).parallel().forEach((i)->{
            Message msg=new Message();
            msg.setClient(new NetAddr(IP,23455));
            msg.setService(new NetAddr(IP,34567));
            msg.setMethodHash(i%2==0?"add":"mutiply");
            HashMap<String,Object> map=new HashMap<>();
            map.put("arg0",new int[]{1,2,3,4,5,6,7,8});
            msg.setMap(map);
            instance.sendMSG(msg);
        });
        final LinkedBlockingQueue<Message> ans = AnswerCache.getAns();
        while (true){
            final Message take = ans.take();
            System.out.println(take);
        }
    }
}
