package com.todo.dataprovider;

import android.database.Cursor;

import com.todo.dataprovider.cast.Cast;
import com.todo.dataprovider.service.Clause;
import com.todo.dataprovider.service.DataService;

import java.io.InputStream;

/**
 * Callback always run in main thread.
 * @author TCG
 * @date 2017/8/9
 */

public class DataCallback {

    public void onResult(int state, Clause clause, Object data){

    }

    public void onError(int state, Clause clause){

    }

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
