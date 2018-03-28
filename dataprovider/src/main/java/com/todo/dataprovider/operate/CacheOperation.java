package com.todo.dataprovider.operate;


import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataService;

/**
 *
 * @author TCG
 * @date 2017/8/9
 */

public class CacheOperation implements DataOperation {

    private DataService.Clause clause;

    public CacheOperation(DataService.Clause clause) {
        this.clause = clause;
    }

    @Override
    public boolean op(DataCallback callback) {
        Action action = clause.getAction();
        switch (action) {
            case INSERT:
                return insert(callback);
            case UPDATE:
                return update(callback);
            case QUERY:
                return query(callback);
            case DELETE:
                return delete(callback);
        }
        return false;
    }

    private boolean insert(DataCallback callback) {
        return false;
    }

    private boolean update(DataCallback callback) {
        return false;
    }

    private boolean query(DataCallback callback) {
        return false;
    }

    private boolean delete(DataCallback callback) {
        return false;
    }


}
