package com.todo.dataprovider.service;


import android.content.Context;
import android.os.Looper;

import com.todo.autocollect.AutoCollect;
import com.todo.autocollect.annotation.Collector;
import com.todo.autocollect.annotation.ProviderRegister;
import com.todo.dataprovider.Action;
import com.todo.dataprovider.annotation.ProviderAction;
import com.todo.dataprovider.provider.BaseDataProvider;

import java.util.HashMap;

/**
 * @author TCG
 * @date 2017/8/9
 */

public class ProviderManager {

    @Collector(ProviderRegister.class)
    private final HashMap<String, BaseDataProvider> PROVIDER_HOLDER = new HashMap<>();
    private static Looper mainLooper;

    private ProviderManager() {
        AutoCollect.begin(this);
    }

    private BaseDataProvider find(String target) {
        BaseDataProvider provider = PROVIDER_HOLDER.get(target);
        if (provider == null) {
            return null;
        }
        ProviderAction action = provider.getClass().getAnnotation(ProviderAction.class);
        Action[] actions;
        if (action == null) {
            actions = new Action[]{Action.QUERY, Action.DELETE, Action.INSERT,
                    Action.UPDATE, Action.HTTP_GET, Action.HTTP_POST};
        } else {
            actions = action.action();
        }
        provider.inject(actions);
        return provider;
    }

    private static class Instance {
        private static final ProviderManager I = new ProviderManager();
    }

    private static ProviderManager manger() {
        return Instance.I;
    }

    static BaseDataProvider getProvider(Clause clause) {
        return manger().find(clause.getClauseInfo()._target);
    }

    public static void init(Context context) {
        mainLooper = context.getMainLooper();
    }

    static Looper getMainLooper() {
        return mainLooper;
    }

    /**
     * When app dead,you`d better call this method.
     */
    public void destory() {
        PROVIDER_HOLDER.clear();
        if (mainLooper != null) {
            mainLooper.quit();
        }
    }
}
