package com.todo.dataprovider.cast;

import android.database.Cursor;

import java.io.InputStream;

/**
 *
 * @author TCG
 * @date 2018/3/28
 * description:This provider a way tyrCast data to T.
 */
public class Cast<T> {

    public T to(Cursor cursor){
        return null;
    }

    public T to(InputStream stream){
        return null;
    }

    /**
     * This is for some type I don`t know.
     */
    public T to(Object o){
        return null;
    }
}
