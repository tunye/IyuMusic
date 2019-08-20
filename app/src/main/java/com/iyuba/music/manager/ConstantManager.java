package com.iyuba.music.manager;

import android.os.Environment;

import java.io.File;

/**
 * Created by 10202 on 2015/10/16.
 */
public class ConstantManager {
    public static final String MIPUSH_APP_ID = "2882303761517139929";
    public static final String MIPUSH_APP_KEY = "5671713914929";
    public final static String SMSAPPID = "1e93e2e17fe3e";
    public final static String SMSAPPSECRET = "20a214f48119fbbc88bbf729590618c0";
    public final static String YOUDAOSECRET = "3438bae206978fec8995b280c49dae1e";
    public final static String WXSECRET = "5d5d3eaf4c6b69a278cf16c115014474";
    public final static String WXID = "wx182643cdcfc2b59f";
    public static final String songUrl = "http://static.iyuba.cn/sounds/song/";
    public static final String vipUrl = "http://staticvip.iyuba.cn/sounds/song/";
    public static final String oldSoundUrl = "http://static2.iyuba.cn/go/musichigh/";
    public static final String oldSoundVipUrl = "http://staticvip2.iyuba.cn/go/musichigh/";
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
