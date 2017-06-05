package com.iyuba.music.manager;

import android.content.Context;
import android.util.DisplayMetrics;

import com.buaa.ct.videocachelibrary.HttpProxyCacheServer;
import com.iyuba.music.MusicApplication;

public class RuntimeManager {
    private MusicApplication application;
    private DisplayMetrics displayMetrics;
    private Context context;
    private boolean showSignInToast;

    private RuntimeManager() {
    }

    public static RuntimeManager getInstance() {
        return InstanceHelper.instance;
    }

    public static void initRuntimeManager(MusicApplication application) {
        InstanceHelper.instance.application = application;
        InstanceHelper.instance.context = application.getApplicationContext();
        InstanceHelper.instance.displayMetrics = application.getResources().getDisplayMetrics();
    }

    public static MusicApplication getApplication() {
        return InstanceHelper.instance.application;
    }

    public static Context getContext() {
        return InstanceHelper.instance.context;
    }

    public static String getString(int resourcesID) {
        return getContext().getString(resourcesID);
    }

    public static HttpProxyCacheServer getProxy() {
        return InstanceHelper.instance.application.getProxy();
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

    public boolean isShowSignInToast() {
        return showSignInToast;
    }

    public void setShowSignInToast(boolean showSignInToast) {
        this.showSignInToast = showSignInToast;
    }

    private static class InstanceHelper {
        private static RuntimeManager instance = new RuntimeManager();
    }
}
