package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

public class MyModule extends XposedModule {

    public MyModule(XposedInterface base, ModuleContext context) {
        super(base, context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // 排除系统核心
        if (lp.getPackageName().equals("android")) return;

        try {
            // 获取 ClassLoader
            ClassLoader loader = lp.getClassLoader();
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 直接调用 hookBefore，不使用注解类
            hookBefore(targetMethod, callback -> {
                // 拦截并返回 false
                callback.setInterception(false);
            });

        } catch (Exception e) {
            // 静默
        }
    }
}
