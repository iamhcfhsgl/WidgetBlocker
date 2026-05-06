package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

public class MyModule extends XposedModule {

    // 关键修正：在 API 101 中，必须指明 ModuleContext 的完整来源
    public MyModule(XposedInterface base, XposedInterface.ModuleContext context) {
        super(base, context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // 过滤系统关键包，防止造成卡顿
        if (lp.getPackageName().equals("android")) return;

        try {
            // API 101 获取 ClassLoader
            ClassLoader loader = lp.getClassLoader();
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 终极兼容写法：手动实现接口，不使用 Lambda，彻底解决符号推导失败
            hookBefore(targetMethod, new XposedInterface.BeforeHookCallback() {
                @Override
                public void onBeforeInvocation(XposedInterface.BeforeHookerCallback callback) {
                    // 拦截并拒绝小组件添加请求
                    callback.setInterception(false);
                }
            });

        } catch (Throwable t) {
            // 保持静默
        }
    }
}
