package com.example.blocker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.annotations.BeforeInvocation;
import io.github.libxposed.api.annotations.XposedHooker;

public class MyModule extends XposedModule {

    public MyModule(XposedInterface base, ModuleContext context) {
        super(base, context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // 排除系统包和常见桌面，避免影响正常系统功能
        if (lp.isSystemPackage() || lp.getPackageName().equals("com.android.launcher3")) {
            return;
        }

        try {
            // 加载目标类
            Class<?> awmClass = lp.getClassLoader().loadClass("android.appwidget.AppWidgetManager");
            
            // Hook requestPinAppWidget 方法
            // 参数列表：ComponentName, Bundle, PendingIntent
            hookBefore(awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class), MyHooker.class);
            
            log("WidgetBlocker: 成功挂钩 " + lp.getPackageName());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // 忽略没有该接口的进程
        }
    }

    @XposedHooker
    public static class MyHooker implements XposedInterface.BeforeHooker {
        @BeforeInvocation
        public static MyInterface.BeforeResult before(XposedInterface.AfterHooker.BeforeParam param) {
            // 核心逻辑：拦截请求并返回 false (表示请求未成功发送)
            // 在 API 101 中，使用 intercept(result) 直接阻止原方法运行
            return MyInterface.BeforeResult.intercept(false);
        }
    }
}
