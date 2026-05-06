package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

public class MyModule extends XposedModule {

    // 修正 1: 使用完整的参数声明
    public MyModule(XposedInterface base, XposedModule.ModuleContext context) {
        super(base, context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // 排除干扰包
        String pkg = lp.getPackageName();
        if (pkg.equals("android") || pkg.contains("launcher") || pkg.contains("systemui")) {
            return;
        }

        try {
            // 修正 2: API 101 确保通过 lp 直接获取 ClassLoader
            ClassLoader loader = lp.getClassLoader();
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 修正 3: 使用 API 101 标准的 hookBefore 签名
            hookBefore(targetMethod, callback -> {
                // 设置拦截，不执行原方法并返回 false
                callback.setInterception(false);
            });

        } catch (Exception e) {
            // 保持静默
        }
    }
}
