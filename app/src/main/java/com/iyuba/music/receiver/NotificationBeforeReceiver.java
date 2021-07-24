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

import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.service.NotificationUtil;
import com.iyuba.music.util.Utils;

/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class NotificationBeforeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.getMusicApplication().getPlayerService().getCurArticleId() == 0 && !StudyManager.getInstance().getApp().equals("101")) {
            NotificationPauseReceiver.playNewSong();
            if (Utils.getMusicApplication().isAppointForeground("MainActivity")) {
                Intent i = new Intent("com.iyuba.music.main");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            }
            NotificationUtil.getInstance().updatePlayStateNotification(NotificationUtil.PLAY_FLAG);
        } else {
            Utils.getMusicApplication().getPlayerService().before();
            Utils.getMusicApplication().getPlayerService().startPlay(StudyManager.getInstance().getCurArticle(), false);
            Utils.getMusicApplication().getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
            Utils.getMusicApplication().getPlayerService().getPlayer().start();
            if (Utils.getMusicApplication().isAppointForeground("StudyActivity")) {
                Intent i = new Intent("com.iyuba.music.study");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            } else if (Utils.getMusicApplication().isAppointForeground("MainActivity")) {
                Intent i = new Intent("com.iyuba.music.main");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            } else if (Utils.getMusicApplication().isAppointForeground("LocalMusicActivity")) {
                Intent i = new Intent("com.iyuba.music.localmusic");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            }
        }
    }
}
