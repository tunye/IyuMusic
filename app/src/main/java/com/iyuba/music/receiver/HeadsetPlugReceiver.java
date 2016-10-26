package com.iyuba.music.receiver;

/**
 * 耳机监听广播
 *
 * @author 陈彤
 * @version 1.0
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.widget.player.StandardPlayer;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

public class HeadsetPlugReceiver extends BroadcastReceiver {

    public HeadsetPlugReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (((MusicApplication) getApplication()).getPlayerService() != null) {
            StandardPlayer player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
            if (intent.getIntExtra("state", 0) == 1) {
                boolean isAutoPlay = SettingConfigManager.instance.isAutoPlay();
                if (isAutoPlay && player.isPrepared() && !player.isPlaying()) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
            } else if (intent.getIntExtra("state", 0) == 0) {
                boolean isAutoStop = SettingConfigManager.instance.isAutoStop();
                if (isAutoStop && player.isPlaying()) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
            }
        }
    }
}
