package com.todo.dataprovider.annotation;

import com.todo.dataprovider.Action;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by CaiGao on 2018/8/19.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ProviderAction {

    /**
     * The actions this provider supported.
     */
    Action[] action() default {Action.QUERY, Action.DELETE, Action.INSERT,
            Action.UPDATE,Action.HTTP_GET,Action.HTTP_POST};
}
