package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

public class MyModule extends XposedModule {

    // 修正 1：ModuleContext 必须指明是 XposedModule 里的内部类
    public MyModule(XposedInterface base, XposedModule.ModuleContext context) {
        super(base, context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // 过滤系统核心
        if (lp.getPackageName().equals("android")) return;

        try {
            // 修正 2：API 101 中 PackageLoadedParam 获取 ClassLoader 的标准写法
            ClassLoader loader = lp.getClassLoader();
            
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 修正 3：由于编译器找不到简写的 hookBefore，我们使用全路径接口
            // 并且显式调用 super 的方法来确保符号绑定
            super.hookBefore(targetMethod, new io.github.libxposed.api.XposedInterface.BeforeHookCallback() {
                @Override
                public void onBeforeInvocation(io.github.libxposed.api.XposedInterface.BeforeHookerCallback callback) {
                    // 拦截请求并返回 false (阻止弹出确认框)
                    callback.setInterception(false);
                }
            });

        } catch (Throwable t) {
            // 使用 Throwable 捕获所有反射或链接错误，防止模块崩溃
        }
    }
}
