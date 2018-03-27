package com.todo.presistence.db;

/**
 * Created by CaiGao on 2017/8/3.
 */

public final class SQLiteDataType {
    /**
     * 值是一个 NULL 值
     */
    public static final String NULL = "NULL";
    /**
     * 值是一个带符号的整数，根据值的大小存储在 1、2、3、4、6 或 8 字节中
     */
    public static final String INTEGER = "INTEGER";
    /**
     * 值是一个浮点值，存储为 8 字节的 IEEE 浮点数字
     */
    public static final String REAL = "REAL";
    /**
     * 值是一个文本字符串，使用数据库编码（UTF-8、UTF-16BE 或 UTF-16LE）存储
     */
    public static final String TEXT = "TEXT";
    /**
     * 值是一个 blob 数据，完全根据它的输入存储
     */
    public static final String BLOB = "BLOB";

}
