package com.todo.presistence.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by TCG on 2017/8/7.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SignTable {
    /**
     * Name of this table.
     * @return String name of the table.
     */
    String name();

    /**
     * Version of this table.Default version is 1.
     * @return int
     */
    int version() default 1;
}