package com.iyuba.music.listener;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by 10202 on 2016/5/17.
 */
public abstract class ChangeUIBroadCast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        refreshUI(intent.getStringExtra("message"));
    }

    public abstract void refreshUI(String message);
}