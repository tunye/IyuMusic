package com.iyuba.music.manager;

/**
 * Created by 10202 on 2015/11/18.
 */
public enum SettingConfigManager {
    instance;
    private final static String EGGSHELL_TAG = "eggshell";
    private final static String PUSH_TAG = "push";
    private final static String LANGUAGE_TAG = "language";
    private final static String NIGHT_TAG = "night";
    private final static String AUTO_LOGIN_TAG = "autoLogin";
    private final static String AUTO_PLAY_TAG = "autoplay";
    private final static String AUTO_STOP_TAG = "autostop";
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
    private final static String DOWNLOAD = "download";
    private final static String DOWNLOADMEANWHILE = "downloadMeanwhile";
    private final static String AUTOROUND = "autoRound";

    private int language, sayingMode, wordOrder, studyMode, studyPlayMode, originalSize, download, studyTranslate;
    private boolean eggshell, push, night, autoLogin, autoplay, autostop, autoRound,
            autoDownload, upgrade, wordDefShow, wordAutoPlay, wordAutoAdd;
    private String lastADUrl;

    SettingConfigManager() {
        push = ConfigManager.instance.loadBoolean(PUSH_TAG, true);
        night = ConfigManager.instance.loadBoolean(NIGHT_TAG);
        language = ConfigManager.instance.loadInt(LANGUAGE_TAG);
        autoLogin = ConfigManager.instance.loadBoolean(AUTO_LOGIN_TAG, true);
        autoplay = ConfigManager.instance.loadBoolean(AUTO_PLAY_TAG);
        autostop = ConfigManager.instance.loadBoolean(AUTO_STOP_TAG, true);
        lastADUrl = ConfigManager.instance.loadString(AD_TAG);

        sayingMode = ConfigManager.instance.loadInt(SAYING_MODE_TAG);
        wordDefShow = ConfigManager.instance.loadBoolean(WORD_DEF_SHOW_TAG, true);
        wordOrder = ConfigManager.instance.loadInt(WORD_ORDER_TAG);
        wordAutoPlay = ConfigManager.instance.loadBoolean(WORD_AUTO_PLAY_TAG, true);
        wordAutoAdd = ConfigManager.instance.loadBoolean(WORD_AUTO_ADD_TAG);

        originalSize = ConfigManager.instance.loadInt(ORIGINAL_SIZE, 16);
        studyPlayMode = ConfigManager.instance.loadInt(STUDY_PLAY_MODE, 1);
        studyMode = ConfigManager.instance.loadInt(STUDY_MODE, 1);
        studyTranslate = ConfigManager.instance.loadInt(STUDY_TRANSLATE, 1);

        eggshell = ConfigManager.instance.loadBoolean(EGGSHELL_TAG);
        upgrade = ConfigManager.instance.loadBoolean(UPGRADE_TAG);
        autoRound = ConfigManager.instance.loadBoolean(AUTOROUND, true);
        autoDownload = ConfigManager.instance.loadBoolean(DOWNLOADMEANWHILE);
        download = ConfigManager.instance.loadInt(DOWNLOAD, 0);
    }

    public boolean isPush() {
        return push;
    }


    public void setPush(boolean push) {
        instance.push = push;
        ConfigManager.instance.putBoolean(PUSH_TAG, push);
    }


    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        instance.night = night;
        ConfigManager.instance.putBoolean(NIGHT_TAG, night);
    }

    public int getLanguage() {
        return language;
    }


    public void setLanguage(int language) {
        instance.language = language;
        ConfigManager.instance.putInt(LANGUAGE_TAG, language);
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }


    public void setAutoLogin(boolean autoLogin) {
        instance.autoLogin = autoLogin;
        ConfigManager.instance.putBoolean(AUTO_LOGIN_TAG, autoLogin);
    }

    public boolean isAutoPlay() {
        return autoplay;
    }

    public void setAutoPlay(boolean autoPlay) {
        instance.autoplay = autoPlay;
        ConfigManager.instance.putBoolean(AUTO_PLAY_TAG, autoPlay);
    }

    public boolean isAutoStop() {
        return autostop;
    }

    public void setAutoStop(boolean autoStop) {
        instance.autostop = autoStop;
        ConfigManager.instance.putBoolean(AUTO_STOP_TAG, autoStop);
    }

    public String getADUrl() {
        return lastADUrl;
    }

    public void setADUrl(String url) {
        instance.lastADUrl = url;
        ConfigManager.instance.putString(AD_TAG, url);
    }

    public int getSayingMode() {
        return sayingMode;
    }

    public void setSayingMode(int mode) {
        instance.sayingMode = mode;
        ConfigManager.instance.putInt(SAYING_MODE_TAG, mode);
    }

    public boolean isWordDefShow() {
        return wordDefShow;
    }

    public void setWordDefShow(boolean show) {
        instance.wordDefShow = show;
        ConfigManager.instance.putBoolean(WORD_DEF_SHOW_TAG, show);
    }

    public int getWordOrder() {
        return wordOrder;
    }

    public void setWordOrder(int order) {
        instance.wordOrder = order;
        ConfigManager.instance.putInt(WORD_ORDER_TAG, order);
    }

    public boolean isWordAutoPlay() {
        return wordAutoPlay;
    }

    public void setWordAutoPlay(boolean play) {
        instance.wordAutoPlay = play;
        ConfigManager.instance.putBoolean(WORD_AUTO_PLAY_TAG, play);
    }

    public boolean isWordAutoAdd() {
        return wordAutoAdd;
    }

    public void setWordAutoAdd(boolean add) {
        instance.wordAutoAdd = add;
        ConfigManager.instance.putBoolean(WORD_AUTO_ADD_TAG, add);
    }

    public int getStudyMode() {
        return studyMode;
    }

    public void setStudyMode(int mode) {
        instance.studyMode = mode;
        ConfigManager.instance.putInt(STUDY_MODE, mode);
    }

    public int getStudyTranslate() {
        return studyTranslate;
    }

    public void setStudyTranslate(int mode) {
        instance.studyTranslate = mode;
        ConfigManager.instance.putInt(STUDY_TRANSLATE, mode);
    }

    public int getStudyPlayMode() {
        return studyPlayMode;
    }

    public void setStudyPlayMode(int mode) {
        instance.studyPlayMode = mode;
        ConfigManager.instance.putInt(STUDY_PLAY_MODE, mode);
    }

    public boolean isEggShell() {
        return eggshell;
    }

    public void setEggShell(boolean eggShell) {
        instance.eggshell = eggShell;
        ConfigManager.instance.putBoolean(EGGSHELL_TAG, eggShell);
    }

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        instance.upgrade = upgrade;
        ConfigManager.instance.putBoolean(UPGRADE_TAG, upgrade);
    }

    public boolean isAutoRound() {
        return autoRound;
    }

    public void setAutoRound(boolean round) {
        instance.autoRound = round;
        ConfigManager.instance.putBoolean(AUTOROUND, round);
    }

    public boolean isDownloadMeanwhile() {
        return autoDownload;
    }

    public void setDownloadMeanwhile(boolean meanwhile) {
        instance.autoDownload = meanwhile;
        ConfigManager.instance.putBoolean(DOWNLOADMEANWHILE, meanwhile);
    }

    public int getDownloadMode() {
        return download;
    }

    public void setDownloadMode(int mode) {
        instance.download = mode;
        ConfigManager.instance.putInt(DOWNLOAD, mode);
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(int size) {
        instance.originalSize = size;
        ConfigManager.instance.putInt(ORIGINAL_SIZE, size);
    }
}
