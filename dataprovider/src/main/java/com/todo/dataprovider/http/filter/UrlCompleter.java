package com.todo.dataprovider.http.filter;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.service.Clause;
import com.todo.dataprovider.service.ClauseInfo;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.service.DataContext;
import com.todo.dataprovider.service.DataService;
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
    public boolean onRequest(HttpRequest request, Clause clause,
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
    public boolean onResponse(Response response, Clause clause,
                              DataContext context, DataCallback callback) {
        return false;
    }
}
