package com.iyuba.music.widget;

import android.widget.Toast;

import com.iyuba.music.manager.RuntimeManager;


/**
 * 重载后toast 可同时触发
 */
public enum CustomToast {
    // enum 维持一个单例
    INSTANCE;
    public static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    public static final int LENGTH_LONG = Toast.LENGTH_LONG;
    private Toast mToast;

    public void showToast(String text) {
        showToast(text, LENGTH_SHORT);
    }

    public void showToast(int resId, int duration) {
        showToast(RuntimeManager.getString(resId), duration);
    }

    public void showToast(int resId) {
        showToast(RuntimeManager.getString(resId), LENGTH_SHORT);
    }

    public void showToast(String text, int duration) {
        if (mToast != null) {
            mToast.setText(text);
            mToast.setDuration(duration);
        } else {
            mToast = Toast.makeText(RuntimeManager.getContext(), text, duration);
        }
        mToast.show();
    }
}
