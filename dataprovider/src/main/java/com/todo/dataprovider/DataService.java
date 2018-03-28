package com.todo.dataprovider;


import android.util.Log;

import com.todo.dataprovider.clause.ClauseInfo;
import com.todo.dataprovider.operate.DataOperation;
import com.todo.dataprovider.provider.BaseDataProvider;
import com.todo.dataprovider.provider.ProviderManager;

/**
 *
 * @author TCG
 * @date 2017/8/9
 */

public class DataService {

    public static DataService obtain() {
        return new DataService();
    }

    public abstract class Clause {

        private String msg = "";

        protected ClauseInfo _clauseInfo = null;

        public Clause(OperateType type, String target) {
            _clauseInfo = new ClauseInfo(type, target);
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public ClauseInfo getClauseInfo() {
            return _clauseInfo;
        }

        public abstract Action getAction();

        public void exec(DataCallback callback) {
            DataService.this.exec(this, callback);
        }

        public Clause where(String key) {
            _clauseInfo.addCondition(key);
            return this;
        }

        public Clause is(Object val) {
            if (isBaseType(val)) {
                String valStr = String.valueOf(val);
                ClauseInfo.Condition condition = _clauseInfo.getInitCondition();
                if (condition != null) {
                    condition.is(valStr);
                }
            } else {
                ClauseInfo.Condition condition = _clauseInfo.getInitCondition();
                if (condition != null) {
                    condition.is(val);
                }
            }
            return this;
        }

        public Clause is(String... val) {
            ClauseInfo.Condition condition = _clauseInfo.getInitCondition();
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

    class InsertClause extends Clause {

        public InsertClause(OperateType type, String target) {
            super(type, target);
        }

        @Override
        public Action getAction() {
            return Action.INSERT;
        }
    }

    class DeleteClause extends Clause {

        public DeleteClause(OperateType type, String target) {
            super(type, target);
        }

        @Override
        public Action getAction() {
            return Action.DELETE;
        }
    }

    class QueryClause extends Clause {

        public QueryClause(OperateType type, String target) {
            super(type, target);
        }

        @Override
        public Action getAction() {
            return Action.QUERY;
        }
    }

    class UpdateClause extends Clause {

        public UpdateClause(OperateType type, String target) {
            super(type, target);
        }

        @Override
        public Action getAction() {
            return Action.UPDATE;
        }
    }

    class HttpGetClause extends Clause {

        public HttpGetClause(OperateType type, String target) {
            super(type, target);
        }

        @Override
        public Action getAction() {
            return Action.HTTP_GET;
        }
    }

    class HttpPostClause extends Clause {

        public HttpPostClause(OperateType type, String target) {
            super(type, target);
        }

        @Override
        public Action getAction() {
            return Action.HTTP_POST;
        }
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
                return new DeleteClause(type, target);
            case QUERY:
                return new QueryClause(type, target);
            case UPDATE:
                return new UpdateClause(type, target);
            case INSERT:
                return new InsertClause(type, target);
            case HTTP_GET:
                return new HttpGetClause(type, target);
            case HTTP_POST:
                return new HttpPostClause(type,target);
            default:
                return new QueryClause(type, target);
        }
    }

    private void exec(Clause clause, DataCallback callback) {
        BaseDataProvider provider = ProviderManager.getProvider(clause);
        if (provider == null) {
            Log.d("data-service", "exec: target provider does not exist !");
            return;
        }
        DataOperation dataOperation = provider.dispatchAction(clause, callback);
        if (dataOperation == null) {
            Log.d("data-service", "exec: target provider do not support this action.");
            return;
        }
        dataOperation.op(callback);
//        provider
    }
}
