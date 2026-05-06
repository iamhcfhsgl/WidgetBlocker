package com.example.blocker;

import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import java.lang.reflect.Method;

import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.XposedModuleInterface.PackageLoadedParam;

public class MyModule extends XposedModule {

    // 1. 根据你提供的代码，构造函数不需要 base 和 context 参数
    // 框架会自动调用无参构造函数
    public MyModule() {
        super();
    }

    @Override
    public void onPackageLoaded(PackageLoadedParam param) {
        super.onPackageLoaded(param);

        // 排除系统核心，防止 Hook 循环
        if (param.getPackageName().equals("android")) {
            return;
        }

        try {
            // 2. 关键：ClassLoader 的正确名称是 defaultClassLoader
            ClassLoader loader = param.getDefaultClassLoader();
            
            Class<?> awmClass = loader.loadClass("android.appwidget.AppWidgetManager");
            Method targetMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // 3. 关键：使用 Modern API 的拦截器语法
            // hook(method) 返回一个控制对象，通过 intercept 进行拦截
            hook(targetMethod).intercept(chain -> {
                // chain.proceed() 是执行原方法
                // 我们直接返回 false (不调用 chain.proceed())，即表示拒绝该请求
                return false; 
            });

        } catch (Throwable t) {
            // 静默处理所有错误
        }
    }
}
