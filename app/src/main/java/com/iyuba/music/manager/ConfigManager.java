package com.iyuba.music.manager;

import android.app.Activity;
import android.content.SharedPreferences;

import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.util.SPUtils;
import com.buaa.ct.core.util.ThreadPoolUtil;

/**
 * Created by 10202 on 2015/11/18.
 */
public class ConfigManager {
    private static final String CONFIG_NAME = "IyuMusic";
    private final static String EGGSHELL_TAG = "eggshell";
    private final static String PUSH_TAG = "push";
    private final static String LANGUAGE_TAG = "language";
    private final static String NIGHT_TAG = "night";
    private final static String AUTO_LOGIN_TAG = "autoLogin";
    private final static String AUTO_PLAY_TAG = "autoplay";
    private final static String AUTO_STOP_TAG = "autostop";
    private final static String MEDIA_BUTTON = "media_button";
    private final static String AD_TAG = "lastADUrl";
    private final static String UPGRADE_TAG = "upgrade";
    private final static String SAYING_MODE_TAG = "sayingMode";
    private final static String WORD_DEF_SHOW_TAG = "wordDefShow";
    private final static String WORD_ORDER_TAG = "wordOrder";
    private final static String WORD_AUTO_PLAY_TAG = "wordAutoPlay";
    private final static String WORD_AUTO_ADD_TAG = "wordAutoAdd";
    private final static String STUDY_MODE = "studyMode";
    private final static String STUDY_TRANSLATE = "studyTranslate";
    private final static String STUDY_PLAY_MODE = "studyPlayMode";
    private final static String ORIGINAL_SIZE = "originalSize";
    private final static String PHOTO_TIMESTAMP = "photoTimestamp";
    private final static String DOWNLOAD = "download";
    private final static String DOWNLOADMEANWHILE = "downloadMeanwhile";
    private final static String AUTOROUND = "autoRound";

    private final static String INITWEIGHT = "initWeight";
    private final static String TARGETWEIGHT = "targetWeight";
    private final static String SHOWTARGET = "showTarget";
    private SharedPreferences preferences;
    private int language, sayingMode, wordOrder, studyMode, studyPlayMode, originalSize, download, studyTranslate;
    private boolean eggshell, push, night, mediaButton, autoLogin, autoPlay, autoStop, autoRound,
            autoDownload, upgrade, wordDefShow, wordAutoPlay, wordAutoAdd, showWeightTarget;
    private float initWeight, targetWeight;
    private String lastADUrl, photoTimestamp;

    private ConfigManager() {
        preferences = RuntimeManager.getInstance().getContext().getSharedPreferences(CONFIG_NAME, Activity.MODE_PRIVATE);
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                push = SPUtils.loadBoolean(preferences, PUSH_TAG, true);
                night = SPUtils.loadBoolean(preferences, NIGHT_TAG);
                language = SPUtils.loadInt(preferences, LANGUAGE_TAG);
                autoLogin = SPUtils.loadBoolean(preferences, AUTO_LOGIN_TAG, true);
                autoPlay = SPUtils.loadBoolean(preferences, AUTO_PLAY_TAG);
                autoStop = SPUtils.loadBoolean(preferences, AUTO_STOP_TAG, true);
                mediaButton = SPUtils.loadBoolean(preferences, MEDIA_BUTTON, true);
                lastADUrl = SPUtils.loadString(preferences, AD_TAG);

                sayingMode = SPUtils.loadInt(preferences, SAYING_MODE_TAG);
                wordDefShow = SPUtils.loadBoolean(preferences, WORD_DEF_SHOW_TAG, true);
                wordOrder = SPUtils.loadInt(preferences, WORD_ORDER_TAG);
                wordAutoPlay = SPUtils.loadBoolean(preferences, WORD_AUTO_PLAY_TAG, true);
                wordAutoAdd = SPUtils.loadBoolean(preferences, WORD_AUTO_ADD_TAG);

                originalSize = SPUtils.loadInt(preferences, ORIGINAL_SIZE, 16);
                studyPlayMode = SPUtils.loadInt(preferences, STUDY_PLAY_MODE, 1);
                studyMode = SPUtils.loadInt(preferences, STUDY_MODE, 1);
                studyTranslate = SPUtils.loadInt(preferences, STUDY_TRANSLATE, 1);

                eggshell = SPUtils.loadBoolean(preferences, EGGSHELL_TAG);
                upgrade = SPUtils.loadBoolean(preferences, UPGRADE_TAG);
                autoRound = SPUtils.loadBoolean(preferences, AUTOROUND, true);
                photoTimestamp = SPUtils.loadString(preferences, PHOTO_TIMESTAMP, "");
                autoDownload = SPUtils.loadBoolean(preferences, DOWNLOADMEANWHILE);
                download = SPUtils.loadInt(preferences, DOWNLOAD, 0);

                initWeight = SPUtils.loadFloat(preferences, INITWEIGHT, 100);
                targetWeight = SPUtils.loadFloat(preferences, TARGETWEIGHT, 80);
                showWeightTarget = SPUtils.loadBoolean(preferences, SHOWTARGET);
            }
        });
    }

    public static ConfigManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public SharedPreferences getPreferences() {
        return preferences;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
        SPUtils.putBoolean(preferences, PUSH_TAG, push);
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
        SPUtils.putBoolean(preferences, NIGHT_TAG, night);
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
        SPUtils.putInt(preferences, LANGUAGE_TAG, language);
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
        SPUtils.putBoolean(preferences, AUTO_LOGIN_TAG, autoLogin);
    }

    public boolean isAutoPlay() {
        return autoPlay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoPlay = autoPlay;
        SPUtils.putBoolean(preferences, AUTO_PLAY_TAG, autoPlay);
    }

    public boolean isAutoStop() {
        return autoStop;
    }

    public void setAutoStop(boolean autoStop) {
        this.autoStop = autoStop;
        SPUtils.putBoolean(preferences, AUTO_STOP_TAG, autoStop);
    }

    public boolean isMediaButton() {
        return mediaButton;
    }

    public void setMediaButton(boolean mediaButton) {
        this.mediaButton = mediaButton;
        SPUtils.putBoolean(preferences, MEDIA_BUTTON, mediaButton);
    }

    public String getADUrl() {
        return lastADUrl;
    }

    public void setADUrl(String url) {
        this.lastADUrl = url;
        SPUtils.putString(preferences, AD_TAG, url);
    }

    public int getSayingMode() {
        return sayingMode;
    }

    public void setSayingMode(int mode) {
        this.sayingMode = mode;
        SPUtils.putInt(preferences, SAYING_MODE_TAG, mode);
    }

    public boolean isWordDefShow() {
        return wordDefShow;
    }

    public void setWordDefShow(boolean show) {
        this.wordDefShow = show;
        SPUtils.putBoolean(preferences, WORD_DEF_SHOW_TAG, show);
    }

    public int getWordOrder() {
        return wordOrder;
    }

    public void setWordOrder(int order) {
        this.wordOrder = order;
        SPUtils.putInt(preferences, WORD_ORDER_TAG, order);
    }

    public boolean isWordAutoPlay() {
        return wordAutoPlay;
    }

    public void setWordAutoPlay(boolean play) {
        this.wordAutoPlay = play;
        SPUtils.putBoolean(preferences, WORD_AUTO_PLAY_TAG, play);
    }

    public boolean isWordAutoAdd() {
        return wordAutoAdd;
    }

    public void setWordAutoAdd(boolean add) {
        this.wordAutoAdd = add;
        SPUtils.putBoolean(preferences, WORD_AUTO_ADD_TAG, add);
    }

    public int getStudyMode() {
        return studyMode;
    }

    public void setStudyMode(int mode) {
        this.studyMode = mode;
        SPUtils.putInt(preferences, STUDY_MODE, mode);
    }

    public int getStudyTranslate() {
        return studyTranslate;
    }

    public void setStudyTranslate(int mode) {
        this.studyTranslate = mode;
        SPUtils.putInt(preferences, STUDY_TRANSLATE, mode);
    }

    public int getStudyPlayMode() {
        return studyPlayMode;
    }

    public void setStudyPlayMode(int mode) {
        this.studyPlayMode = mode;
        SPUtils.putInt(preferences, STUDY_PLAY_MODE, mode);
    }

    public boolean isEggShell() {
        return eggshell;
    }

    public void setEggShell(boolean eggShell) {
        this.eggshell = eggShell;
        SPUtils.putBoolean(preferences, EGGSHELL_TAG, eggShell);
    }

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
        SPUtils.putBoolean(preferences, UPGRADE_TAG, upgrade);
    }

    public boolean isAutoRound() {
        return autoRound;
    }

    public void setAutoRound(boolean round) {
        this.autoRound = round;
        SPUtils.putBoolean(preferences, AUTOROUND, round);
    }

    public boolean isDownloadMeanwhile() {
        return autoDownload;
    }

    public void setDownloadMeanwhile(boolean meanwhile) {
        this.autoDownload = meanwhile;
        SPUtils.putBoolean(preferences, DOWNLOADMEANWHILE, meanwhile);
    }

    public int getDownloadMode() {
        return download;
    }

    public void setDownloadMode(int mode) {
        this.download = mode;
        SPUtils.putInt(preferences, DOWNLOAD, mode);
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(int size) {
        this.originalSize = size;
        SPUtils.putInt(preferences, ORIGINAL_SIZE, size);
    }

    public float getInitWeight() {
        return initWeight;
    }

    public void setInitWeight(float weight) {
        this.initWeight = weight;
        SPUtils.putFloat(preferences, INITWEIGHT, weight);
    }

    public float getTargetWeight() {
        return targetWeight;
    }

    public void setTargetWeight(float weight) {
        this.targetWeight = weight;
        SPUtils.putFloat(preferences, TARGETWEIGHT, weight);
    }

    public boolean isShowTarget() {
        return showWeightTarget;
    }

    public void setShowTarget(boolean show) {
        this.showWeightTarget = show;
        SPUtils.putBoolean(preferences, SHOWTARGET, show);
    }

    public String getUserPhotoTimeStamp() {
        return photoTimestamp;
    }

    public void setUserPhotoTimeStamp() {
        this.photoTimestamp = "time=" + System.currentTimeMillis();
        SPUtils.putString(preferences, PHOTO_TIMESTAMP, photoTimestamp);
    }

    private static class SingleInstanceHelper {
        private static ConfigManager instance = new ConfigManager();
    }
}
