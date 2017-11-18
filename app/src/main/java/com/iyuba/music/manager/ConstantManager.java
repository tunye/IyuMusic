package com.iyuba.music.manager;

import android.os.Environment;
import android.support.v4.util.ArrayMap;

import java.io.File;
import java.util.Map;

/**
 * Created by 10202 on 2015/10/16.
 */
public class ConstantManager {
    public static final String MIPUSH_APP_ID = "2882303761517139929";
    public static final String MIPUSH_APP_KEY = "5671713914929";
    public final static String SMSAPPID = "1e93e2e17fe3e";
    public final static String SMSAPPSECRET = "20a214f48119fbbc88bbf729590618c0";
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

    public static Map<String, String> qun;

    static {
        envir = Environment.getExternalStorageDirectory() + "/iyuba/music";
        updateFolder = envir + File.separator + "update";
        musicFolder = envir + File.separator + "audio";
        lrcFolder = envir + File.separator + "audioLrc";
        originalFolder = envir + File.separator + "audioOriginal";
        crashFolder = envir + File.separator + "crash";
        recordFile = envir + File.separator + "sound";
        imgFile = envir + File.separator + "image";
        qun = new ArrayMap<>();
        qun.put("huawei", "339895927");
        qun.put("vivo", "483288976");
        qun.put("oppo", "624796280");
        qun.put("xiaomi", "493470842");
        qun.put("samsung", "639727892");
        qun.put("360", "625355797");
        qun.put("gionee", "621392974");
        qun.put("meizu", "625401994");
        qun.put("default", "540297996");
    }
}
