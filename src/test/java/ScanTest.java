import UTiles.*;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author qxc
 * @Date 2022 2022/9/24 23:02
 * @Version 1.0
 * @PACKAGE PACKAGE_NAME
 */
public class ScanTest {
    private static @NotNull HashMap<String, Object> getMap() {
        HashMap<String, Object> map = new HashMap<>();
        HashMap<String, Object> net = new HashMap<>();
        net.put("ip", "127.0.0.1");
        net.put("port", 12345);
        map.put("client", net);
        return map;
    }

    @Test
    public void testScan() throws ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Utiles.ScanPojo("pojo");
        System.out.println(Arrays.toString(ConstructCache.get("pojo.Msg").getCons()));
        for (ClassReslover cls : ConstructCache.getMap().values()) {
            cls.assembleParam();
        }
        Utiles.ScanMethod("Funtion");
        final MethodConstruct add = MethodsCache.get("mutiply");

        HashMap<String, Object> map = new HashMap<>();
        map.put("arg0", new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9});

        final Object invoke = add.invoke(map);
        System.out.println(invoke);
    }
}
