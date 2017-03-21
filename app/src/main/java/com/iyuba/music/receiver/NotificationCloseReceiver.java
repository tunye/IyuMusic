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

/**
 * @author ct <br/>
 *         退出程序
 */
public class NotificationCloseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        RuntimeManager.getApplication().exit();
    }
}
