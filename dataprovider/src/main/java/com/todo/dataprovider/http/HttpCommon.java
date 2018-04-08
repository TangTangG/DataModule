package com.todo.dataprovider.http;

import com.todo.dataprovider.http.filter.HttpCache;
import com.todo.dataprovider.http.filter.HttpFilter;
import com.todo.dataprovider.http.filter.UrlCompleter;

import java.util.ArrayList;
import java.util.List;

/**
 * Record common info of the http.
 *
 * @author TCG
 * @date 2018/4/3
 */

public class HttpCommon {

    public static String host;
    public static int port = -1;

    private static List<HttpFilter> filters = new ArrayList<>(8);

    public static void initial(String host, int port, List<Class<?>> classes) {
        HttpCommon.host = host;
        HttpCommon.port = port;
        initialFilters(classes);
    }

    public static void initialFilters(List<Class<?>> classes) {

        classes.add(HttpCache.class);
        classes.add(UrlCompleter.class);

        for (Class<?> cls : classes) {
            if (HttpFilter.class.isAssignableFrom(cls)) {
                try {
                    HttpFilter instance = (HttpFilter) cls.newInstance();
                    filters.add(instance);
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static List<HttpFilter> getFilters() {
        return filters;
    }
}
