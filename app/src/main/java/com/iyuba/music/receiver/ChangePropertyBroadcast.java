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
    public static final String FLAG = "changeProperty";
    public static final String SOURCE = "source";
    public static final String RESULT_FLAG = "cp_result";

    @Override
    public void onReceive(Context context, Intent intent) {
        ((MusicApplication) RuntimeManager.getApplication()).clearActivityList();
        Intent target = new Intent();
        target.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        target.putExtra(RESULT_FLAG, true);
        String source = intent.getStringExtra(SOURCE);
        if (TextUtils.isEmpty(source)) {
            target.setClass(context, MainActivity.class);
            context.startActivity(target);
        } else if (source.contains("SettingActivity")) {
            target.setClass(context, SettingActivity.class);
            context.startActivity(target);
        } else if (source.contains("StudyActivity")) {
            target.setClass(context, StudyActivity.class);
            context.startActivity(target);
        } else {
            target.setClass(context, MainActivity.class);
            context.startActivity(target);
        }
    }
}