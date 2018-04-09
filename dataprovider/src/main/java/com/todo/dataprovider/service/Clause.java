package com.todo.dataprovider.service;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.OperateType;
import com.todo.dataprovider.operate.DataOperation;

/**
 * @author TCG
 * @date 2018/4/9
 */

public class Clause {

    private String msg = "";
    private Action action;
    private DataOperation op;

    protected ClauseInfo clauseInfo = null;

    private DataService mService;

    public Clause(DataService mService, OperateType type, String target, Action t) {
        this.mService = mService;
        this.action = t;
        clauseInfo = new ClauseInfo(type, target);
    }

    void setOp(DataOperation op) {
        this.op = op;
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
        mService.exec(this, callback);
        mService = null;
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
