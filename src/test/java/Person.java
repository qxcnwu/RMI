import Annotation.Structs;
import UTiles.ClassReslover;
import UTiles.Convert;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import pojo.Message;

import java.lang.reflect.*;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * @Author qxc
 * @Date 2022 2022/9/21 23:01
 * @Version 1.0
 * @PACKAGE PACKAGE_NAME
 */
@Slf4j
class JavaTypeTest {


    @Test
    public void Find() {
        /**
         * 如何创建一个类的构造器
         */

        /**
         * 首先检查类中是否包含对应的构造器注解
         */
        Message msg = new Message();
        final Class aClass = msg.getClass();
        if (!ClassReslover.canConstruct(aClass, Structs.class)) {
            log.info("Can't been convert to structs");
            return;
        }
        final Field[] fds = aClass.getDeclaredFields();
        for (Field fd : fds) {
            System.out.println(fd.getType());
            System.out.println(fd.getName());
        }
    }


    @Test
    public void testConvert() {
        t a = new t();
        final Method[] methods = t.class.getMethods();
        for (Method m : methods) {
            System.out.println(m);
            final Parameter[] parameters = m.getParameters();
            for (int i = 0; i < parameters.length; i++) {
                System.out.println(Convert.convert(parameters[i]).getName());
            }
        }
    }

    @Test
    public void TransTest(){
        int.class.cast(12);
    }


    @Test
    public void constructTest() throws InvocationTargetException, InstantiationException, IllegalAccessException {
        ClassReslover classReslover=new ClassReslover(Person.class);
        HashMap<String,Object> map=new HashMap<>();
        mapAdd(map);
        final Object o1 = classReslover.parseObj(map);
        System.out.println(o1);
    }

    private static void mapAdd(HashMap<String,Object> map){
        map.put("arg0",12);
        map.put("arg1","qxc");
        map.put("arg2",false);
        map.put("arg3",'c');
        map.put("arg4", null);
        map.put("arg5",1.);
        map.put("arg6",1.f);

        map.put("arg7",0);
        map.put("arg8",false);
        map.put("arg9",'c');
        map.put("arg10", null);
        map.put("arg11",1.);
        map.put("arg12",1.f);

        map.put("arg13",new int[]{1,2,3});
        map.put("arg14",new boolean[]{false,true});
        map.put("arg15",new char[]{'a','b'});
        map.put("arg16",new byte[10]);
        map.put("arg17",new double[]{1.,2.});
        map.put("arg18",new float[]{1.f,2.f});

        map.put("arg19",new Integer[]{1,2,3});
        map.put("arg20",new String[]{"q","a"});
        map.put("arg21",new Boolean[]{false,true});
        map.put("arg22",new Character[]{'a','b'});
        map.put("arg23",new Byte[10]);
        map.put("arg24",new Double[]{1.,2.});
        map.put("arg25",new Float[]{1.f,2.f});

        map.put("arg26",new Date());

    }

    @Test
    public void showName(){
        System.out.println(Integer[].class.getName());
    }

}

class t {
    public void add(int a, double b, float c, boolean d, byte e, char f, long g) {

    }

    public void add(int[] a, double[] b, float[] c, boolean[] d, byte[] e, char[] f, long[] g) {

    }
}

@Structs
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Person {
    /*
    基本数据类型
     */
    private Integer id;
    private String name;
    private Boolean man;
    private Character asd;
    private Byte aByte;
    private Double db;
    private Float ft;

    private int idx;
    private boolean bl;
    private char as;
    private byte asdd;
    private double dbb;
    private float flo;

    private int[] idx1;
    private boolean[] bl1;
    private char[] as1;
    private byte[] asdd1;
    private double[] dbb1;
    private float[] flo1;

    private Integer[] id1;
    private String[] name1;
    private Boolean[] man1;
    private Character[] asd1;
    private Byte[] aByte1;
    private Double[] db1;
    private Float[] ft1;
    /**
     * 拓展数据类型
     */
    private Date date;
}










