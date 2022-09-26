import Annotation.Visited;
import Funtion.ComputeImpl;
import UTiles.Exec;
import UTiles.ExecRunable;
import UTiles.ExectorTools;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pojo.Message;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * @Author qxc
 * @Date 2022 2022/9/19 22:24
 * @Version 1.0
 * @PACKAGE PACKAGE_NAME
 */
@Slf4j
public class ExectorToolsTest {
    @Test
    public void testExec() throws InterruptedException {
        ExectorTools exec=new ExectorTools(4);
        new Thread(()->{
            while(true){
                try {
                    exec.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        new Thread(()->{
            while(true){
                try {
                    exec.add(new Message());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    Thread.sleep((long) (Math.random()*100));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }).start();
        Thread.sleep(1000*60*24);
    }

    public void TestExec(){
        addMethod();
        final ConcurrentHashMap<String, Method> map = Exec.getMap();
        ExecutorService service= Executors.newFixedThreadPool(2);
        ArrayList<Future<Message>> arrayList=new ArrayList<>();
        for (String str:map.keySet()) {
            Message msg=new Message();
            msg.setMethodHash(str);
            ExecRunable exec=new ExecRunable(msg);
            arrayList.add(service.submit(exec));
        }
        try {
            Message message1 = arrayList.get(0).get();
            log.info(message1+" ");
            Message message2 = arrayList.get(1).get();
            log.info(message2+"");
            log.info(map+"");
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

    }

    @Test
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
    public void esexTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        ComputeImpl compute=new ComputeImpl();
        final Method method = ComputeImpl.class.getDeclaredMethod("add", Integer[].class);
        Integer[] k=new Integer[]{1,2,3,4,5};
        final Object answer = method.invoke(compute, (Object) new Integer[]{1,2,3,4,5});
        System.out.println(answer);
    }

    @SneakyThrows
    @Test
    public void okTest() throws NoSuchMethodException {
        Integer[] names = new Integer[]{1, 2, 3};
        Method sayHello = A.class.getDeclaredMethod("sayHello", Integer[].class);
        sayHello.setAccessible(true);
        sayHello.invoke(new A(), new Object[]{names});
    }
}

class A{
    private void sayHello(Integer[] names){
        //...
        System.out.println("sayHello invoked");
    }
}

