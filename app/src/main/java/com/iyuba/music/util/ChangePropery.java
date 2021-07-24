package com.iyuba.music.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.support.v7.app.AppCompatDelegate;
import android.util.DisplayMetrics;

import com.buaa.ct.core.manager.ImmersiveManager;
import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.util.LanguageUtil;
import com.iyuba.music.manager.ConfigManager;

import java.util.Locale;

/**
 * Created by 10202 on 2015/10/9.
 */
public class ChangePropery {
    public static void setAppConfig(Activity activity) {
        ChangePropery.updateNightMode(ConfigManager.getInstance().isNight());
        ChangePropery.updateLanguageMode(activity, ConfigManager.getInstance().getLanguage());
        activity.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        ImmersiveManager.getInstance().updateImmersiveStatus(activity);
    }

    public static void updateNightMode(boolean on) {
        Resources resources = RuntimeManager.getInstance().getContext().getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        if (isSystemDark()) {
            on = true;
        }
        if (on) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        resources.updateConfiguration(config, dm);
    }

    public static boolean isSystemDark() {
        Configuration config = Resources.getSystem().getConfiguration();
        return (config.uiMode & Configuration.UI_MODE_NIGHT_YES) == Configuration.UI_MODE_NIGHT_YES;
    }

    public static void updateLanguageMode(int languageType) {
        Resources resources = RuntimeManager.getInstance().getContext().getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        changeLanguage(languageType, config);
        resources.updateConfiguration(config, dm);
    }

    public static void updateLanguageMode(Context context, int languageType) {
        Resources resources = context.getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        changeLanguage(languageType, config);
        resources.updateConfiguration(config, dm);
    }

    private static void changeLanguage(int languageType, Configuration config) {
        switch (languageType) {
            case 0://跟随系统
                config.setLocale(LanguageUtil.getSystemLocale());
                break;
            case 1://中文
                config.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;
            case 2://英文
                config.setLocale(Locale.ENGLISH);
                break;
            case 3://繁体
                config.setLocale(Locale.TRADITIONAL_CHINESE);
                break;
            case 4://阿拉伯语
                config.setLocale(new Locale("ar"));
                break;
            default:
                config.setLocale(Locale.getDefault());
                break;
        }
    }
}
