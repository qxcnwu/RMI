package UTiles;

import Annotation.Structs;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import pojo.Message;
import pojo.NetAddr;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

/**
 * @Author qxc
 * @Date 2022 2022/9/20 15:17
 * @Version 1.0
 * @PACKAGE UTiles
 */
public class Utiles {
    /**
     * swap msg object's net address
     * @param msg
     * @return
     */
    @Contract("_ -> param1")
    public static @NotNull Message swapNet(@NotNull Message msg){
        NetAddr temp=msg.getClient();
        msg.setClient(msg.getService());
        msg.setService(temp);
        return msg;
    }

    /**
     * 扫描路径下的类文件
     * @param basePack
     * @throws ClassNotFoundException
     */
    public static void ScanPojo(String basePack) throws ClassNotFoundException {
        String classpath = Utiles.class.getResource("/").getPath();
        basePack = basePack.replace(".", File.separator);
        String searchPath = classpath + basePack;
        ArrayList<String> ans=new ArrayList<>();
        doPath(new File(searchPath),ans);
        for (String s : ans) {
            s = s.replace(classpath.replace("/","\\").replaceFirst("\\\\",""),"").replace("\\",".").replace(".class","");
            Class<?> cls = Class.forName(s);
            if (cls.getAnnotation(Structs.class)!=null) {
                ConstructCache.add(cls.getName(),new ClassReslover(cls));
            }
        }
        /**
         * 二次装配
         */
        for (ClassReslover cls : ConstructCache.getMap().values()) {
            cls.assembleParam();
        }
    }

    /**
     * 扫描函数
     * @param basePack
     * @throws ClassNotFoundException
     */
    public static void ScanMethod(String basePack) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        String classpath = Utiles.class.getResource("/").getPath();
        basePack = basePack.replace(".", File.separator);
        String searchPath = classpath + basePack;
        ArrayList<String> ans=new ArrayList<>();
        doPath(new File(searchPath),ans);
        for (String s : ans) {
            s = s.replace(classpath.replace("/","\\").replaceFirst("\\\\",""),"").replace("\\",".").replace(".class","");
            Class<?> cls = Class.forName(s);
            final Method[] methods = cls.getDeclaredMethods();
            for(Method method:methods){
                MethodsCache.add(method.getName(),method,cls);
            }
        }
    }

    /**
     * 该方法会得到所有的类，将类的绝对路径写入到classPaths中
     * @param file
     */
    private static void doPath(@NotNull File file, ArrayList<String> classPaths) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f1 : files) {
                doPath(f1,classPaths);
            }
        } else {//标准文件
            //标准文件我们就判断是否是class文件
            if (file.getName().endsWith(".class")) {
                classPaths.add(file.getPath());
            }
        }
    }
}
