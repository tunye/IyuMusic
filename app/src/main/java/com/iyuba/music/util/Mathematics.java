package com.iyuba.music.util;

import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

/**
 * Created by 10202 on 2015/11/19.
 */
public class Mathematics {
    public static boolean range(int floor, int ceiling, int task) {
        return (task >= floor) && (task < ceiling);
    }

    public static boolean rangeIn(int floor, int ceiling, int task) {
        return (task > floor) && (task < ceiling);
    }

    public static boolean rangeBorder(int floor, int ceiling, int task) {
        return (task >= floor) && (task <= ceiling);
    }

    public static String formatTime(int time) {
        int hour = time / 3600;
        int minute = time / 60 % 60;
        int second = time % 60;
        if (hour != 0) {
            return String.format(Locale.CHINA, "%02d:%02d:%02d", hour, minute, second);
        } else {
            return String.format(Locale.CHINA, "%02d:%02d", minute, second);
        }
    }

    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
