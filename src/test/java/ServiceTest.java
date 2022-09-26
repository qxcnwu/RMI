import Annotation.Visited;
import Funtion.ComputeImpl;
import Socket.ReceiveMessage;
import UTiles.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pojo.NetAddr;
import server.RMIServerImpl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * @Author qxc
 * @Date 2022 2022/9/20 15:44
 * @Version 1.0
 * @PACKAGE PACKAGE_NAME
 */
@Slf4j
public class ServiceTest {
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

    @Test
    public void TestReceive() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        /**
         * 扫描
         */
        Utiles.ScanPojo("pojo");
        Utiles.ScanMethod("Funtion");

        new Thread(()->{
            final ExectorTools exec = ExectorTools.getInstance(16);
            while(true){
                try {
                    exec.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        ReceiveMessage.receiveMsg(new NetAddr(IP,34567),true);
    }

    @Test
    public void ImplTest(){
        RMIServerImpl server=RMIServerImpl.getInstance(
                new String[]{"",""},
                new String[]{"",""},
                new NetAddr("127.0.0.1",34567),
                16
        );
    }
}
