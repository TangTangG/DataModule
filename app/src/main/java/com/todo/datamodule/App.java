package com.todo.datamodule;

import android.app.Application;

import com.todo.dataprovider.service.ProviderManager;
import com.todo.dataprovider.http.HttpCommon;

import java.util.ArrayList;

/**
 * Created by TCG on 2018/4/2.
 */

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        ProviderManager.autoRegister(this, "");
        HttpCommon.initial("172.31.14.249", 8080
                , new ArrayList<Class<?>>(8));
    }
}
