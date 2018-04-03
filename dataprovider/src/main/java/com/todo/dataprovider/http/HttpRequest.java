package com.todo.dataprovider.http;

import android.text.TextUtils;

import com.todo.dataprovider.Action;

import java.net.URLEncoder;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *
 * @author TCG
 * @date 2018/4/3
 */

public class HttpRequest {

    public Action action = Action.HTTP_GET;
    public String schema = "http";
    public String path;

    private LinkedHashMap<String, String> queries;

    @Override
    public String toString() {
        final StringBuilder urlBuilder = new StringBuilder();
        HttpRequest request = this;
        urlBuilder.append(request.schema)
                .append("://");
        request.buildHostString(urlBuilder);
        if (!TextUtils.isEmpty(request.path)) {
            urlBuilder.append(request.path);
        }
        if (action != Action.HTTP_POST &&
                request.queries != null && !request.queries.isEmpty()) {
            urlBuilder.append("?");
            buildQueryString(urlBuilder, request.queries);
        }
        return urlBuilder.toString();
    }

    private StringBuilder buildHostString(StringBuilder urlBuilder) {
        if (!TextUtils.isEmpty(HttpCommon.host)) {
            urlBuilder.append(HttpCommon.host);
        }
        if (HttpCommon.port > 0) {
            urlBuilder.append(":").append(HttpCommon.port);
        }
        return urlBuilder;
    }

    private StringBuilder buildQueryString(StringBuilder urlBuilder, LinkedHashMap<String, String> params) {
        int idx = 0;
        if (params != null) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (idx > 0) {
                    urlBuilder.append("&");
                }
                ++idx;
                urlBuilder.append(URLEncoder.encode(entry.getKey()));
                if (entry.getValue() != null) {
                    urlBuilder.append("=")
                            .append(URLEncoder.encode(entry.getValue()));
                }
            }
        }
        return urlBuilder;
    }

    public String url() {
        return toString();
    }

    public void addQuery(String key, String val) {
        if (queries == null) {
            queries = new LinkedHashMap<>();
        }
        queries.put(key, val);
    }
}
