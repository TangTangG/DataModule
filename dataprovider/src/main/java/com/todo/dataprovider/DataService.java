package com.todo.dataprovider;


import android.util.Log;

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

    private DataContext ctx;

    public static DataService obtain() {
        return new DataService();
    }

    public class Clause {

        private String msg = "";
        private Action action;
        private DataOperation op;

        protected ClauseInfo clauseInfo = null;

        public Clause(OperateType type, String target, Action t) {
            this.action = t;
            clauseInfo = new ClauseInfo(type, target);
        }

        DataOperation getOp() {
            return op;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public ClauseInfo getClauseInfo() {
            return clauseInfo;
        }

        public Action getAction() {
            return action;
        }

        public void exec(DataCallback callback) {
            DataService.this.exec(this, callback);
        }

        public Clause where(String key) {
            clauseInfo.addCondition(key);
            return this;
        }

        public Clause is(Object val) {
            if (isBaseType(val)) {
                String valStr = String.valueOf(val);
                ClauseInfo.Condition condition = clauseInfo.getInitCondition();
                if (condition != null) {
                    condition.is(valStr);
                }
            } else {
                ClauseInfo.Condition condition = clauseInfo.getInitCondition();
                if (condition != null) {
                    condition.is(val);
                }
            }
            return this;
        }

        public Clause is(String... val) {
            ClauseInfo.Condition condition = clauseInfo.getInitCondition();
            if (condition != null) {
                condition.is(val);
            }

            return this;
        }

        private boolean isBaseType(Object val) {
            return val instanceof Integer || val instanceof Long || val instanceof String
                    || val instanceof Boolean || val instanceof Double || val instanceof Byte
                    || val instanceof Float || val instanceof Short || val instanceof Character;
        }

    }

    public Clause db(Action action,DBManager manager) {
        Clause clause = act(action, OperateType.DB);
        clause.where(DBDataOperation.CAST2DB).is(manager);
        return clause;
    }

    public Clause http(Action action) {
        return http("",action);
    }

    public Clause http(String path,Action action) {
        Clause clause = act(action, OperateType.HTTP);
        clause.where(HttpOperation.HEAD_PATH).is(path);
        return clause;
    }

    public Clause cache(Action action) {
        return act(action, OperateType.CACHE);
    }

    public Clause act(Action action, OperateType type) {
        return act(action, type, "@GuTang.base");
    }

    public Clause act(Action action, String target) {
        return act(action, OperateType.OTHER, target);
    }

    public Clause act(Action action, OperateType type, String target) {
        switch (action) {
            case DELETE:
                return new Clause(type, target, Action.DELETE);
            case QUERY:
                return new Clause(type, target, Action.QUERY);
            case UPDATE:
                return new Clause(type, target, Action.UPDATE);
            case INSERT:
                return new Clause(type, target, Action.INSERT);
            case HTTP_GET:
                return new Clause(type, target, Action.HTTP_GET);
            case HTTP_POST:
                return new Clause(type, target, Action.HTTP_POST);
            default:
                return new Clause(type, target, Action.QUERY);
        }
    }

    private void exec(Clause clause, DataCallback callback) {
        BaseDataProvider provider = ProviderManager.getProvider(clause);
        if (provider == null) {
            Log.d("data-service", "exec: target provider does not exist !");
            return;
        }

        boolean actionState = false;
        Action targetAct = clause.action;
        BaseDataProvider.Info info = provider.getInfo();
        Action[] actions = info.actions;
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
        clause.op = dataOperation;
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
            ctx.destory();
        }
    }
}
