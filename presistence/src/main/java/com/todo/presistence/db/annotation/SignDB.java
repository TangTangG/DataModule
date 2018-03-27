package com.todo.presistence.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by TCG on 2017/8/7.
 * desc:Annotation to create a database.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface SignDB {
    /**
     * Name of the DB.
     * @return String.
     */
    String dbName();

    /**
     * Version of this data base.
     * @return int
     */
    int version() default 1;
}
