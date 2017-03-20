package com.iyuba.music.manager;

import android.app.Activity;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

/**
 * @author ct
 *         <p/>
 *         功能：配置文件管理
 */
public class ConfigManager {
    public static final String CONFIG_NAME = "IyuMusic";
    private SharedPreferences preferences;

    private ConfigManager() {
        int mode = Activity.MODE_PRIVATE;
        preferences = RuntimeManager.getContext().getSharedPreferences(CONFIG_NAME, mode);
    }

    public static ConfigManager getInstance() {
        return SingleInstanceHelper.instance;
    }

    public void putBoolean(String name, boolean value) {
        preferences.edit().putBoolean(name, value).apply();
    }

    public void putInt(String name, int value) {
        preferences.edit().putInt(name, value).apply();
    }

    public void putString(String name, String value) {
        preferences.edit().putString(name, value).apply();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean loadBoolean(String key, boolean defaultBool) {
        return preferences.getBoolean(key, defaultBool);
    }

    public int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int loadInt(String key, int defaultInt) {
        return preferences.getInt(key, defaultInt);
    }

    public String loadString(String key) {
        return preferences.getString(key, "");
    }

    public String loadString(String key, @NonNull String defaultString) {
        return preferences.getString(key, defaultString);
    }

    public void removeKey(String key) {
        preferences.edit().remove(key).apply();
    }

    public void clearKey() {
        preferences.edit().clear().apply();
    }

    private static class SingleInstanceHelper {
        private static ConfigManager instance = new ConfigManager();
    }
}
