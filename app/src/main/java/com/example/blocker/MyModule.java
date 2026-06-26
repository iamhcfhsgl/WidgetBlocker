package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;
import io.github.libxposed.api.XposedModuleInterface.PackageReadyParam;

public class MyModule extends XposedModule {

    // 核心修正：构造函数名称必须与类名 MyModule 严格保持一致
    public MyModule() {
        super();
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);
    }

    @Override
    public void onPackageReady(PackageReadyParam param) {
        super.onPackageReady(param);

        if (param.getPackageName().equals("android")) return;

        try {
            ClassLoader loader = param.getClassLoader();
            
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            hook(targetMethod).intercept(chain -> {
                return false; 
            });

        } catch (Throwable t) {
            // 静默处理
        }
    }
}
