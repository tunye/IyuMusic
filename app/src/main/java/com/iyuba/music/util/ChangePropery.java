package com.iyuba.music.util;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.iyuba.music.manager.RuntimeManager;

import java.util.Locale;

/**
 * Created by 10202 on 2015/10/9.
 */
public class ChangePropery {
    public static void updateNightMode(boolean on) {
        Resources resources = RuntimeManager.getContext().getResources();
        DisplayMetrics dm = RuntimeManager.getDisplayMetrics();
        Configuration config = resources.getConfiguration();
        config.uiMode &= ~Configuration.UI_MODE_NIGHT_MASK;
        config.uiMode |= on ? Configuration.UI_MODE_NIGHT_YES : Configuration.UI_MODE_NIGHT_NO;
        resources.updateConfiguration(config, dm);
    }

    public static void updateLanguageMode(int languageType) {
        Resources resources = RuntimeManager.getContext().getResources();
        Configuration config = resources.getConfiguration();
        DisplayMetrics dm = resources.getDisplayMetrics();
        switch (languageType) {
            case 0://跟随系统
                config.setLocale(Locale.getDefault());
                break;
            case 1://中文
                config.setLocale(Locale.SIMPLIFIED_CHINESE);
                break;
            case 2://英文
                config.setLocale(Locale.ENGLISH);
                break;
            case 3://日文
                config.setLocale(Locale.JAPANESE);
                break;
            case 4://阿拉伯语
                config.setLocale(new Locale("ar"));
                break;
            default:
                config.setLocale(Locale.getDefault());
                break;
        }
        resources.updateConfiguration(config, dm);
    }
}
