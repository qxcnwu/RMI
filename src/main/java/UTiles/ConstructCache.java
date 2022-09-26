package UTiles;

/**
 * @Author qxc
 * @Date 2022 2022/9/24 23:11
 * @Version 1.0
 * @PACKAGE UTiles
 */

import lombok.Data;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;

/**
 * 构造器缓存
 */
public class ConstructCache {

    private static final HashMap<String, ClassReslover> MAP = new HashMap<>();

    public static ClassReslover get(String name) {
        return MAP.getOrDefault(name, null);
    }

    public static void add(String name, ClassReslover constructCache) {
        MAP.put(name, constructCache);
    }

    /**
     * 获取相关的MAP信息
     * @return
     */
    @Contract(pure = true)
    public static HashMap<String, ClassReslover> getMap(){
        return MAP;
    }

}




