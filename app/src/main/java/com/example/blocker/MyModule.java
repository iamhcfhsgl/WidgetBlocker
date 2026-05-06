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
        // 排除系统核心和桌面
        String pkg = lp.getPackageName();
        if (pkg.equals("android") || pkg.contains("launcher") || pkg.contains("systemui")) {
            return;
        }

        try {
            // 加载类
            Class<?> awmClass = lp.getClassLoader().loadClass("android.appwidget.AppWidgetManager");
            
            // 获取目标方法
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // API 101 的标准 Hook 写法
            hookBefore(targetMethod, callback -> {
                // 直接拦截并返回 false，阻止小组件添加请求
                callback.setInterception(false);
            });

        } catch (Exception e) {
            // 忽略报错
        }
    }
}
