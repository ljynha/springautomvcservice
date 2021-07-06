package com.ljy.Annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Import(com.ljy.common.LjyImportRegisterclass.class)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EnableAutowerdService {

}
