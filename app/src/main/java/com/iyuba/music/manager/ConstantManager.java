package com.iyuba.music.manager;

import android.os.Environment;

import java.io.File;

/**
 * Created by 10202 on 2015/10/16.
 */
public class ConstantManager {
    public final static String SMSAPPID = "19f74c7fb89c";
    public final static String SMSAPPSECRET = "a4f1e7a1e13c63162cc987f9cc9785e0";
    public final static String YOUDAOSECRET = "b932187c3ec9f01c9ef45ad523510edd";
    public final static String WXSECRET = "5d5d3eaf4c6b69a278cf16c115014474";
    public final static String WXID = "wx182643cdcfc2b59f";
    private static final String songUrl = "http://static.iyuba.com/sounds/song/";
    private static final String vipUrl = "http://staticvip.iyuba.com/sounds/song/";
    private static final String oldSoundUrl = "http://static2.iyuba.com/go/musichigh/";
    private static final String oldSoundVipUrl = "http://staticvip2.iyuba.com/go/musichigh/";
    private String envir;
    private String appId;
    private String appName;
    private String appEnglishName;
    private String updateFolder;
    private String musicFolder;
    private String crashFolder;
    private String lrcFolder;
    private String originalFolder;
    private String recordFile;
    private String imgFile;
    private ConstantManager() {
        envir = Environment.getExternalStorageDirectory() + "/iyuba/music";
        appId = "209";
        appName = "听歌学英语";
        appEnglishName = "afterclass";
        updateFolder = envir + File.separator + "update";
        musicFolder = envir + File.separator + "audio";
        lrcFolder = envir + File.separator + "audioLrc";
        originalFolder = envir + File.separator + "audioOriginal";
        crashFolder = envir + File.separator + "crash";
        recordFile = envir + File.separator + "sound";
        imgFile = envir + File.separator + "image";
    }

    public static ConstantManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public static String getSongUrl() {
        return songUrl;
    }

    public static String getVipUrl() {
        return vipUrl;
    }

    public static String getOldSoundUrl() {
        return oldSoundUrl;
    }

    public static String getOldSoundVipUrl() {
        return oldSoundVipUrl;
    }

    public String getEnvir() {
        return envir;
    }

    public String getAppId() {
        return appId;
    }

    public String getUpdateFolder() {
        return updateFolder;
    }

    public String getMusicFolder() {
        return musicFolder;
    }

    public String getLrcFolder() {
        return lrcFolder;
    }

    public String getOriginalFolder() {
        return originalFolder;
    }

    public String getAppName() {
        return appName;
    }

    public String getAppEnglishName() {
        return appEnglishName;
    }

    public String getCrashFolder() {
        return crashFolder;
    }

    public String getRecordFile() {
        return recordFile;
    }

    public String getImgFile() {
        return imgFile;
    }

    private static class SingleInstanceHelper {
        private static ConstantManager instance = new ConstantManager();
    }
}
