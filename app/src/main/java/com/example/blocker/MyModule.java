package com.example.blocker;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.os.Bundle;
import android.app.PendingIntent;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.Method;

import io.github.libxposed.api.XposedInterface;
import io.github.libxposed.api.XposedModule;
import io.github.libxposed.api.annotations.BeforeInvocation;
import io.github.libxposed.api.annotations.XposedHooker;

public class MyModule extends XposedModule {

    public MyModule(@NonNull XposedInterface base, @NonNull ModuleContext context) {
        super(base, context);
    }

    @Override
    public void onPackageLoaded(@NonNull PackageLoadedParam lp) {
        // API 101 中，lp.isSystemPackage() 改为了属性访问或特定接口，
        // 我们改用通用的包名过滤
        if (lp.getPackageName().equals("android") || 
            lp.getPackageName().contains("launcher") || 
            lp.getPackageName().contains("systemui")) {
            return;
        }

        try {
            // 加载目标类
            Class<?> awmClass = lp.getClassLoader().loadClass("android.appwidget.AppWidgetManager");
            
            // 获取方法
            Method requestMethod = awmClass.getDeclaredMethod("requestPinAppWidget", 
                ComponentName.class, Bundle.class, PendingIntent.class);

            // API 101 使用更严格的泛型 hook 方法
            hookBefore(requestMethod, MyHooker.class);
            
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            // 忽略
        }
    }

    @XposedHooker
    public static class MyHooker implements XposedInterface.Hooker {
        @BeforeInvocation
        public static MyInterface.BeforeResult before(XposedInterface.BeforeParam param) {
            // 核心拦截逻辑：阻止申请弹窗
            return MyInterface.BeforeResult.intercept(false);
        }
    }
}
