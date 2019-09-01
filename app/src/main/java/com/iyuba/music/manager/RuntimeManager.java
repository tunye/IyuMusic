package com.iyuba.music.manager;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.buaa.ct.videocache.HttpProxyCacheServer;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.util.Utils;

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

    public void initRuntimeManager(MusicApplication application) {
        this.application = application;
        context = application.getApplicationContext();
        displayMetrics = application.getResources().getDisplayMetrics();
    }

    public MusicApplication getApplication() {
        return application;
    }

    public Context getContext() {
        return context;
    }

    public String getString(int resourcesID) {
        return getContext().getString(resourcesID);
    }

    public HttpProxyCacheServer getProxy() {
        return application.getProxy();
    }

    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public int getWindowWidth() {
        return getDisplayMetrics().widthPixels;
    }

    public int getWindowHeight() {
        return getDisplayMetrics().heightPixels;
    }

    public int dip2px(float dpValue) {
        float scale = getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5F);
    }

    public int px2dip(float pxValue) {
        float scale = getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5F);
    }

    public int px2sp(float pxValue) {
        float fontScale = getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5F);
    }

    public int sp2px(float spValue) {
        float fontScale = getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5F);
    }

    public String newPhoneDiviceId() {
        String uuid = Build.SERIAL;
        if (TextUtils.isEmpty(uuid)) {
            return Utils.getIMEI();
        }
        return uuid;
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
