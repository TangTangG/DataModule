package com.todo.dataprovider.operate;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataService;

/**
 *
 * @author TCG
 * @date 2017/8/9
 */

public class HttpOperation implements DataOperation {

    private DataService.Clause clause;

    public HttpOperation(DataService.Clause clause) {
        this.clause = clause;
    }

    @Override
    public boolean op(DataCallback callback) {
        Action action = clause.getAction();
        switch (action) {
            case HTTP_GET:
                return tryGet(callback);
            case HTTP_POST:
                return tryPost(callback);
            default:
                //do nothing
                return false;
        }
    }

    private boolean tryPost(DataCallback callback) {

        return true;
    }

    private boolean tryGet(DataCallback callback) {

        return true;
    }
}
