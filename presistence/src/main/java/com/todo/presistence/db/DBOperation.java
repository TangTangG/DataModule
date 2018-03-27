package com.todo.presistence.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by TCG on 2017/8/3.
 */

public class DBOperation {

    private SQLiteDatabase database;
    private String tableName;

    private DBOperation(SQLiteDatabase database, String tableName) {
        this.database = database;
        this.tableName = tableName;
    }

    public static DBOperation i(SQLiteDatabase database, String tableName) {
        return new DBOperation(database, tableName);
    }

    public void newTable(Table table) {
        StringBuilder builder = new StringBuilder("create table if not exists ");
        builder.append(table.TABLE_NAME).append("( ").append(table.id)
                .append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        HashMap<String, String> keyAndType = new HashMap<>(table.keyAndType);
        Set<Map.Entry<String, String>> entries = keyAndType.entrySet();
        int index = 0;
        int size = entries.size();
        for (Map.Entry<String, String> entry : entries) {
            index++;
            String key = entry.getKey();
            String type = entry.getValue();
            builder.append(key).append(" ").append(type)
                    .append(index == size ? ")" : " , ");
        }
        database.execSQL(builder.toString());

    }

    /**
     * finally remember close database
     */
    public void close() {
        if (database != null) {
            database.close();
        }
    }

    /**
     * 添加数据
     */
    public DBOperation insert(ContentValues values) {
        database.insert(tableName, null, values);
        return this;
    }


    /**
     * 更新数据
     *
     * @param values
     * @param whereClause
     * @param whereArgs
     */
    public DBOperation update(ContentValues values, String whereClause,
                              String[] whereArgs) {
        database.update(tableName, values, whereClause, whereArgs);
        return this;
    }

    /**
     * 删除数据
     *
     * @param whereClause
     * @param whereArgs
     */
    public DBOperation delete(String whereClause, String[] whereArgs) {
        database.delete(tableName, whereClause, whereArgs);
        return this;
    }

    /**
     * 查询数据
     *
     * @param columns
     * @param selection
     * @param selectionArgs
     * @param groupBy
     * @param having
     * @param orderBy
     * @return
     */
    public Cursor select(String[] columns, String selection,
                         String[] selectionArgs, String groupBy, String having,
                         String orderBy) {
        Cursor cursor = database.query(tableName, columns, selection,
                selectionArgs, groupBy, having, orderBy);
        return cursor;
    }
}
