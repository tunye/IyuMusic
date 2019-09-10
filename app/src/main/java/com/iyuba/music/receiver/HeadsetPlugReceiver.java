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

import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.player.StandardPlayer;

public class HeadsetPlugReceiver extends BroadcastReceiver {

    public HeadsetPlugReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Utils.getMusicApplication().getPlayerService() != null) {
            StandardPlayer player = Utils.getMusicApplication().getPlayerService().getPlayer();
            if (intent.getIntExtra("state", 0) == 1) {
                boolean isAutoPlay = ConfigManager.getInstance().isAutoPlay();
                if (isAutoPlay && player.isPrepared() && !player.isPlaying()) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
            } else if (intent.getIntExtra("state", 0) == 0) {
                boolean isAutoStop = ConfigManager.getInstance().isAutoStop();
                if (isAutoStop && player.isPlaying()) {
                    context.sendBroadcast(new Intent("iyumusic.pause"));
                }
            }
        }
    }
}
