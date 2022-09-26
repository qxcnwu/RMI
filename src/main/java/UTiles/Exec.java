package UTiles;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author qxc
 * @Date 2022 2022/9/20 11:43
 * @Version 1.0
 * @PACKAGE UTiles
 */
@Slf4j
public class Exec {
    private static final ConcurrentHashMap<String, Method> METHOD_MAP = new ConcurrentHashMap<>();

    public static void addMethod(@NotNull Method method){
        String name=method.getName();
        AtomicInteger version=new AtomicInteger(0);
        while(METHOD_MAP.containsKey(name)){
            name=name+version.getAndIncrement();
        }
        METHOD_MAP.put(name, method);
    }

    public static Method getMethod(String hashCode) {
        return METHOD_MAP.getOrDefault(hashCode, null);
    }

    @Contract(pure = true)
    public static ConcurrentHashMap<String, Method> getMap(){
        return METHOD_MAP;
    }
}
