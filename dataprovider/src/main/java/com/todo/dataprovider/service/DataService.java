package com.todo.dataprovider.service;


import android.util.Log;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.OperateType;
import com.todo.dataprovider.http.HttpOperation;
import com.todo.dataprovider.operate.DBDataOperation;
import com.todo.dataprovider.operate.DataOperation;
import com.todo.dataprovider.provider.BaseDataProvider;
import com.todo.presistence.db.DBManager;

/**
 * @author TCG
 * @date 2017/8/9
 */

public class DataService {

    private final String default_target = "@GuTang.base";

    private DataContext ctx;

    public static DataService obtain() {
        return new DataService();
    }

    /**
     * If use this method create a clause,it will return DBClause that can make it easier to use DB.
     */
    public DBClause db(Action action, DBManager manager) {
        return db(action, manager, default_target);
    }

    public DBClause db(Action action, DBManager manager, String target) {
        DBClause clause = new DBClause(this, OperateType.DB, target, action);
        clause.where(DBDataOperation.CAST2DB).is(manager);
        return clause;
    }

    public Clause http(Action action) {
        return http("", action);
    }

    public Clause http(String path, Action action) {
        Clause clause = act(action, OperateType.HTTP);
        clause.where(HttpOperation.HEAD_PATH).is(path);
        return clause;
    }

    public Clause cache(Action action) {
        return act(action, OperateType.CACHE);
    }

    public Clause act(Action action, OperateType type) {
        return act(action, type, default_target);
    }

    public Clause act(Action action, String target) {
        return act(action, OperateType.OTHER, target);
    }

    public Clause act(Action action, OperateType type, String target) {
        switch (action) {
            case DELETE:
                return new Clause(this, type, target, Action.DELETE);
            case QUERY:
                return new Clause(this, type, target, Action.QUERY);
            case UPDATE:
                return new Clause(this, type, target, Action.UPDATE);
            case INSERT:
                return new Clause(this, type, target, Action.INSERT);
            case HTTP_GET:
                return new Clause(this, type, target, Action.HTTP_GET);
            case HTTP_POST:
                return new Clause(this, type, target, Action.HTTP_POST);
            default:
                return new Clause(this, type, target, Action.QUERY);
        }
    }

    void exec(Clause clause, DataCallback callback) {
        BaseDataProvider provider = ProviderManager.getProvider(clause);
        if (provider == null) {
            Log.d("data-service", "exec: target provider does not exist !");
            return;
        }

        boolean actionState = false;
        Action targetAct = clause.getAction();
        Action[] actions = provider.actions;
        for (Action act : actions) {
            if (act == targetAct) {
                actionState = true;
                break;
            }
        }
        if (!actionState) {
            Log.e("data-service",
                    String.format("exec: target provider do not support this action [%s]."
                            , targetAct));
            return;
        }

        DataOperation dataOperation = provider.dispatchAction(clause, callback);
        if (dataOperation == null) {
            Log.w("data-service",
                    String.format("exec: target provider need support this action [%s]!!!!"
                            , targetAct));
            return;
        }
        clause.setOp(dataOperation);
        DataContext ctx = getCtx();
        ctx.bind(dataOperation);
        dataOperation.op(ctx, callback);
    }

    public DataContext getCtx() {
        if (ctx == null) {
            ctx = new DataContext();
        }
        return ctx;
    }

    public void cancel() {
        if (ctx != null) {
            ctx.destroy();
        }
    }
}
