package com.iyuba.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.SettingActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.manager.RuntimeManager;


/**
 * Created by 10202 on 2017/3/7.
 */

public class ChangePropertyBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ((MusicApplication) RuntimeManager.getApplication()).clearActivityList();
        Intent target;
        String source = intent.getStringExtra("source");
        if (TextUtils.isEmpty(source)) {
            target = new Intent(context, MainActivity.class);
            target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(target);
        } else if (source.contains("SettingActivity")) {
            target = new Intent(context, SettingActivity.class);
            target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(target);
        } else if (source.contains("StudyActivity")) {
            target = new Intent(context, StudyActivity.class);
            target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(target);
        } else {
            target = new Intent(context, MainActivity.class);
            target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(target);
        }
    }
}