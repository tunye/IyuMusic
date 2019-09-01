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

import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.service.NotificationUtil;

/**
 * 类名
 *
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class NotificationNextReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (RuntimeManager.getInstance().getApplication().getPlayerService().getCurArticleId() == 0 && !StudyManager.getInstance().getApp().equals("101")) {
            NotificationPauseReceiver.playNewSong();
            if (RuntimeManager.getInstance().getApplication().isAppointForeground("MainActivity")) {
                Intent i = new Intent("com.iyuba.music.main");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            }
            NotificationUtil.getInstance().updatePlayStateNotification(NotificationUtil.PLAY_FLAG);
        } else {
            RuntimeManager.getInstance().getApplication().getPlayerService().next(false);
            RuntimeManager.getInstance().getApplication().getPlayerService().startPlay(
                    StudyManager.getInstance().getCurArticle(), false);
            RuntimeManager.getInstance().getApplication().getPlayerService().setCurArticleId(StudyManager.getInstance().getCurArticle().getId());
            RuntimeManager.getInstance().getApplication().getPlayerService().getPlayer().start();
            if (RuntimeManager.getInstance().getApplication().isAppointForeground("StudyActivity")) {
                Intent i = new Intent("com.iyuba.music.study");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            } else if (RuntimeManager.getInstance().getApplication().isAppointForeground("MainActivity")) {
                Intent i = new Intent("com.iyuba.music.main");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            } else if (RuntimeManager.getInstance().getApplication().isAppointForeground("LocalMusicActivity")) {
                Intent i = new Intent("com.iyuba.music.localmusic");
                i.putExtra("message", "change");
                context.sendBroadcast(i);
            }
        }
    }

}
