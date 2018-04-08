package com.todo.dataprovider.http;

import android.support.annotation.NonNull;

import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.DataContext;
import com.todo.dataprovider.DataService;
import com.todo.dataprovider.ClauseInfo;
import com.todo.dataprovider.http.filter.HttpFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;

/**
 * @author TCG
 * @date 2018/3/28
 * desc:Complete url,provider some common method to application.
 */

public class ToDoHttpClient {

    private final OkHttpClient mOkHttp;

    private static ToDoHttpClient instance = null;

    private final Map<String, List<Cookie>> cookieHolder = new HashMap<>(16);

    private ToDoHttpClient() {
        //cookie enabled
        mOkHttp = new OkHttpClient.Builder().cookieJar(new CookieJar() {
            @Override
            public void saveFromResponse(@NonNull HttpUrl url, @NonNull List<Cookie> cookies) {
                cookieHolder.put(url.host(), cookies);
            }

            @Override
            public List<Cookie> loadForRequest(@NonNull HttpUrl url) {
                List<Cookie> cookies = cookieHolder.get(url.host());
                return cookies == null ? new ArrayList<Cookie>() : cookies;
            }
        }).build();
    }

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

    /**
     * Post async
     */
    void post(String url, final DataContext context, final DataCallback callback,
              final DataService.Clause clause) {

        final ClauseInfo info = clause.getClauseInfo();

        Request.Builder requestBuilder = new Request.Builder();
        requestBuilder.url(url);

        final FormBody.Builder bodyBuilder = new FormBody.Builder();
        LinkedHashMap<String, ClauseInfo.Condition> conditions = new LinkedHashMap<>(info.conditions);
        Set<Map.Entry<String, ClauseInfo.Condition>> entries = conditions.entrySet();
        for (Map.Entry<String, ClauseInfo.Condition> entry :
                entries) {
            String s = entry.getKey();
            ClauseInfo.Condition condition = entry.getValue();
            bodyBuilder.add(s, condition.value);
        }
        Request request = requestBuilder
                .post(bodyBuilder.build())
                .build();

        mOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                deliverFail(context, -1, clause, callback);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    deliverSuccess(context, response, callback, clause);
                } else {
                    deliverFail(context, response.code(), clause, callback);
                }
            }
        });
    }

    /**
     * Get async
     */
    void get(String url, final DataContext context, final DataCallback callback,
             final DataService.Clause clause) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        mOkHttp.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                deliverFail(context, -1, clause, callback);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    deliverSuccess(context, response, callback, clause);
                } else {
                    deliverFail(context, response.code(), clause, callback);
                }
            }
        });
    }

    private void deliverFail(DataContext context, int code, final DataService.Clause clause,
                             final DataCallback callback) {
        clause.setMsg(httpMessageFromCode(code));
        context.deliverError(callback, clause);
    }

    private void deliverSuccess( DataContext context, Response response, final DataCallback callback,
                                final DataService.Clause clause) {
        if (callback == null) {
            return;
        }
        if (!HttpHeaders.hasBody(response)){
            clause.setMsg("response body is empty.end http");
            context.deliverError(callback,clause);
        } else if (bodyEncoded(response.headers())){
            clause.setMsg("encoded body omitted");
            context.deliverError(callback,clause);
        } else {
            List<HttpFilter> filters = HttpCommon.getFilters();
            for (HttpFilter filter : filters) {
                filter.onResponse(response, clause, context, callback);
            }
            try {
                ResponseBody body = response.body();
                String string = body.string();
                clause.setMsg(httpMessageFromCode(response.code()));
                context.deliverResult(callback, clause, string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean bodyEncoded(Headers headers) {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private String httpMessageFromCode(int code) {
        switch (code) {
            case 200:
                return "OK";
            case 401:
                return "Unauthorized";
            case 403:
                return "Forbidden";
            case 404:
                return "Not Found";
            case 301:
                return "Moved Permanently";
            case 302:
                return "Redirect";
            case 304:
                return "Not Modified";
            case 500:
                return "Internal Server Error";
            case 501:
                return "Not implemented";
            case 502:
                return "Proxy Error";
            case 100:
                return "Continue";
            default:
                return "other reasons";
        }
    }

}
