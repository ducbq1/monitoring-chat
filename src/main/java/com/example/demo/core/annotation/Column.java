package com.example.demo.core.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    String name();
    String title() default "";
    int length() default -1;
    ColumnType type() default ColumnType.AUTO;
    boolean readonly() default false;
    boolean required() default false;
}
