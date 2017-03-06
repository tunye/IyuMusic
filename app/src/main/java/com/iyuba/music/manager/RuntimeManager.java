package com.iyuba.music.manager;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

public class RuntimeManager {
    private Application application;
    private DisplayMetrics displayMetrics;
    private Context context;
    private RuntimeManager() {
    }

    public static void initRuntimeManager(Application application) {
        InstanceHelper.instance.application = application;
        InstanceHelper.instance.context = application.getApplicationContext();
        InstanceHelper.instance.displayMetrics = application.getResources().getDisplayMetrics();
    }

    public static Application getApplication() {
        return InstanceHelper.instance.application;
    }

    public static Context getContext() {
        return InstanceHelper.instance.context;
    }

    public static String getString(int resourcesID) {
        return getContext().getString(resourcesID);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return InstanceHelper.instance.displayMetrics;
    }

    public static int getWindowWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public static int getWindowHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public static int dip2px(float dpValue) {
        float scale = getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    public static int px2dip(float pxValue) {
        float scale = getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public static int px2sp(float pxValue) {
        float fontScale = getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5F);
    }

    public static int sp2px(float spValue) {
        float fontScale = getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    private static class InstanceHelper {
        private static RuntimeManager instance = new RuntimeManager();
    }
}
