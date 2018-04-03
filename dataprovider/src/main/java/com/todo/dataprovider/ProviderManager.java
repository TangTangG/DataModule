package com.todo.dataprovider;


import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.todo.dataprovider.Action;
import com.todo.dataprovider.DataService;
import com.todo.dataprovider.annotation.ProviderRegister;
import com.todo.dataprovider.provider.BaseDataProvider;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.DexFile;

/**
 * @author TCG
 * @date 2017/8/9
 */

public class ProviderManager {

    private static final HashMap<String, BaseDataProvider> PROVIDER_HOLDER = new HashMap<>();
    private static Looper mainLooper;

    static BaseDataProvider getProvider(DataService.Clause clause) {
        return PROVIDER_HOLDER.get(clause.getClauseInfo()._target);
    }

    static Looper getMainLooper() {
        return mainLooper;
    }

    /**
     * When app dead,you`d better call this method.
     */
    public static void destory() {
        PROVIDER_HOLDER.clear();
        if (mainLooper != null) {
            mainLooper.quit();
        }
    }

    /**
     * Provider must provide a default constructor.
     */
    public static void autoRegister(Context context, String pkg) {
        final String tag = "provider-manager";

        mainLooper = context.getMainLooper();
        Set<String> inPackage = Util.getClassInPackage(context, pkg);
        inPackage.add("com.todo.dataprovider.provider.DefaultProvider");
        Class<?> cls;
        try {
            for (String clsName : inPackage) {
                cls = Class.forName(clsName);
                if (BaseDataProvider.class.isAssignableFrom(cls)) {
                    Log.d(tag, "autoRegister: " + clsName);
                    ProviderRegister annotation = cls.getAnnotation(ProviderRegister.class);
                    if (annotation != null) {
                        String target = annotation.target();
                        Action[] actions = annotation.action();
                        BaseDataProvider instance = (BaseDataProvider) cls.newInstance();
                        //auto build info
                        Method method = findTargetMethod(cls, "buildInfo");
                        if (method != null) {
                            method.setAccessible(true);
                            method.invoke(instance, target, actions);
                            PROVIDER_HOLDER.put(target, instance);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            Log.e(tag, "autoRegister failed ---- " + e.getClass().getSimpleName());
        } catch (InstantiationException e) {
            Log.e(tag, "autoRegister failed ---- get instance failed");
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            Log.e(tag, "autoRegister failed ---- " + e.getClass().getSimpleName());
        } catch (InvocationTargetException e) {
            Log.e(tag, "autoRegister failed ---- " + e.getClass().getSimpleName());
        }
    }

    private static Method findTargetMethod(Class<?> cls, String target) {
        if (cls == null) {
            return null;
        }
        Method[] methods = cls.getDeclaredMethods();
        for (Method method : methods) {
            if (target.equals(method.getName())) {
                return method;
            }
        }
        return findTargetMethod(cls.getSuperclass(), target);
    }

    static class Util {

        /**
         * 获取指定包下的所有类型
         *
         * @param context 当前上下文
         * @param pkgName 指定包完整包名
         * @return 包下的类型集合，未找到则类型集合为empty
         */
        public static Set<String> getClassInPackage(Context context, String pkgName) {
            Set<String> classSet = new HashSet<String>();
            if (TextUtils.isEmpty(pkgName)) {
                return classSet;
            }
            DexFile dexFile = null;
            try {
                String packageResourcePath = context.getPackageResourcePath();
                dexFile = new DexFile(packageResourcePath);
                Enumeration<String> classes = dexFile.entries();
                String classPath;
                while (classes.hasMoreElements()) {
                    classPath = classes.nextElement();
                    if (classPath.startsWith(pkgName)) {
                        // 编译后的类型以$分隔
                        classSet.add(classPath.split("\\$")[0]);
                    }
                }
            } catch (IOException e) {
                Log.w("getClassInPackage ", "Parse dex file failed!", e);
            } finally {
                if (dexFile != null) {
                    try {
                        dexFile.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return classSet;
        }
    }
}
