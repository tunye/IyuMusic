package com.iyuba.music.util;

import android.content.Context;

import com.iyuba.music.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateFormat {
    private static SimpleDateFormat time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat year = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parseTime(String date) throws ParseException {
        return time.parse(date);
    }

    public static Date parseYear(String date) throws ParseException {
        return year.parse(date);
    }

    public static String formatTime(Date date) {
        return time.format(date);
    }

    public static String formatYear(Date date) {
        return year.format(date);
    }

    public static String showTime(Context context, Date ctime) {
        long nowtimelong = System.currentTimeMillis();
        long ctimelong = ctime.getTime();
        long result = Math.abs(nowtimelong - ctimelong);
        String r;
        if (result < 60000) {// 一分钟内
            long seconds = result / 1000;
            if (seconds < 10) {
                r = context.getString(R.string.message_now);
            } else {
                r = seconds + context.getString(R.string.message_second_ago);
            }
        } else if (result >= 60000 && result < 3600000) {// 一小时内
            long seconds = result / 60000;
            r = seconds + context.getString(R.string.message_minutes_ago);
        } else if (result >= 3600000 && result < 86400000) {// 一天内
            long seconds = result / 3600000;
            r = seconds + context.getString(R.string.message_hour_ago);
        } else if (result >= 8640000 && result < 172800000) {// 昨天
            SimpleDateFormat hour = new SimpleDateFormat(" HH:mm");
            r = context.getString(R.string.message_lastday) + hour.format(ctime);
        } else {// 日期格式
            r = formatYear(ctime);
        }
        return r;
    }

    public static String contentShowTime(Date ctime, Date compareTime) {
        long compareTimeLong = compareTime.getTime();
        long nowTimeLong = System.currentTimeMillis();
        long ctimelong = ctime.getTime();
        long result = Math.abs(compareTimeLong - ctimelong);
        String r;
        if (result < 300000) {// 五分钟内
            r = "";
        } else if (nowTimeLong - ctimelong < 86400000) {
            if (result >= 300000 && result < 86400000 && ctime.getDay() == compareTime.getDay()) {// 一天内
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm");
                r = hour.format(ctime);
            } else {
                SimpleDateFormat hour = new SimpleDateFormat("MM-dd HH:mm");
                r = hour.format(ctime);
            }
        } else {// 本年度
            Calendar today = Calendar.getInstance();
            Calendar target = Calendar.getInstance();
            target.setTime(ctime);
            if (target.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                SimpleDateFormat hour = new SimpleDateFormat("MM-dd HH:mm");
                r = hour.format(ctime);
            } else {
                SimpleDateFormat hour = new SimpleDateFormat("yy-MM-dd HH:mm");
                r = hour.format(ctime);
            }
        }
        return r;
    }
}
