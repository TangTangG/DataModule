package com.todo.dataprovider.service;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.OperateType;
import com.todo.dataprovider.operate.DBDataOperation;

/**
 * @author TCG
 * @date 2018/4/9
 */

public class DBClause extends Clause {

    public DBClause(DataService mService, OperateType type, String target, Action t) {
        super(mService, type, target, t);
    }

    //-----------------------------
    //          COMMON
    //-----------------------------

    public DBClause tableName(String table) {
        where(DBDataOperation.TABLE_NAME).is(table);
        return this;
    }

    public DBClause dbName(String db) {
        where(DBDataOperation.DB_NAME).is(db);
        return this;
    }

    //-----------------------------
    //          UPDATE
    //-----------------------------

    public DBClause set(String key) {
        clauseInfo.addCondition(DBDataOperation.UPDATE_SET + key);
        return this;
    }

    public DBClause updateWhere(String key) {
        clauseInfo.addCondition(DBDataOperation.UPDATE_WHERE + key);
        return this;
    }

    //-----------------------------
    //          QUERY
    //-----------------------------

    public DBClause columns(String... columns) {
        where(DBDataOperation.QUERY_COLUMNS).is(columns);
        return this;
    }

    public DBClause groupBy(String by) {
        where(DBDataOperation.QUERY_GROUPBY).is(by);
        return this;
    }

    public DBClause having(String having) {
        where(DBDataOperation.QUERY_HAVING).is(having);
        return this;
    }

    public DBClause orderBy(String by) {
        where(DBDataOperation.QUERY_ORDERBY).is(by);
        return this;
    }
}
