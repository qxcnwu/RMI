package UTiles;

import org.jetbrains.annotations.Contract;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @Author qxc
 * @Date 2022 2022/9/25 22:51
 * @Version 1.0
 * @PACKAGE UTiles
 */
public class MethodsCache {
   private static HashMap<String,MethodConstruct> map=new HashMap<>();

   public static void add(String name, Method method,Class<?> aClass) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
       MethodConstruct mt=new MethodConstruct(method,aClass);
       if(mt.isCanVisited()){
           map.put(name,mt);
       }
   }

   public static MethodConstruct get(String name){
       return map.getOrDefault(name,null);
   }

   @Contract(pure = true)
   public static HashMap<String,MethodConstruct> getMap(){
       return map;
   }
}
