package com.todo.presistence.db;

import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;

/**
 * Created by TCG on 2017/8/3.
 */

public class DB {

    public int version;
    public String DB_NAME;
    public DBHelper dbHelper;
    public HashMap<String, Table> tables = new HashMap<>();

    public static DB obtain(String dbName) {
        DB db = new DB();
        db.DB_NAME = dbName;
        db.version = 1;
        return db;
    }

    public SQLiteDatabase getSQLiteDatabase() {
        if (dbHelper == null) {
            throw new NullPointerException("DBHelper can not null");
        }
        return dbHelper.getWritableDatabase();
    }

    public void close() {
        if (dbHelper != null) {
            dbHelper.close();
        }
        dbHelper = null;
    }
}
