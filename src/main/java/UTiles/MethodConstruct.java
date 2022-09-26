package UTiles;

import Annotation.Visited;
import Funtion.ComputeImpl;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

/**
 * @author 邱星晨
 * @version 1.0
 * @date 2022 2022/9/25 22:28
 * @PACKAGE UTiles
 */
@Slf4j
@Data
public class MethodConstruct {

    private Method method;
    private MethodParam[] paramName;
    private int size;
    private Class<?> aClass;
    private boolean canVisited = false;
    private Object obj;

    @Contract(pure = true)
    public MethodConstruct(@NotNull Method method,Class<?> aClass){
        this.aClass=aClass;
        this.method = method;
        size = method.getParameterCount();
        paramName = new MethodParam[size];
        canStruct();
        getsObj();
    }

    /**
     * 获取无参数对象
     */
    @Contract(pure = true)
    private void getsObj() {
        if(!canVisited){
            return;
        }
        try {
            obj = aClass.getDeclaredConstructor().newInstance();
            canVisited=true;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            canVisited=false;
            log.info("No such zero param method "+e.getMessage());
        }
    }

    /**
     * 判断是否可以被解析
     */
    @Contract(pure = true)
    private void canStruct() {
        final Visited annotation = method.getAnnotation(Visited.class);
        canVisited = annotation != null;
        parseParam();
    }

    /**
     * 解析参数
     */
    private void parseParam() {
        final Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            paramName[i] = new MethodParam(parameters[i].getName(), new ClassReslover.Param(parameters[i]));
        }
    }

    /**
     * 解析传入的对象
     *
     * @param map
     * @return
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Contract(pure = true)
    private Object @Nullable [] parseMap(HashMap<String, Object> map) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        Object[] target = new Object[size];
        for (int i = 0; i < size; i++) {
            final String name = paramName[i].getName();
            if (!map.containsKey(name)) {
                throw new RuntimeException("Can not parse map without key " + name);
            }
            target[i] = paramName[i].getParam().parseParam(map.get(name));
        }
        return target;
    }

    /**
     * 执行方法
     *
     * @return
     */
    public Object invoke(HashMap<String, Object> map) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        final Object[] objects = parseMap(map);
        return method.invoke(obj, objects);
    }

    @Data
    @AllArgsConstructor
    static class MethodParam {
        private String name;
        private ClassReslover.Param param;
    }
}