package UTiles;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author qxc
 * @Date 2022 2022/9/23 16:38
 * @Version 1.0
 * @PACKAGE UTiles
 */
public class Convert {

    private static final ArrayList<String> Prefic = new ArrayList<>(List.of("[I", "[D", "[F", "[Z", "[B", "[C", "[J", "int", "boolean", "char", "byte", "double", "float", "long"));
    private static final ArrayList<String> BASIC = new ArrayList<>(List.of("int", "boolean", "char", "byte", "double", "float", "long"));

    /**
     * 判断是否是java自带的类
     * @param name
     * @return
     */
    public static boolean isJava(@NotNull String name) {
        if (name.contains("java.lang") || name.contains("java.util")) {
            return true;
        }
        if (Prefic.contains(name)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是不是java积累
     * @param name
     * @return
     */
    @Contract(pure = true)
    public static boolean basic(String name) {
        return BASIC.contains(name);
    }

    public static Class<?> convert(@NotNull Parameter param) {
        final Class<?> type = param.getType();
        if (type.isArray()) {
            return listsConvert(type);
        } else {
            return objConvert(type);
        }
    }

    @Contract(pure = true)
    private static Class<?> listsConvert(@NotNull Class<?> aClass) {
        switch (aClass.getName()) {
            case "[I":
                return Integer[].class;
            case "[D":
                return Double[].class;
            case "[F":
                return Float[].class;
            case "[Z":
                return Boolean[].class;
            case "[B":
                return Byte[].class;
            case "[C":
                return Character[].class;
            case "[J":
                return Long[].class;
            default:
                return aClass;
        }
    }

    @Contract(pure = true)
    private static Class<?> objConvert(@NotNull Class<?> aClass) {
        switch (aClass.getName()) {
            case "int":
                return Integer.class;
            case "double":
                return Double.class;
            case "float":
                return Float.class;
            case "boolean":
                return Boolean.class;
            case "byte":
                return Byte.class;
            case "char":
                return Character.class;
            case "long":
                return Long.class;
            default:
                return aClass;
        }
    }

    public static Object ConvertArr(Object obj, @NotNull Class<?> aClass) {
        switch (aClass.getName()) {
            case "[Ljava.lang.Integer;":
                int[] tmp = (int[]) obj;
                return Arrays.stream(tmp).boxed().toArray(Integer[]::new);
            case "[Djava.lang.Integer;":
                double[] tmp1 = (double[]) obj;
                return Arrays.stream(tmp1).boxed().toArray(Double[]::new);
            case "[Fjava.lang.Integer;":
                return transFloat(obj);
            case "[Zjava.lang.Integer;":
                return transBoolean(obj);
            case "[Bjava.lang.Integer;":
                return transByte(obj);
            case "[Cjava.lang.Integer;":
                return transCharacter(obj);
            case "[Jjava.lang.Integer;":
                long[] tmp6 = (long[]) obj;
                return Arrays.stream(tmp6).boxed().toArray(Long[]::new);
            default:
                return obj;
        }
    }

    /**
     * 转换不同类型的数组
     *
     * @param obj
     * @return
     */
    @Contract(pure = true)
    private static @NotNull Object transFloatList(Object obj) {
        float[] tmp = (float[]) obj;
        int size = tmp.length;
        Float[] ans = new Float[size];
        for (int i = 0; i < size; i++) {
            ans[i] = tmp[i];
        }
        return ans;
    }

    @Contract(pure = true)
    private static @NotNull Object transBooleanList(Object obj) {
        boolean[] tmp = (boolean[]) obj;
        int size = tmp.length;
        Boolean[] ans = new Boolean[size];
        for (int i = 0; i < size; i++) {
            ans[i] = tmp[i];
        }
        return ans;
    }

    @Contract(pure = true)
    private static @NotNull Object transByteList(Object obj) {
        byte[] tmp = (byte[]) obj;
        int size = tmp.length;
        Byte[] ans = new Byte[size];
        for (int i = 0; i < size; i++) {
            ans[i] = tmp[i];
        }
        return ans;
    }

    @Contract(pure = true)
    private static @NotNull Object transCharacterList(Object obj) {
        char[] tmp = (char[]) obj;
        int size = tmp.length;
        Character[] ans = new Character[size];
        for (int i = 0; i < size; i++) {
            ans[i] = tmp[i];
        }
        return ans;
    }

    @Contract(pure = true)
    private static int transInteger(Object obj) {
        return (int) obj;
    }

    /**
     * 转换基本数据类型
     *
     * @param obj
     * @param name
     * @return
     */
    @Contract(pure = true)
    public static Object ConvertObj(Object obj, @NotNull String name) {
        switch (name) {
            case "int":
                return transInteger(obj);
            case "double":
                return transDouble(obj);
            case "float":
                return transFloat(obj);
            case "boolean":
                return transBoolean(obj);
            case "byte":
                return transByte(obj);
            case "char":
                return transCharacter(obj);
            case "long":
                return transLong(obj);
            default:
                return obj;
        }
    }

    @Contract(pure = true)
    private static double transDouble(Object obj) {
        return (double) obj;
    }

    @Contract(pure = true)
    private static float transFloat(Object obj) {
        return (float) obj;
    }

    @Contract(pure = true)
    private static boolean transBoolean(Object obj) {
        return (boolean) obj;
    }

    @Contract(pure = true)
    private static byte transByte(Object obj) {
        if (obj == null) {
            return "qxc".getBytes(StandardCharsets.UTF_8)[0];
        }
        return (byte) obj;
    }

    @Contract(pure = true)
    private static char transCharacter(Object obj) {
        return (char) obj;
    }

    @Contract(pure = true)
    private static long transLong(Object obj) {
        return (long) obj;
    }
}


