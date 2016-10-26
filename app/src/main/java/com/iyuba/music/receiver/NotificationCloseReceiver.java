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
import android.support.v4.content.LocalBroadcastManager;

import com.iyuba.music.service.BigNotificationService;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

/**
 * @author ct <br/>
 *         退出程序
 */
public class NotificationCloseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent(context, BigNotificationService.class);
        i.setAction(BigNotificationService.NOTIFICATION_SERVICE);
        i.putExtra(BigNotificationService.COMMAND, BigNotificationService.COMMAND_REMOVE);
        BigNotificationService.INSTANCE.setNotificationCommand(intent);
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(new Intent("sleepFinish"));
    }
}
