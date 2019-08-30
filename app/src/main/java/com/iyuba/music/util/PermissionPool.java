package com.iyuba.music.util;

import android.Manifest;
import android.support.annotation.IntDef;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PermissionPool {
    //联系人相关组
    public static final int WRITE_CONTACTS = 0;
    public static final int GET_ACCOUNTS = 1;
    public static final int READ_CONTACTS = 2;
    //  电话相关组
    public static final int READ_CALL_LOG = 3;
    public static final int READ_PHONE_STATE = 4;
    public static final int CALL_PHONE = 5;
    public static final int WRITE_CALL_LOG = 6;
    public static final int USE_SIP = 7;
    public static final int PROCESS_OUTGOING_CALLS = 8;
    public static final int ADD_VOICEMAIL = 9;
    //日历相关组
    public static final int READ_CALENDAR = 10;
    public static final int WRITE_CALENDAR = 11;
    //摄像头相关组
    public static final int CAMERA = 12;
    //传感器相关组
    public static final int BODY_SENSORS = 13;
    public static final int ACCESS_FINE_LOCATION = 14;
    public static final int ACCESS_COARSE_LOCATION = 15;
    //存储相关组
    public static final int READ_EXTERNAL_STORAGE = 16;
    public static final int WRITE_EXTERNAL_STORAGE = 17;
    //麦克风相关组
    public static final int RECORD_AUDIO = 18;
    //短信相关组
    public static final int READ_SMS = 19;
    public static final int RECEIVE_WAP_PUSH = 20;
    public static final int RECEIVE_MMS = 21;
    public static final int RECEIVE_SMS = 22;
    public static final int SEND_SMS = 23;

    @IntDef({WRITE_CONTACTS,
            GET_ACCOUNTS,
            READ_CONTACTS,
            READ_CALL_LOG,
            READ_PHONE_STATE,
            CALL_PHONE,
            WRITE_CALL_LOG,
            USE_SIP,
            PROCESS_OUTGOING_CALLS,
            ADD_VOICEMAIL,
            READ_CALENDAR,
            WRITE_CALENDAR,
            CAMERA,
            BODY_SENSORS,
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            READ_EXTERNAL_STORAGE,
            WRITE_EXTERNAL_STORAGE,
            RECORD_AUDIO,
            READ_SMS,
            RECEIVE_WAP_PUSH,
            RECEIVE_MMS,
            RECEIVE_SMS,
            SEND_SMS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionCode {
    }

    @StringDef({
            //联系人相关组
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS,
            //  电话相关组
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ADD_VOICEMAIL,
            //日历相关组
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,
            //摄像头相关组
            Manifest.permission.CAMERA,
            //传感器相关组
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            //存储相关组
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //麦克风相关组
            Manifest.permission.RECORD_AUDIO,
            //短信相关组
            Manifest.permission.READ_SMS,
            Manifest.permission.RECEIVE_WAP_PUSH,
            Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_SMS,
            Manifest.permission.SEND_SMS,
    })
    @Retention(RetentionPolicy.SOURCE)
    public @interface PermissionName {
    }
}