package com.todo.dataprovider.operate;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataService;
import com.todo.dataprovider.clause.ClauseInfo;
import com.todo.presistence.db.DBManager;
import com.todo.presistence.db.DBOperation;
import com.todo.presistence.db.Table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 *
 * @author TCG
 * @date 2017/8/9
 * USE LIKE THIS:
 * DataService.obtain().act(Action.INSERT, OperateType.DB)
 * .where(DBDataOperation.CAST2DB).is({insert your DBManager Instance})
 * .exec(new DataCallback() {});
 */

public class DBDataOperation implements DataOperation {

    public static final String CAST2DB = "@cast2DBManager";

//    public static final String ACTION_CREATE_TABLE = "@action_create_table";

    /**
     * for db query
     */
    public static final String QUERY_COLUMNS = "@query_columns";
    public static final String QUERY_GROUPBY = "@query_groupby";
    public static final String QUERY_HAVING = "@query_having";
    public static final String QUERY_ORDERBY = "@query_orderby";

    /**
     * for update
     */
    public static final String UPDATE_SET = "@update_set";
    public static final String UPDATE_WHERE = "@update_where";

    public static final String DB_NAME = "@dbName";
    public static final String TABLE_NAME = "@tableName";
    private DataService.Clause clause;

    public DBDataOperation(DataService.Clause clause) {
        this.clause = clause;
    }

    @Override
    public boolean op(DataCallback callback) {
        ClauseInfo clauseInfo = clause.getClauseInfo();
        ClauseInfo.Condition condition = clauseInfo.getCondition(CAST2DB);
        if (condition == null) {
            Log.d("DBDataOperation ", "There is a DBManager request here");
            return false;
        }
        Object obj = condition.obj;
        DBManager dbManager = null;
        if (obj != null && obj instanceof DBManager) {
            dbManager = (DBManager) obj;
        }
        if (dbManager == null) {
            clause.setMsg("dbManager == null");
            if (callback != null) {
                callback.onError(DataCallback.ERROR, clause);
            }
            return false;
        }
        ClauseInfo.Condition dbName = clauseInfo.getCondition(DB_NAME);
        String db = "";
        if (dbName != null) {
            db = dbName.value;
        }
        String tableName = clauseInfo.getCondition(TABLE_NAME).value;

        DBOperation operate = dbManager.operate(db, tableName);
        if (operate == null) {
            clause.setMsg("operate == null");
            if (callback != null) {
                callback.onError(DataCallback.ERROR, clause);
            }
            return false;
        }
        //Copy
        LinkedHashMap<String, ClauseInfo.Condition> conditions =
                new LinkedHashMap<>(clauseInfo.conditions);
        conditions.remove(CAST2DB);
        conditions.remove(DB_NAME);
        conditions.remove(TABLE_NAME);
        clauseInfo.conditions = conditions;
        Table table = dbManager.getTable(db, tableName);
        Action action = clause.getAction();
        switch (action) {
            case INSERT:
                return insert(callback, clauseInfo, table, operate);
            case UPDATE:
                return update(callback, clauseInfo, table, operate);
            case QUERY:
                return query(callback, clauseInfo, table, operate);
            case DELETE:
                return delete(callback, clauseInfo, table, operate);
            default:
                return false;
        }


    }

    /**
     * 暂不支持直接输入一个对象插入
     */
    private boolean insert(DataCallback callback, ClauseInfo clauseInfo, Table table, DBOperation operate) {
        ContentValues values = getContentValues(clauseInfo, table);
        operate.insert(values);
        if (callback != null) {
            callback.onResult(DataCallback.SUCCESS, clause, null);
        }
        operate.close();
        return true;
    }

    private boolean update(DataCallback callback, ClauseInfo clauseInfo, Table table, DBOperation operate) {
        final ContentValues values = new ContentValues();
        LinkedHashMap<String, ClauseInfo.Condition> conditions = clauseInfo.getConditions();
        final StringBuffer where = new StringBuffer();

        final List<String> tempArgs = new ArrayList<>();
        conditions.forEach(new BiConsumer<String, ClauseInfo.Condition>() {
            @Override
            public void accept(String s, ClauseInfo.Condition condition) {
                if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(condition.key)) {
                    if (condition.key.contains(UPDATE_SET)) {
                        String realKey = condition.key.replaceAll(UPDATE_SET, "");
                        values.put(realKey, condition.value);
                    } else if (condition.key.contains(UPDATE_WHERE)) {
                        String realKey = condition.key.replaceAll(UPDATE_WHERE, "");
                        where.append(realKey).append("=?");
                        tempArgs.add(condition.value);
                    }
                }
            }
        });
        String[] whereArgs = new String[tempArgs.size()];
        tempArgs.toArray(whereArgs);
        operate.update(values, where.length() > 0 ? where.toString() : null,
                whereArgs.length > 0 ? whereArgs : null);
        if (callback != null) {
            callback.onResult(DataCallback.SUCCESS, clause, null);
        }
        operate.close();
        return true;
    }

    /**
     * 数据库查询的时候，需要按照格式传入，暂未想到更好的方案
     */
    private boolean query(DataCallback callback, ClauseInfo clauseInfo, Table table, DBOperation operate) {

        class QueryParams {
            StringBuffer selection = new StringBuffer();
            String[] columns = null;
            String[] selectionArgs = null;
            String groupBy;
            String having;
            String orderBy;
        }

        final QueryParams queryParams = new QueryParams();
        final List<String> tempArgs = new ArrayList<>();

        LinkedHashMap<String, ClauseInfo.Condition> conditions = clauseInfo.getConditions();
        conditions.forEach(new BiConsumer<String, ClauseInfo.Condition>() {
            @Override
            public void accept(String s, ClauseInfo.Condition condition) {
                if (condition == null) {
                    return;
                }
                switch (s) {
                    case QUERY_COLUMNS:
                        queryParams.columns = condition.values;
                        break;
                    case QUERY_GROUPBY:
                        queryParams.groupBy = condition.value;
                        break;
                    case QUERY_HAVING:
                        queryParams.having = condition.value;
                        break;
                    case QUERY_ORDERBY:
                        queryParams.orderBy = condition.value;
                        break;
                    default:
                        if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(condition.key)) {
                            queryParams.selection.append(condition.key).append("=?");
                            tempArgs.add(condition.value);
                        }
                        break;
                }
            }
        });
        queryParams.selectionArgs = new String[tempArgs.size()];
        tempArgs.toArray(queryParams.selectionArgs);

        Cursor cursor = operate.select(queryParams.columns, queryParams.selection.toString(),
                queryParams.selectionArgs, queryParams.groupBy, queryParams.having, queryParams.orderBy);
        if (callback != null) {
            callback.onResult(DataCallback.SUCCESS, clause, callback.tyrCast(cursor));
        }
        operate.close();
        return true;
    }

    private boolean delete(DataCallback callback, ClauseInfo clauseInfo, Table table, DBOperation operate) {
        LinkedHashMap<String, ClauseInfo.Condition> conditions = clauseInfo.getConditions();
        final StringBuffer where = new StringBuffer();

        final List<String> tempArgs = new ArrayList<>();
        conditions.forEach(new BiConsumer<String, ClauseInfo.Condition>() {
            @Override
            public void accept(String s, ClauseInfo.Condition condition) {
                if (!TextUtils.isEmpty(s) && !TextUtils.isEmpty(condition.key)) {
                    where.append(condition.key).append("=?");
                    tempArgs.add(condition.value);
                }
            }
        });
        String[] whereArgs = new String[tempArgs.size()];
        tempArgs.toArray(whereArgs);
        operate.delete(where.length() > 0 ? where.toString() : null,
                whereArgs.length > 0 ? whereArgs : null);
        if (callback != null) {
            callback.onResult(DataCallback.SUCCESS, clause, null);
        }
        operate.close();
        return true;
    }

    @NonNull
    private ContentValues getContentValues(ClauseInfo clauseInfo, Table table) {
        HashMap<String, String> keyAndType = new HashMap<>(table.keyAndType);
        Set<String> keySet = keyAndType.keySet();
        ContentValues values = new ContentValues();
        for (String key : keySet) {
            ClauseInfo.Condition condition = clauseInfo.getCondition(key);
            if (condition == null) {
                continue;
            }
            if (!TextUtils.isEmpty(condition.value)) {
                values.put(key, condition.value);
            } else if (condition.values != null) {
                //build as string
               /* StringBuffer buffer = new StringBuffer();
                for (:
                     ){

                }*/
            }
        }
        return values;
    }
}
