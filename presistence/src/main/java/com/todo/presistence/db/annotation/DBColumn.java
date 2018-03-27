package com.todo.presistence.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by TCG on 2017/8/7.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBColumn {
    /**
     * The data-type of this column.
     * @return String type.
     */
    String type() default "BLOB";
}
