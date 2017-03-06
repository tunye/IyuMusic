package com.iyuba.music.manager;

/**
 * Created by 10202 on 2015/11/18.
 */
public class SettingConfigManager {
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
    private SettingConfigManager() {
        push = ConfigManager.getInstance().loadBoolean(PUSH_TAG, true);
        night = ConfigManager.getInstance().loadBoolean(NIGHT_TAG);
        language = ConfigManager.getInstance().loadInt(LANGUAGE_TAG);
        autoLogin = ConfigManager.getInstance().loadBoolean(AUTO_LOGIN_TAG, true);
        autoplay = ConfigManager.getInstance().loadBoolean(AUTO_PLAY_TAG);
        autostop = ConfigManager.getInstance().loadBoolean(AUTO_STOP_TAG, true);
        lastADUrl = ConfigManager.getInstance().loadString(AD_TAG);

        sayingMode = ConfigManager.getInstance().loadInt(SAYING_MODE_TAG);
        wordDefShow = ConfigManager.getInstance().loadBoolean(WORD_DEF_SHOW_TAG, true);
        wordOrder = ConfigManager.getInstance().loadInt(WORD_ORDER_TAG);
        wordAutoPlay = ConfigManager.getInstance().loadBoolean(WORD_AUTO_PLAY_TAG, true);
        wordAutoAdd = ConfigManager.getInstance().loadBoolean(WORD_AUTO_ADD_TAG);

        originalSize = ConfigManager.getInstance().loadInt(ORIGINAL_SIZE, 16);
        studyPlayMode = ConfigManager.getInstance().loadInt(STUDY_PLAY_MODE, 1);
        studyMode = ConfigManager.getInstance().loadInt(STUDY_MODE, 1);
        studyTranslate = ConfigManager.getInstance().loadInt(STUDY_TRANSLATE, 1);

        eggshell = ConfigManager.getInstance().loadBoolean(EGGSHELL_TAG);
        upgrade = ConfigManager.getInstance().loadBoolean(UPGRADE_TAG);
        autoRound = ConfigManager.getInstance().loadBoolean(AUTOROUND, true);
        autoDownload = ConfigManager.getInstance().loadBoolean(DOWNLOADMEANWHILE);
        download = ConfigManager.getInstance().loadInt(DOWNLOAD, 0);
    }

    public static SettingConfigManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
        ConfigManager.getInstance().putBoolean(PUSH_TAG, push);
    }

    public boolean isNight() {
        return night;
    }

    public void setNight(boolean night) {
        this.night = night;
        ConfigManager.getInstance().putBoolean(NIGHT_TAG, night);
    }

    public int getLanguage() {
        return language;
    }

    public void setLanguage(int language) {
        this.language = language;
        ConfigManager.getInstance().putInt(LANGUAGE_TAG, language);
    }

    public boolean isAutoLogin() {
        return autoLogin;
    }

    public void setAutoLogin(boolean autoLogin) {
        this.autoLogin = autoLogin;
        ConfigManager.getInstance().putBoolean(AUTO_LOGIN_TAG, autoLogin);
    }

    public boolean isAutoPlay() {
        return autoplay;
    }

    public void setAutoPlay(boolean autoPlay) {
        this.autoplay = autoPlay;
        ConfigManager.getInstance().putBoolean(AUTO_PLAY_TAG, autoPlay);
    }

    public boolean isAutoStop() {
        return autostop;
    }

    public void setAutoStop(boolean autoStop) {
        this.autostop = autoStop;
        ConfigManager.getInstance().putBoolean(AUTO_STOP_TAG, autoStop);
    }

    public String getADUrl() {
        return lastADUrl;
    }

    public void setADUrl(String url) {
        this.lastADUrl = url;
        ConfigManager.getInstance().putString(AD_TAG, url);
    }

    public int getSayingMode() {
        return sayingMode;
    }

    public void setSayingMode(int mode) {
        this.sayingMode = mode;
        ConfigManager.getInstance().putInt(SAYING_MODE_TAG, mode);
    }

    public boolean isWordDefShow() {
        return wordDefShow;
    }

    public void setWordDefShow(boolean show) {
        this.wordDefShow = show;
        ConfigManager.getInstance().putBoolean(WORD_DEF_SHOW_TAG, show);
    }

    public int getWordOrder() {
        return wordOrder;
    }

    public void setWordOrder(int order) {
        this.wordOrder = order;
        ConfigManager.getInstance().putInt(WORD_ORDER_TAG, order);
    }

    public boolean isWordAutoPlay() {
        return wordAutoPlay;
    }

    public void setWordAutoPlay(boolean play) {
        this.wordAutoPlay = play;
        ConfigManager.getInstance().putBoolean(WORD_AUTO_PLAY_TAG, play);
    }

    public boolean isWordAutoAdd() {
        return wordAutoAdd;
    }

    public void setWordAutoAdd(boolean add) {
        this.wordAutoAdd = add;
        ConfigManager.getInstance().putBoolean(WORD_AUTO_ADD_TAG, add);
    }

    public int getStudyMode() {
        return studyMode;
    }

    public void setStudyMode(int mode) {
        this.studyMode = mode;
        ConfigManager.getInstance().putInt(STUDY_MODE, mode);
    }

    public int getStudyTranslate() {
        return studyTranslate;
    }

    public void setStudyTranslate(int mode) {
        this.studyTranslate = mode;
        ConfigManager.getInstance().putInt(STUDY_TRANSLATE, mode);
    }

    public int getStudyPlayMode() {
        return studyPlayMode;
    }

    public void setStudyPlayMode(int mode) {
        this.studyPlayMode = mode;
        ConfigManager.getInstance().putInt(STUDY_PLAY_MODE, mode);
    }

    public boolean isEggShell() {
        return eggshell;
    }

    public void setEggShell(boolean eggShell) {
        this.eggshell = eggShell;
        ConfigManager.getInstance().putBoolean(EGGSHELL_TAG, eggShell);
    }

    public boolean isUpgrade() {
        return upgrade;
    }

    public void setUpgrade(boolean upgrade) {
        this.upgrade = upgrade;
        ConfigManager.getInstance().putBoolean(UPGRADE_TAG, upgrade);
    }

    public boolean isAutoRound() {
        return autoRound;
    }

    public void setAutoRound(boolean round) {
        this.autoRound = round;
        ConfigManager.getInstance().putBoolean(AUTOROUND, round);
    }

    public boolean isDownloadMeanwhile() {
        return autoDownload;
    }

    public void setDownloadMeanwhile(boolean meanwhile) {
        this.autoDownload = meanwhile;
        ConfigManager.getInstance().putBoolean(DOWNLOADMEANWHILE, meanwhile);
    }

    public int getDownloadMode() {
        return download;
    }

    public void setDownloadMode(int mode) {
        this.download = mode;
        ConfigManager.getInstance().putInt(DOWNLOAD, mode);
    }

    public int getOriginalSize() {
        return originalSize;
    }

    public void setOriginalSize(int size) {
        this.originalSize = size;
        ConfigManager.getInstance().putInt(ORIGINAL_SIZE, size);
    }

    private static class SingleInstanceHelper {
        private static SettingConfigManager instance = new SettingConfigManager();
    }
}
