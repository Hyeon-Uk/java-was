package codesquad.framework.coffee.annotation;

import codesquad.was.http.message.request.HttpMethod;
import codesquad.was.http.message.response.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {
    String path() default "";
    HttpMethod method() default HttpMethod.GET;
    HttpStatus status() default HttpStatus.OK;
}
