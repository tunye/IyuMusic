package com.iyuba.music.manager;

import android.os.Environment;

import java.io.File;

/**
 * Created by 10202 on 2015/10/16.
 */
public class ConstantManager {
    public final static String SMSAPPID = "19f74c7fb89c";
    public final static String SMSAPPSECRET = "a4f1e7a1e13c63162cc987f9cc9785e0";
    public final static String YOUDAOSECRET = "5542d99e63893312d28d7e49e2b43559";
    public final static String WXSECRET = "5d5d3eaf4c6b69a278cf16c115014474";
    public final static String WXID = "wx182643cdcfc2b59f";
    public static final String songUrl = "http://static.iyuba.com/sounds/song/";
    public static final String vipUrl = "http://staticvip.iyuba.com/sounds/song/";
    public static final String oldSoundUrl = "http://static2.iyuba.com/go/musichigh/";
    public static final String oldSoundVipUrl = "http://staticvip2.iyuba.com/go/musichigh/";
    public static final String appId = "209";
    public static final String appName = "听歌学英语";
    public static final String appEnglishName = "afterclass";
    public static String envir;
    public static String updateFolder;
    public static String musicFolder;
    public static String crashFolder;
    public static String lrcFolder;
    public static String originalFolder;
    public static String recordFile;
    public static String imgFile;

    static {
        envir = Environment.getExternalStorageDirectory() + "/iyuba/music";
        updateFolder = envir + File.separator + "update";
        musicFolder = envir + File.separator + "audio";
        lrcFolder = envir + File.separator + "audioLrc";
        originalFolder = envir + File.separator + "audioOriginal";
        crashFolder = envir + File.separator + "crash";
        recordFile = envir + File.separator + "sound";
        imgFile = envir + File.separator + "image";
    }
}
