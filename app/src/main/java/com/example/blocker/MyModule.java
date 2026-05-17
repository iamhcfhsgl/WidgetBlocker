package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam;

public class MyModule extends XposedModule {

    // 规则 1：构造函数无参！框架会自动调用 attachFramework
    public MyModule() {
        super();
    }

    // 规则 2：onPackageLoaded 里不要去碰 classloader
    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);
        // 如果不是目标判断逻辑，可以直接在这里拦截一部分，或者留空
    }

    // 规则 3：必须在 onPackageReady 阶段获取 classLoader 并进行 Hook！
    @Override
    public void onPackageReady(PackageReadyParam param) {
        super.onPackageReady(param);

        // 排除系统框架
        if (param.getPackageName().equals("android")) return;

        try {
            // 从 Ready 阶段安全获取 ClassLoader
            ClassLoader loader = param.getClassLoader();
            
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 规则 4：对齐 101 正式版的单参数新版拦截器语法
            hook(targetMethod).intercept(chain -> {
                // 不调用 chain.proceed()，直接返回 false，强制拦截并使其失效
                return false; 
            });

        } catch (Throwable t) {
            // 保持静默
        }
    }
}
