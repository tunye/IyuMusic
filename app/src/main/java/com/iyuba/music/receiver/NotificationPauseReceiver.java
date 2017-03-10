/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.service.NotificationUtil;
import com.iyuba.music.widget.player.StandardPlayer;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

/**
 * 类名
 *
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class NotificationPauseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        StandardPlayer player = ((MusicApplication) getApplication()).getPlayerService().getPlayer();
        if (player.isPlaying()) {
            player.pause();
            NotificationUtil.getInstance().updatePlayStateNotification(NotificationUtil.PAUSE_FLAG);
        } else {
            player.start();
            NotificationUtil.getInstance().updatePlayStateNotification(NotificationUtil.PLAY_FLAG);
        }
        Intent i;
        if (((MusicApplication) getApplication()).isAppointForeground("MainActivity")) {
            i = new Intent("com.iyuba.music.main");
            i.putExtra("message", "pause");
            context.sendBroadcast(i);
        } else if (((MusicApplication) getApplication()).isAppointForeground("LocalMusicActivity")) {
            i = new Intent("com.iyuba.music.localmusic");
            i.putExtra("message", "pause");
            context.sendBroadcast(i);
        } else if (((MusicApplication) getApplication()).isAppointForeground("StudyActivity")) {
            i = new Intent("com.iyuba.music.study");
            i.putExtra("message", "pause");
            context.sendBroadcast(i);
        }
    }
}
