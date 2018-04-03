package com.todo.dataprovider.http.filter;

import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataContext;
import com.todo.dataprovider.DataService;
import com.todo.dataprovider.http.HttpRequest;

import okhttp3.Response;

/**
 *
 * @author TCG
 * @date 2018/4/3
 */

public class HttpCache implements HttpFilter {

    @Override
    public boolean onRequest(HttpRequest request, DataService.Clause clause,
                             DataContext context, DataCallback callback) {
        return false;
    }

    @Override
    public boolean onResponse(Response response, DataService.Clause clause,
                              DataContext context, DataCallback callback) {
        return false;
    }
}
