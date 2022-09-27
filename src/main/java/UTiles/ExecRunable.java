package UTiles;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pojo.Message;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * @Author qxc
 * @Date 2022 2022/9/20 10:14
 * @Version 1.0
 * @PACKAGE UTiles
 */
@Slf4j
public class ExecRunable implements Callable<Message> {
    private final Message msg;

    @Contract(pure = true)
    public ExecRunable(Message msg) {
        this.msg = msg;
    }

    /**
     * reflect exec function
     *
     * @return
     * @throws Exception
     */
    @Override
    public Message call() {
        /*
          Available use method
         */
        try {
            final MethodConstruct methodConstruct = MethodsCache.get(msg.getMethodHash());
            final Object answer = methodConstruct.invoke(msg.getMap());
            msg.setAns(answer);
            msg.setMsg("Method Done");
            return msg;
        } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
            msg.setMsg("wrong method type");
            return msg;
        }
    }
}

/**
 * proxy class exec
 */
@Slf4j
class DynamicProxy implements InvocationHandler {
    private Object obj;

    @Contract(pure = true)
    public DynamicProxy(Object obj) {
        this.obj = obj;
    }

    @Override
    public Object invoke(Object proxy, @NotNull Method method, Object[] args) throws Throwable {
        Object result = method.invoke(proxy, args);
        log.info(method.getName() + " exec has done");
        return result;
    }
}