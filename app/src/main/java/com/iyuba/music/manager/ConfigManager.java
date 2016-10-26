package com.iyuba.music.manager;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author ct
 *         <p/>
 *         功能：配置文件管理
 */
public enum ConfigManager {
    instance;
    public static final String CONFIG_NAME = "IyuMusic";
    private Context context;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;

    ConfigManager() {
        this.context = RuntimeManager.getContext();
        openEditor();
    }

    // 创建或修改配置文件
    public void openEditor() {
        int mode = Activity.MODE_PRIVATE;
        preferences = context.getSharedPreferences(CONFIG_NAME, mode);
        editor = preferences.edit();
    }

    public void putBoolean(String name, boolean value) {
        editor.putBoolean(name, value);
        editor.apply();
    }

    public void putFloat(String name, float value) {
        editor.putFloat(name, value);
        editor.apply();
    }

    public void putInt(String name, int value) {
        editor.putInt(name, value);
        editor.apply();
    }

    public void putLong(String name, long value) {
        editor.putLong(name, value);
        editor.apply();
    }

    public void putString(String name, String value) {
        editor.putString(name, value);
        editor.apply();
    }

    public boolean loadBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean loadBoolean(String key, boolean defaultBool) {
        return preferences.getBoolean(key, defaultBool);
    }

    public float loadFloat(String key) {
        return preferences.getFloat(key, 0);
    }

    public float loadFloat(String key, float defaultFloat) {
        return preferences.getFloat(key, defaultFloat);
    }

    public int loadInt(String key) {
        return preferences.getInt(key, 0);
    }

    public int loadInt(String key, int defaultInt) {
        return preferences.getInt(key, defaultInt);
    }

    public long loadLong(String key) {
        return preferences.getLong(key, 0);
    }

    public long loadLong(String key, long defaultLong) {
        return preferences.getLong(key, defaultLong);
    }

    public String loadString(String key) {
        return preferences.getString(key, "");
    }

    public String loadString(String key, String defaultString) {
        return preferences.getString(key, defaultString);
    }

    public void removeKey(String key) {
        editor.remove(key);
        editor.commit();
    }

    public void clearKey() {
        editor.clear();
        editor.commit();
    }
}
