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
import com.iyuba.music.manager.StudyManager;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

/**
 * 类名
 *
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class NotificationBeforeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ((MusicApplication) getApplication()).getPlayerService().before();
        ((MusicApplication) getApplication()).getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
        ((MusicApplication) getApplication()).getPlayerService().setCurArticle(StudyManager.getInstance().getCurArticle().getId());
        ((MusicApplication) getApplication()).getPlayerService().getPlayer().start();
        if (((MusicApplication) getApplication()).isAppointForeground("StudyActivity")) {
            Intent i = new Intent("com.iyuba.music.study");
            i.putExtra("message", "change");
            context.sendBroadcast(i);
        } else if (((MusicApplication) getApplication()).isAppointForeground("MainActivity")) {
            Intent i = new Intent("com.iyuba.music.main");
            i.putExtra("message", "change");
            context.sendBroadcast(i);
        } else if (((MusicApplication) getApplication()).isAppointForeground("LocalMusicActivity")) {
            Intent i = new Intent("com.iyuba.music.localmusic");
            i.putExtra("message", "change");
            context.sendBroadcast(i);
        }
    }
}
