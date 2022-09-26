package Annotation;

import java.lang.annotation.*;

/**
 * @Author qxc
 * @Date 2022 2022/9/21 23:13
 * @Version 1.0
 * @PACKAGE Annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.TYPE})
@Inherited
public @interface Structs {
}
