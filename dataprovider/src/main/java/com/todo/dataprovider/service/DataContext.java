package com.todo.dataprovider.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.todo.dataprovider.DataCallback;
import com.todo.dataprovider.operate.DataOperation;

import java.util.WeakHashMap;

/**
 * @author TCG
 * @date 2018/4/2
 * Control and deliver op.
 */

public class DataContext {

    private Handler mHandler = null;
    private volatile boolean destroyed = false;
    private WeakHashMap<DataOperation, Object> operations = new WeakHashMap<>(8);

    DataContext() {
        mHandler = new Handler(ProviderManager.getMainLooper());
    }

    synchronized void bind(DataOperation op) {
        if (destroyed) {
            operations = new WeakHashMap<>(8);
        }
        operations.put(op, null);
    }

    synchronized void unbind(DataOperation op) {
        if (destroyed) {
            return;
        }
        operations.remove(op);
    }

    synchronized void destory() {
        destroyed = true;
        if (operations != null) {
            operations.clear();
        }
        mHandler.removeCallbacksAndMessages(null);
    }

    public synchronized void deliverResult(final DataCallback callback,
                                           final Clause clause, final Object data) {
        if (callback == null || destroyed) {
            Log.i("data-service-ctx",
                    "deliverResult callback is null or has been destroyed. ");
            return;
        }
        unbind(clause.getOp());
        if (isMainThread()) {
            callback.onResult(DataCallback.SUCCESS, clause, callback.tyrCast(data));
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (destroyed){
                        return;
                    }
                    callback.onResult(DataCallback.SUCCESS, clause, callback.tyrCast(data));
                }
            });
        }
    }

    public synchronized void deliverError(final DataCallback callback,
                                           final Clause clause) {
        if (callback == null || destroyed) {
            Log.i("data-service-ctx",
                    "deliverError callback is null or has been destroyed. ");
            return;
        }
        unbind(clause.getOp());
        if (isMainThread()) {
            callback.onError(DataCallback.SUCCESS, clause);
        } else {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (destroyed){
                        return;
                    }
                    callback.onError(DataCallback.SUCCESS, clause);
                }
            });
        }
    }

    private boolean isMainThread() {
        Looper mainLooper = ProviderManager.getMainLooper();
        return mainLooper != null && mainLooper.getThread().getId() == Thread.currentThread().getId();
    }
}
