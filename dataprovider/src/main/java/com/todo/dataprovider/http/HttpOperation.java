package com.todo.dataprovider.http;

import android.util.Log;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataContext;
import com.todo.dataprovider.DataService;
import com.todo.dataprovider.ClauseInfo;
import com.todo.dataprovider.http.filter.HttpFilter;
import com.todo.dataprovider.operate.DataOperation;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author TCG
 * @date 2017/8/9
 */

public class HttpOperation implements DataOperation {

    public static final String HEAD_PATH = "@head_path";

    private DataService.Clause clause;

    public HttpOperation(DataService.Clause clause) {
        this.clause = clause;
    }

    @Override
    public boolean op(DataContext context, DataCallback callback) {
        ClauseInfo clauseInfo = clause.getClauseInfo();
        ClauseInfo.Condition path = clauseInfo.getCondition(HEAD_PATH);

        LinkedHashMap<String, ClauseInfo.Condition> conditions =
                new LinkedHashMap<>(clauseInfo.conditions);
        conditions.remove(HEAD_PATH);

        clauseInfo.conditions = conditions;

        Action action = clause.getAction();
        HttpRequest request = new HttpRequest();
        request.action = action;
        request.path = path.value;

        List<HttpFilter> filters = HttpCommon.getFilters();
        for (HttpFilter filter : filters) {
            filter.onRequest(request, clause, context, callback);
        }
        String url = request.url();
        Log.d("tang", "op: ---------"+url);
        switch (action) {
            case HTTP_GET:
                ToDoHttpClient.i().get(url, context, callback, clause);
                return true;
            case HTTP_POST:
                ToDoHttpClient.i().post(url, context, callback, clause);
                return true;
            default:
                //do nothing
                return false;
        }
    }


}
