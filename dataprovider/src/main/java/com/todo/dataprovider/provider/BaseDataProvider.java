package com.todo.dataprovider.provider;


import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.service.Clause;
import com.todo.dataprovider.operate.DataOperation;

/**
 *
 * @author TCG
 * @date 2017/8/9
 */

public class BaseDataProvider implements ProviderInterface {

    public Action[] actions;

    public BaseDataProvider() {

    }

    @Override
    public void inject(Object[] args) {
        if (args == null || args.length <= 0){
            return;
        }
        actions = (Action[]) args;
    }

    public DataOperation dispatchAction(Clause clause, DataCallback callback) {
        Action action = clause.getAction();
        switch (action) {
            case INSERT:
                return insertAction(clause, callback);
            case UPDATE:
                return updateAction(clause, callback);
            case QUERY:
                return queryAction(clause, callback);
            case DELETE:
                return deleteAction(clause, callback);
            default:
                return null;
        }
    }

    protected DataOperation deleteAction(Clause clause, DataCallback callback) {
        return null;
    }

    protected DataOperation queryAction(Clause clause, DataCallback callback) {
        return null;
    }

    protected DataOperation updateAction(Clause clause, DataCallback callback) {
        return null;
    }

    protected DataOperation insertAction(Clause clause, DataCallback callback) {
        return null;
    }
}
