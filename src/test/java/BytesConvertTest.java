import UTiles.ClassReslover;
import UTiles.ConstructCache;
import UTiles.Utiles;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @Author qxc
 * @Date 2022 2022/9/25 17:23
 * @Version 1.0
 * @PACKAGE PACKAGE_NAME
 */
public class BytesConvertTest {

    @Test
    public void byteConvert() throws IOException, ClassNotFoundException, InvocationTargetException, InstantiationException, IllegalAccessException {
        HashMap<String,Object> map=new HashMap<>();
        HashMap<String,Object> ip=new HashMap<>();
        ip.put("ip","127.0.0.1");
        ip.put("port",12345);
        map.put("client",ip);
        ByteArrayOutputStream byt=new ByteArrayOutputStream();
        ObjectOutputStream obj=new ObjectOutputStream(byt);
        obj.writeObject(map);

        byte[] bytes=byt.toByteArray();
        ByteArrayInputStream byteInt=new ByteArrayInputStream(bytes);
        ObjectInputStream objInt=new ObjectInputStream(byteInt);
        final HashMap<String,Object> object = (HashMap<String, Object>) objInt.readObject();

        Utiles.ScanPojo("pojo");
        System.out.println(Arrays.toString(ConstructCache.get("pojo.Msg").getCons()));
        for (ClassReslover cls : ConstructCache.getMap().values()) {
            cls.assembleParam();
        }
        System.out.println(Arrays.toString(ConstructCache.get("pojo.Msg").getCons()));
        final Object o = ConstructCache.get("pojo.Msg").parseObj(object);
        System.out.println(o);

    }
}
