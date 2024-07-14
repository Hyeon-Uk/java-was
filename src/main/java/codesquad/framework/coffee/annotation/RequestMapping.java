package codesquad.framework.coffee.annotation;

import codesquad.was.http.message.request.HttpMethod;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequestMapping {
    String path() default "";
    HttpMethod method() default HttpMethod.GET;
}
