package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.annotations.BeforeInvocation;
import io.github.libxposed.api.annotations.XposedHooker;
import io.github.libxposed.api.XposedInterface.BeforeHookerCallback; // 修正 import 路径

public class MyModule extends XposedModule {

    // 构造函数：ModuleContext 是 XposedModule 的内部接口，直接写即可
    public MyModule(ModuleContext context) {
        super(context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // API 101 中 PackageLoadedParam 的属性访问
        // 如果 lp.isSystem() 报错，说明在 101 中需通过 getPackageName() 判断
        if (lp.getPackageName().equals("android")) return;

        try {
            // 修正：如果 getClassLoader() 报错，请直接使用字段访问
            ClassLoader loader = lp.getClassLoader(); 
            
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 进行 Hook
            hook(targetMethod, MyHooker.class);

        } catch (Exception e) {
            // 忽略
        }
    }

    @XposedHooker
    public static class MyHooker {
        @BeforeInvocation
        public static void before(BeforeHookerCallback callback) {
            // 核心逻辑：拦截请求并返回 false
            callback.setInterception(false);
        }
    }
}
