package com.todo.dataprovider.http.filter;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.ClauseInfo;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataContext;
import com.todo.dataprovider.DataService;
import com.todo.dataprovider.http.HttpRequest;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import okhttp3.Response;

/**
 *
 * @author TCG
 * @date 2018/4/3
 */

public class UrlCompleter implements HttpFilter {

    @Override
    public boolean onRequest(HttpRequest request, DataService.Clause clause,
                             DataContext context, DataCallback callback) {
        if (clause.getAction() == Action.HTTP_POST){
            return false;
        }
        LinkedHashMap<String, ClauseInfo.Condition> conditions = clause.getClauseInfo().conditions;
        Set<Map.Entry<String, ClauseInfo.Condition>> entries = conditions.entrySet();
        for (Map.Entry<String, ClauseInfo.Condition> entry : entries) {
            request.addQuery(entry.getKey(),entry.getValue().value);
        }
        return true;
    }

    @Override
    public boolean onResponse(Response response, DataService.Clause clause,
                              DataContext context, DataCallback callback) {
        return false;
    }
}
