package com.iyuba.music.listener;

import android.view.View;

/**
 * Created by chentong1 on 2017/4/26.
 */

public abstract class NoDoubleClickListener implements View.OnClickListener {

    private static final int MIN_CLICK_DELAY_TIME = 500;
    private long lastClickTime = 0;

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime;
            onNoDoubleClick(v);
        }
    }

    public abstract void onNoDoubleClick(View v);
}
