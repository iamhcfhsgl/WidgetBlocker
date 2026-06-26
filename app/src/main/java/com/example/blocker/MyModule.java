package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam;

public class MyModule extends XposedModule {

    // 规范：必须是无参构造函数，框架会自动调用 attachFramework
    public MainHook() {
        super();
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);
        // API 102 规范：此阶段不允许、也无法获取类加载器
    }

    @Override
    public void onPackageReady(PackageReadyParam param) {
        super.onPackageReady(param);

        // 排除系统核心，防止 Hook 循环死锁
        if (param.getPackageName().equals("android")) return;

        try {
            // API 102 正式规范：从 Ready 阶段安全提取目标进程的 ClassLoader
            ClassLoader loader = param.getClassLoader();
            
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 现代 OkHttp 式拦截器模型
            hook(targetMethod).intercept(chain -> {
                // 不执行 chain.proceed()，直接阻断原本的弹窗/申请逻辑
                // 直接返回 false 拒绝该小组件的添加请求
                return false; 
            });

        } catch (Throwable t) {
            // 保持静默
        }
    }
}
