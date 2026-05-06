package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.annotations.BeforeInvocation;
import io.github.libxposed.api.annotations.XposedHooker;

public class MyModule extends XposedModule {

    // Modern API 构造函数不再接收 XposedInterface
    public MyModule(ModuleContext context) {
        super(context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // 排除自身和无关包
        if (lp.isSystem() || lp.getPackageName().equals("android")) return;

        try {
            // Modern API 获取 ClassLoader 的方式
            ClassLoader loader = lp.getClassLoader();
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 使用 Modern API 的 Hook 方式
            hook(targetMethod, MyHooker.class);

        } catch (Exception e) {
            // 记录日志或忽略
        }
    }

    // 定义拦截器类
    @XposedHooker
    public static class MyHooker implements io.github.libxposed.api.XposedInterface.Hooker {
        @BeforeInvocation
        public static void before(io.github.libxposed.api.XposedInterface.BeforeHookerCallback callback) {
            // 拦截并返回 false
            callback.setInterception(false);
        }
    }
}
