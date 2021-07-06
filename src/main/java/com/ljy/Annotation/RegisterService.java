package com.ljy.Annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RegisterService {
    String port() default "80";

    String address() default "";

    Class<?> type() ;
}
