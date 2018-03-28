package com.todo.dataprovider.http;

import okhttp3.OkHttpClient;

/**
 *
 * @author TCG
 * @date 2018/3/28
 */

public class ToDoHttpClient {

    private OkHttpClient okHttp = new OkHttpClient();

    private static ToDoHttpClient instance = null;

    public static ToDoHttpClient i() {
        if (instance == null) {
            synchronized (ToDoHttpClient.class) {
                if (instance == null) {
                    instance = new ToDoHttpClient();
                }
            }
        }
        return instance;
    }

}
