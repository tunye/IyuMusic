package com.iyuba.music.util;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;

import com.iyuba.music.activity.WelcomeActivity;

/**
 * Created by 10202 on 2017/1/18.
 */

public class CreateLinkUtil {
    public static void addLocalMusicLink(Context context, Class cls, String name, int picResId) {
        Intent shortcutIntent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutIntent.putExtra("duplicate", false);
        // 需要显示的名称
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(context, picResId);
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 发送广播。OK
        Intent intent = new Intent();
        intent.setClass(context, cls);
        intent.putExtra(WelcomeActivity.NORMAL_START, false);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcutIntent);
    }
}
