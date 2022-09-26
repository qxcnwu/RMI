package UTiles;

/**
 * @Author qxc
 * @Date 2022 2022/9/23 15:29
 * @Version 1.0
 * @PACKAGE UTiles
 */

import Annotation.Structs;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 用于创建一个类型的构造器
 * @author 邱星晨
 */
@Data
public class ClassReslover {
    private final Class<?> aClass;
    private final String packageName;
    private final Class<? extends Annotation>[] anns;
    private final HashMap<String, Class<?>> map;
    private ConstructosCls[] cons;
    private DefaultConstructor defaultConstructor;

     /**
     * create new construct of class<?>
     *
     * @param aClass
     */
    @SafeVarargs
    @Contract(pure = true)
    public ClassReslover(Class<?> aClass, String packageName, Class<? extends Annotation>... ann) {
        this.aClass = aClass;
        this.packageName = packageName;
        this.map = new HashMap<>();
        this.anns = ann;
        if (!canConstruct(aClass, ann)) {
            throw new RuntimeException(this.packageName + " can't be constructed without " + Arrays.toString(anns));
        }
        ParseConstruct();
        ParseWithoutConstruct();
    }

    @SafeVarargs
    public ClassReslover(@NotNull Class<?> aClass, Class<? extends Annotation>... ann) {
        this(aClass, aClass.getPackageName(), ann);
    }

    @SafeVarargs
    public ClassReslover(String className, Class<? extends Annotation>... ann) throws ClassNotFoundException {
        this(Class.forName(className), ann);
    }

    public ClassReslover(String className) throws ClassNotFoundException {
        this(Class.forName(className), Structs.class);
    }

    public ClassReslover(@NotNull Class<?> aClass) {
        this(aClass, aClass.getPackageName(), Structs.class);
    }

    /**
     * 判断类是否可以被参数化
     *
     * @param aClass 类
     * @param ann    需要满足的注解类型
     * @return
     */
    @SafeVarargs
    @Contract(pure = true)
    public static boolean canConstruct(@NotNull Class<?> aClass, Class<? extends Annotation> @NotNull ... ann) {
        final AtomicInteger ans = new AtomicInteger(ann.length);
        Arrays.stream(ann).parallel().forEach(an -> {
            if (aClass.getAnnotationsByType(an).length == 0) {
                return;
            }
            ans.addAndGet(-1);
        });
        return ans.get() == 0;
    }

    /**
     * 二次装配所有的构造器中的参数对象
     */
    public void assembleParam() {
        for (ConstructosCls cls : cons) {
            for (Param param : cls.getParams()) {
                param.getCon();
            }
        }
        if (defaultConstructor.canUse()) {
            defaultConstructor.getMethodMap().values().forEach(st -> {
                st.getParam().getCon();
            });
        }
    }

    /**
     * parse constructs of aClass
     */
    @Contract(pure = true)
    private void ParseConstruct() {
        final Constructor<?>[] constructors = aClass.getDeclaredConstructors();
        cons = new ConstructosCls[constructors.length];
        for (int i = 0; i < cons.length; ++i) {
            cons[i] = new ConstructosCls(constructors[i], aClass);
        }
    }

    /**
     * 创建参数解析方法
     */
    @Contract(pure = true)
    private void ParseWithoutConstruct() {
        final DefaultConstructor defaultConstructor = new DefaultConstructor(aClass);
        if (defaultConstructor.canUse()) {
            this.defaultConstructor = defaultConstructor;
        }
    }

    /**
     * 解析对象
     *
     * @param map
     * @return
     */
    public Object parseObj(HashMap<String, Object> map) throws InvocationTargetException, InstantiationException, IllegalAccessException {
        for (ConstructosCls conCls : cons) {
            if (!conCls.check(map)) {
                continue;
            }
            return conCls.parseObject(map);
        }
        if (defaultConstructor.canUse()) {
            return defaultConstructor.parseObj(map);
        }
        return null;
    }

    /**
     * 对于每一个类默认的参数设置方法
     * 采用get或者set方法填入参数
     */
    @Data
    @Slf4j
    static class DefaultConstructor {
        private HashMap<String, ST> methodMap;
        private Class<?> aClass;
        private Constructor<?> constructor;
        private boolean canUse;

        @Contract(pure = true)
        public DefaultConstructor(Class<?> aClass) {
            this.aClass = aClass;
            methodMap = new HashMap<>();
            getNoTypeCon(aClass);
            updateField();
        }

        /**
         * 获取set方法
         *
         * @param field
         * @return
         */
        private static @NotNull String getMethodName(@NotNull Field field) {
            String name= field.getName();
            final char[] chars = name.toCharArray();
            StringBuilder sb = new StringBuilder();
            sb.append("set");
            chars[0]=Character.toUpperCase(chars[0]);
            sb.append(chars);
            return sb.toString();
        }

        /**
         * 获取无参数构造方法
         *
         * @param aClass
         */
        private void getNoTypeCon(@NotNull Class<?> aClass) {
            try {
                constructor = aClass.getConstructor();
                canUse = true;
            } catch (NoSuchMethodException e) {
                canUse = false;
                log.info(aClass.getName() + " has no such empty constructor");
            }
        }

        /**
         * 解析所有set方法
         */
        @Contract(pure = true)
        private void updateField() {
            if (constructor == null) {
                return;
            }
            final Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                try {
                    final Method method = aClass.getMethod(getMethodName(field), field.getType());
                    final ST st = new ST(method, new Param(method.getParameters()[0]));
                    methodMap.put(field.getName(), st);
                } catch (NoSuchMethodException e) {
                    log.info(getMethodName(field) + " can't be parse");
                }
            }
        }

        /**
         * 解析相应的对象
         *
         * @return
         */
        public Object parseObj(@NotNull HashMap<String, Object> map) throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (constructor == null) {
                return null;
            }
            final Object ans = constructor.newInstance();
            for (String name : map.keySet()) {
                if (!methodMap.containsKey(name)) {
                    continue;
                }
                final ST st = methodMap.get(name);
                st.getMethod().invoke(ans, st.getParam().parseParam(map.get(name)));
            }
            return ans;
        }

        /**
         * 参数解析器是否可以使用
         *
         * @return
         */
        public boolean canUse() {
            return canUse;
        }

        /**
         * 参数解析装配类
         */
        @Data
        @AllArgsConstructor
        class ST {
            private Method method;
            private Param param;
        }

    }

    /**
     * 对应自定义类的构造对象
     */
    @Data
    static class ConstructosCls {
        private Constructor<?> constructor;
        private HashMap<String, Class<?>> map;
        private Param[] params;
        private Class<?> aClass;
        private Integer num;

        @Contract(pure = true)
        public ConstructosCls(@NotNull Constructor<?> constructor, Class<?> aClass) {
            this.constructor = constructor;
            this.aClass = aClass;
            map = new HashMap<>();
            num = constructor.getParameterCount();
            params = new Param[num];
            getConType();
        }

        /**
         * 获取参数类型
         */
        @Contract(pure = true)
        private void getConType() {
            final Parameter[] parameters = constructor.getParameters();
            final AtomicInteger tmpidx = new AtomicInteger(0);
            Arrays.stream(parameters).forEach(param -> {
                params[tmpidx.getAndIncrement()] = new Param(param);
                map.put(param.getName(), param.getType());
            });
        }

        /**
         * 判断指定参数的构造函数是否正确
         *
         * @return
         */
        public boolean check(@NotNull HashMap<String, Object> args) {
            final Set<String> tmp = args.keySet();
            if (tmp.size() != num) {
                return false;
            }
            return tmp.containsAll(map.keySet());
        }

        /**
         * 解析对应的参数创建对象
         *
         * @param args
         * @return
         * @throws InvocationTargetException
         * @throws InstantiationException
         * @throws IllegalAccessException
         */
        public Object parseObject(HashMap<String, Object> args) throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (!check(args)) {
                return null;
            }
            Object[] argObj = new Object[num];
            for (int i = 0; i < num; i++) {
                argObj[i] = params[i].parseParam(args.get(params[i].getName()));
            }
            return constructor.newInstance(argObj);
        }
    }

    /**
     * 参数解析如果该参数不是java类则需要相应的构造器
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    static class Param {
        private Parameter param;
        private String name;
        private Boolean isJava;
        private Boolean isBasic;
        private Class<?> aClass;
        private ClassReslover constructosCls;
        private boolean constructEqNull = true;
        private boolean finish = false;

        @Contract(pure = true)
        public Param(@NotNull Parameter param) {
            name = param.getName();
            this.param = param;
            aClass = param.getType();
            isJava = Convert.isJava(aClass.getName());
            isBasic = Convert.basic(aClass.getName());
            getCon();
        }

        /**
         * 设置ConstructosCls
         */
        @Contract(pure = true)
        private void getCon() {
            if (isJava) {
                finish = true;
                return;
            }
            constructosCls = ConstructCache.get(aClass.getName());
            constructEqNull = constructosCls == null;
            if (!finish && constructEqNull) {
                finish = true;
                return;
            }
            /*
            如果超过最大重试次数则报错
             */
            if (finish && constructEqNull) {
                throw new RuntimeException("No such param Class Constructor");
            }
        }

        /**
         * 解析参数
         *
         * @return
         */
        public Object parseParam(Object param) throws InvocationTargetException, InstantiationException, IllegalAccessException {
            if (isJava) {
                return isBasic ? Convert.ConvertObj(param, aClass.getName()) : aClass.cast(param);
            } else {
                return constructosCls.parseObj((HashMap<String, Object>) param);
            }
        }

        /**
         * 解析素组对象
         *
         * @param param
         * @return
         */
        @Deprecated
        private Object getArray(Object param) {
            return Convert.ConvertArr(param, aClass);
        }
    }
}





















