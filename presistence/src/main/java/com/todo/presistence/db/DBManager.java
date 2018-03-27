package com.todo.presistence.db;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.util.Log;

import com.todo.presistence.db.annotation.DBColumn;
import com.todo.presistence.db.annotation.SignTable;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dalvik.system.DexFile;

/**
 * Created by TCG on 2017/7/28.
 * desc:可管理多个数据库，支持查询
 */

public class DBManager {
    public static final String closeAll = "close-all-db";
    private final static String DEFAULT_DB = "defaultDB";
    private Context mContext;
    private HashMap<String, DB> dbs = new HashMap<>();

    public DBManager(Context context) {
        mContext = context;
        openInstance(DEFAULT_DB);
    }

    /**
     * 初始化数据库操作DBManager类
     */
    public DBManager openInstance(String dbName) {
        DB db = DB.obtain(dbName);
        db.dbHelper = new DBHelper(mContext, db);
        dbs.put(dbName, db);
        return this;
    }

    /**
     * @param pkg table所处的目录
     */
    public void register(String pkg) {
        Set<String> classInPackage = Util.getClassInPackage(mContext, pkg);
        Class curClass;
        for (String clsName : classInPackage) {
            try {
                curClass = Class.forName(clsName);
                boolean annotationPresent = curClass.isAnnotationPresent(SignTable.class);
                if (annotationPresent) {
                    register(curClass);
                }
            } catch (ClassNotFoundException e) {
                Log.w("register", e);
            }
        }
    }

    /**
     * @param cls 需要注册的class
     */
    public void register(Class<?> cls) {
        SignTable table = cls.getAnnotation(SignTable.class);
        if (table == null) {
            Log.d("register failed,", "do not sign as a table " + cls.getSimpleName());
            return;
        }
        String tableName = table.name();
        List<String> keys = new ArrayList<>();
        Field[] fields = cls.getDeclaredFields();
        for (Field filed : fields) {
            DBColumn column = filed.getAnnotation(DBColumn.class);
            if (column != null) {
                String key = filed.getName();
                String type = column.type();
                keys.add(key);
                keys.add(type);
            }
        }
        String[] keyAndType = new String[keys.size()];
        keyAndType = keys.toArray(keyAndType);
        obtainTable(null, tableName, false, keyAndType);
    }

    /**
     * 关闭数据库
     */
    public void close(String dbName) {
        if (dbName.equals(closeAll)) {
            Collection<DB> values = dbs.values();
            for (DB db : values) {
                db.close();
            }
        } else {
            DB db = dbs.get(dbName);
            if (db != null) {
                db.close();
            }
        }
    }

    /**
     * @param dbName empty means DEFAULT_DB
     */
    public DBOperation obtainTable(String dbName, String tableName, boolean drop, String... keyAndType) {
        if (TextUtils.isEmpty(dbName)) {
            dbName = DEFAULT_DB;
        }
        boolean exist = checkTableExist(dbName, tableName);
        if (exist && drop) {
            dropTable(dbName, tableName);
        }
        DB db = dbs.get(dbName);
        DBOperation operation = DBOperation.i(db.getSQLiteDatabase(),
                tableName);
        Table table = db.tables.get(tableName);
        if (table == null) {
            table = new Table(tableName);
            table.initKeyAndType(keyAndType);
            db.tables.put(tableName, table);
        }
        if (!exist || drop) {
            operation.newTable(table);
        }
        return operation;
    }

    /**
     * @param dbName empty means DEFAULT_DB
     */
    public DBOperation operate(String dbName, String tableName) {
        if (TextUtils.isEmpty(dbName)) {
            dbName = DEFAULT_DB;
        }
        DB db = getDB(dbName);
        if (db == null) {
            Log.e("DBMANAGER ", "operate db error: db do not create");
            return null;
        }
        Table table = db.tables.get(tableName);
        if (table == null) {
            Log.e("DBMANAGER ", "operate:there is no table named as " + tableName);
            return null;
        }
        return DBOperation.i(db.getSQLiteDatabase(),tableName);
    }

    /**
     * @param dbName empty means DEFAULT_DB
     */
    public boolean checkTableExist(String dbName, String tableName) {
        if (TextUtils.isEmpty(dbName)) {
            dbName = DEFAULT_DB;
        }
        DB db = dbs.get(dbName);
        if (db == null) {
            openInstance(dbName);
            return false;
        }
        String sql = "SELECT * FROM " + tableName;
        Cursor cursor = null;
        try {
            cursor = db.getSQLiteDatabase().rawQuery(sql, null);
        } catch (Exception e) {
            cursor = null;
        }
        return cursor != null;
    }

    /**
     * @param dbName empty means DEFAULT_DB
     */
    public void dropTable(String dbName, String tableName) {
        if (TextUtils.isEmpty(dbName)) {
            dbName = DEFAULT_DB;
        }
        DB db = dbs.get(dbName);
        if (db == null) {
            return;
        }
        try {
            String sql = "DROP TABLE " + tableName;
            db.getSQLiteDatabase().execSQL(sql);
        } catch (Exception e) {
        }

        Table table = db.tables.get(tableName);
        if (table != null) {
            db.tables.remove(tableName);
        }
    }

    /**
     * @param dbName empty means DEFAULT_DB
     */
    public void clearTable(String dbName, String tableName) {
        if (TextUtils.isEmpty(dbName)) {
            dbName = DEFAULT_DB;
        }
        DB db = dbs.get(dbName);
        if (db == null) {
            return;
        }
        try {
            db.getSQLiteDatabase().delete(tableName,null,null);
        } catch (Exception e) {
        }
    }

    public DB getDB(String dbName) {
        if (TextUtils.isEmpty(dbName)) {
            dbName = DEFAULT_DB;
        }
        return dbs.get(dbName);
    }

    public Table getTableFromDefaultDB(String tableName) {
        return dbs.get(DEFAULT_DB).tables.get(tableName);
    }

    public Table getTable(String dbName, String tableName) {
        if (TextUtils.isEmpty(dbName)) {
            dbName = DEFAULT_DB;
        }
        DB db = dbs.get(dbName);
        if (db == null) {
            return null;
        }
        return db.tables.get(tableName);
    }

    static class Util {

        /**
         * 获取指定包下的所有类型
         *
         * @param context 当前上下文
         * @param pkgName 指定包完整包名
         * @return 包下的类型集合，未找到则类型集合为empty
         */
        public static Set<String> getClassInPackage(Context context, String pkgName) {
            Set<String> classSet = new HashSet<String>();
            DexFile dexFile = null;
            try {
                String packageResourcePath = context.getPackageResourcePath();
                dexFile = new DexFile(packageResourcePath);
                Enumeration<String> classes = dexFile.entries();
                String classPath;
                while (classes.hasMoreElements()) {
                    classPath = classes.nextElement();
                    if (classPath.startsWith(pkgName)) {
                        // 编译后的类型以$分隔
                        classSet.add(classPath.split("\\$")[0]);
                    }
                }
            } catch (IOException e) {
                Log.w("getClassInPackage ", "Parse dex file failed!", e);
            } finally {
                if (dexFile != null) {
                    try {
                        dexFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return classSet;
        }
    }
}
