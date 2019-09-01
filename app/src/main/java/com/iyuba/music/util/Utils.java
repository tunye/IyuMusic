package com.iyuba.music.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.activity.WelcomeActivity;
import com.iyuba.music.manager.RuntimeManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

import static android.content.Context.TELEPHONY_SERVICE;

public class Utils {
    public static boolean range(int floor, int ceiling, int task) {
        return (task >= floor) && (task < ceiling);
    }

    public static int getRandomInt(int seed) {
        return new Random().nextInt(seed);
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        context.sendBroadcast(shortcutIntent);
    }

    public static String getMAC() {
        String result = "";
        String Mac = "";
        result = callCmd("busybox ifconfig", "HWaddr");

        if (result == null) {
            return "网络出错，请检查网络";
        }
        if (result.length() > 0 && result.contains("HWaddr")) {
            Mac = result.substring(result.indexOf("HWaddr") + 6, result.length() - 1);
            if (Mac.length() > 1) {
                result = Mac.toLowerCase();
            }
        }
        return result.trim();

    }

    private static String callCmd(String cmd, String filter) {
        String result = "";
        String line = "";
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);

            //执行命令cmd，只取结果中含有filter的这一行
            while ((line = br.readLine()) != null && !line.contains(filter)) {
            }
            result = line;
            br.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI() {
        TelephonyManager TelephonyMgr = (TelephonyManager) RuntimeManager.getInstance().getContext().getSystemService(TELEPHONY_SERVICE);
        try {
            return TelephonyMgr.getDeviceId();
        }catch (Throwable t){
            return "";
        }
    }
}
