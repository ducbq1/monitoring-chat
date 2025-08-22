package com.example.demo.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Metadata {
    String menu();
    String title();
    String icon() default "bi bi-code-slash me-2";
    String[] roles() default {};
    String type() default "list";
}
