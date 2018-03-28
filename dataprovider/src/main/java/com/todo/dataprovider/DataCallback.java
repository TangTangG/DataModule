package com.todo.dataprovider;

import android.database.Cursor;

import com.todo.dataprovider.cast.Cast;

import java.io.InputStream;

/**
 * Created by TCG on 2017/8/9.
 */

public class DataCallback {

    public void onResult(int state, DataService.Clause clause, Object data){

    };

    public void onError(int state, DataService.Clause clause){

    };

    public static final int SUCCESS = 1;
    public static final int ERROR = 2;
    public static final int OTHER = -1;

    private Cast cast;

    public DataCallback() {
    }

    public DataCallback(Cast cast) {
        this.cast = cast;
    }

    public Object tyrCast(Object obj) {
        if (cast == null) {
            return obj;
        }
        if (obj instanceof Cursor) {
            return cast.to((Cursor) obj);
        } else if (obj instanceof InputStream) {
            return cast.to((InputStream) obj);
        } else {
            return cast.to(obj);
        }
    }
}
