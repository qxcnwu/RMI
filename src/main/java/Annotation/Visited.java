package Annotation;

import java.lang.annotation.*;

/**
 * @Author qxc
 * @Date 2022 2022/9/20 14:36
 * @Version 1.0
 * @PACKAGE Annotation
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target(ElementType.METHOD)
@Inherited
public @interface Visited {
}
