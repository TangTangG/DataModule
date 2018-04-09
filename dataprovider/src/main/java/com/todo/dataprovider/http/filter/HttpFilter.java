package com.todo.dataprovider.http.filter;

import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.service.Clause;
import com.todo.dataprovider.service.DataContext;
import com.todo.dataprovider.service.DataService;
import com.todo.dataprovider.http.HttpRequest;

import okhttp3.Response;

/**
 * @author TCG
 * @date 2018/3/28
 */

public interface HttpFilter {

    boolean onRequest(HttpRequest request,Clause clause,
                      DataContext context, DataCallback callback);

    boolean onResponse(Response response, Clause clause,
                       DataContext context, DataCallback callback);

}
