package com.iyuba.music.util;

import android.content.Context;
import android.util.Log;

import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class DateFormat {
    private static SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy-MM-dd");

    public static Date parseTime(String date) throws ParseException {
        return timeFormat.parse(date);
    }

    public static Date parseYear(String date) throws ParseException {
        return yearFormat.parse(date);
    }

    public static String formatTime(Date date) {
        return timeFormat.format(date);
    }

    public static String formatYear(Date date) {
        return yearFormat.format(date);
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
        } else if (result < 3600000) {// 一小时内
            long seconds = result / 60000;
            r = seconds + context.getString(R.string.message_minutes_ago);
        } else if (result < 86400000) {// 一天内
            long seconds = result / 3600000;
            r = seconds + context.getString(R.string.message_hour_ago);
        } else if (result < 172800000) {// 昨天
            SimpleDateFormat hour = new SimpleDateFormat(" HH:mm");
            r = context.getString(R.string.message_lastday) + hour.format(ctime);
        } else {// 日期格式
            r = formatYear(ctime);
        }
        return r;
    }

    public static String contentShowTime(Date time, Date compareTime) {
        long compareTimeLong = compareTime.getTime();
        long timeLong = time.getTime();
        long result = Math.abs(compareTimeLong - timeLong);
        String r;
        if (result < 300000) {// 五分钟内
            r = "";
        } else if (System.currentTimeMillis() - timeLong < 86400000) {
            if (daysMinus(time, compareTime) == 0) {// 一天内
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm", Locale.CHINA);
                r = hour.format(time);
            } else if (Math.abs(daysMinus(time, compareTime)) == 1) {// 差一天
                SimpleDateFormat hour = new SimpleDateFormat("HH:mm", Locale.CHINA);
                r = RuntimeManager.getString(R.string.message_lastday) + hour.format(time);
            } else {
                SimpleDateFormat hour = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
                r = hour.format(time);
            }
        } else {// 本年度
            Calendar today = Calendar.getInstance();
            Calendar target = Calendar.getInstance();
            target.setTime(time);
            if (target.get(Calendar.YEAR) == today.get(Calendar.YEAR)) {
                SimpleDateFormat hour = new SimpleDateFormat("MM月dd日 HH:mm", Locale.CHINA);
                r = hour.format(time);
            } else {
                SimpleDateFormat hour = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.CHINA);
                r = hour.format(time);
            }
        }
        return r;
    }

    private static int daysMinus(Date fDate, Date oDate) {
        Calendar aCalendar = Calendar.getInstance();
        aCalendar.setTime(fDate);
        int day1 = aCalendar.get(Calendar.DAY_OF_YEAR);
        aCalendar.setTime(oDate);
        int day2 = aCalendar.get(Calendar.DAY_OF_YEAR);
        return day2 - day1;
    }


}
