package com.todo.dataprovider.annotation;

import com.todo.dataprovider.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by TCG on 2018/3/28.
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProviderRegister {

    /**
     * Expected response type.
     */
    String target();

    /**
     * The actions this provider supported.
     */
    Action[] action() default {Action.QUERY, Action.DELETE, Action.INSERT,
            Action.UPDATE,Action.HTTP_GET,Action.HTTP_POST};

}
