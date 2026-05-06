package com.example.blocker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import android.util.Log;

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
        // 排除桌面和系统 UI，防止系统功能故障
        if (lp.isSystemPackage() || lp.getPackageName().equals("com.android.launcher3")) {
            return;
        }

        try {
            // 找到 AppWidgetManager 类
            Class<?> awmClass = lp.getClassLoader().loadClass("android.appwidget.AppWidgetManager");
            
            // 使用 LibXposed 的 hook 方法
            hookBefore(awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class), MyHooker.class);
            
            log("成功 Hook " + lp.getPackageName() + " 的小组件请求接口");
        } catch (Exception e) {
            // 针对某些不包含此方法的进程忽略报错
        }
    }

    @XposedHooker
    public static class MyHooker implements XposedInterface.BeforeHooker {
        @BeforeInvocation
        public static MyHooker.BeforeResult before(XposedInterface.AfterHooker.BeforeParam param) {
            // 核心操作：直接拦截并返回 false
            // 这样系统弹窗就不会出现，应用也会收到“失败”反馈
            return MyHooker.BeforeResult.intercept(false);
        }
    }
}
