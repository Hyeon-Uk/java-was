package codesquad.framework.coffee.annotation;

import java.lang.annotation.*;

@Retention(value= RetentionPolicy.RUNTIME)
@Target(value= ElementType.TYPE)
public @interface Coffee {
    String name() default "";
}
