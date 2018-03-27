package com.todo.presistence.db;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by TCG on 2017/7/28.
 */

public class Table {
    public String id;
    public String TABLE_NAME;
    public HashMap<String, String> keyAndType = new HashMap<>();

    public Table(String tableName) {
        this.id = "id";
        this.TABLE_NAME = tableName;
    }

    public Table(String id, String tableName) {
        this.id = id;
        this.TABLE_NAME = tableName;
    }

    public void initKeyAndType(String... keyAndTypes) {
        int length = keyAndTypes.length;
        if (length < 2) {
            Log.d("table ", "initKeyAndType keyAndTypes has problem");
            return;
        }
        int i = 0;
        while (i < length) {
            String key = keyAndTypes[i];
            if (TextUtils.isEmpty(key)) {
                i = i + 2;
                continue;
            }
            if (i + 1 >= length) {
                break;
            }
            String type = keyAndTypes[i + 1];
            keyAndType.put(key, type);
            i = i + 2;
        }
    }

    public ContentValues transformValues(Object... value) {
        ContentValues values = new ContentValues();
        Object[] keys =  keyAndType.keySet().toArray();
        int size = keys.length;
        int length = value.length;
        for (int i = 0; i < size; i++) {
            String key = (String) keys[i];
            Object val = i < length ? value[i] : "";
            if (val instanceof String) {
                values.put(key, (String) val);
            } else if (val instanceof Boolean) {
                values.put(key, (Boolean) val);
            } else if (val instanceof Byte) {
                values.put(key, (Byte) val);
            } else if (val instanceof Short) {
                values.put(key, (Short) val);
            } else if (val instanceof Integer) {
                values.put(key, (Integer) val);
            } else if (val instanceof Long) {
                values.put(key, (Long) val);
            } else if (val instanceof Float) {
                values.put(key, (Float) val);
            } else if (val instanceof Double) {
                values.put(key, (Double) val);
            } else if (val instanceof byte[]) {
                values.put(key, (byte[]) val);
            }
        }
        return values;
    }
}
