package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;

public class MyModule extends XposedModule {

    // 修正 1: ModuleContext 实际上在 XposedInterface 里面
    public MyModule(XposedInterface base, XposedInterface.ModuleContext context) {
        super(base, context);
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam lp) {
        // 排除系统核心和桌面，防止循环 Hook 或性能损耗
        String pkg = lp.getPackageName();
        if (pkg.equals("android") || pkg.contains("launcher") || pkg.contains("systemui")) {
            return;
        }

        try {
            // 修正 2: 如果 getClassLoader() 找不到，直接尝试 getClassLoader 属性
            // 某些版本的 API 101 将其作为公共成员暴露
            ClassLoader loader = lp.getClassLoader(); 
            
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            
            // 目标方法：请求固定小组件
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 修正 3: API 101 的 hookBefore 会映射到具体的回调接口
            // 我们显式使用 XposedInterface 内部的回调定义
            this.hookBefore(targetMethod, new XposedInterface.BeforeHookCallback() {
                @Override
                public void onBeforeInvocation(XposedInterface.BeforeHookerCallback callback) {
                    // 核心逻辑：拦截请求，返回 false (表示失败/拒绝)
                    callback.setInterception(false);
                }
            });

        } catch (Exception e) {
            // 静默处理反射异常
        }
    }
}
