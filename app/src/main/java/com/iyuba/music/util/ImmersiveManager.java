package com.iyuba.music.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.iyuba.music.R;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Properties;

public class ImmersiveManager {
    private static ImmersiveManager sInstance;
    private static String emuiVersion = "none";

    private ImmersiveManager() {
        getEmuiLevel();
    }

    public synchronized static ImmersiveManager getInstance() {
        if (sInstance == null) {
            sInstance = new ImmersiveManager();
        }
        return sInstance;
    }

    // activity沉浸式效果实现
    public void updateImmersiveStatus(Activity activity) {
        Window window = activity.getWindow();
        updateImmersive(window, isBrightTheme());
    }

    // activity沉浸式效果实现
    public void updateImmersiveStatus(Activity activity, boolean isBright) {
        Window window = activity.getWindow();
        updateImmersive(window, isBright);
    }

    // dialog沉浸式效果实现
    public void updateImmersiveStatus(Dialog dialog) {
        Window window = dialog.getWindow();
        updateImmersive(window, isBrightTheme());
    }

    /**
     * dialog沉浸式效果实现,
     *
     * @param dialog
     * @param isBright
     */
    public void updateImmersiveStatus(Dialog dialog, boolean isBright) {
        Window window = dialog.getWindow();
        updateImmersive(window, isBright);
    }

    @TargetApi(23)
    private void updateImmersive(Window window, boolean isBright) {
        int sysUIVisible = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
        if (isMARSHMALLOW()) {
            if (isBright) {
                sysUIVisible &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                sysUIVisible |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            setXiaomiStatusBarDarkMode(isBright, window);
        }
        setStatusBarUI(window, sysUIVisible);
    }

    @TargetApi(21)
    private void setStatusBarUI(Window window, int sysUIVisible) {
        if (isKITKAT()) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            return;
        }
        if (emuiVersion.startsWith("EmotionUI_")) {
            double version = Double.parseDouble(emuiVersion.split("_")[1]);
            if (version < 4 && version >= 3) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            } else {
                window.getDecorView().setSystemUiVisibility(sysUIVisible);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            }
        } else {
            window.getDecorView().setSystemUiVisibility(sysUIVisible);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        try {
            window.setStatusBarColor(Color.TRANSPARENT);
        } catch (NoSuchMethodError e) {
            e.printStackTrace();
        }
    }


    // miui6+系统手机状态栏字体颜色设置api
    private void setXiaomiStatusBarDarkMode(boolean darkmode, Window window) {
        Class<? extends Window> clazz = window.getClass();
        try {
            int darkModeFlag = 0;
            Class<?> layoutParams = Class.forName("android.view.MiuiWindowManager$LayoutParams");
            Field field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE");
            darkModeFlag = field.getInt(layoutParams);
            Method extraFlagField = clazz.getMethod("setExtraFlags", int.class, int.class);
            extraFlagField.invoke(window, darkmode ? darkModeFlag : 0, darkModeFlag);
        } catch (Exception e) {
            setMeizuStatusBarDarkMode(darkmode, window);
        }
    }

    // flyme4+系统手机状态栏字体颜色设置api
    private void setMeizuStatusBarDarkMode(final boolean darkmode, final Window window) {
        try {
            WindowManager.LayoutParams lp = window.getAttributes();
            Field darkFlag = WindowManager.LayoutParams.class
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON");
            Field meizuFlags = WindowManager.LayoutParams.class
                    .getDeclaredField("meizuFlags");
            darkFlag.setAccessible(true);
            meizuFlags.setAccessible(true);
            int bit = darkFlag.getInt(null);
            int value = meizuFlags.getInt(lp);
            if (darkmode) {
                value |= bit;
            } else {
                value &= ~bit;
            }
            meizuFlags.setInt(lp, value);
            window.setAttributes(lp);
        } catch (Exception e) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    setLeshiStatusBarDarkMode(darkmode, window);
                }
            }, 5);
        }
    }

    // 乐视eui系统手机状态栏字体颜色设置api,需要延时执行
    private void setLeshiStatusBarDarkMode(boolean darkmode, Window window) {
        int color = Color.WHITE;
        if (darkmode) {
            color = Color.BLACK;
        }
        Class<? extends Window> clazz = window.getClass();
        try {
            Method setStatusBarIconColorField = clazz.getMethod("setStatusBarIconColor", int.class);
            setStatusBarIconColorField.invoke(window, color);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getEmuiLevel() {
        if (TextUtils.isEmpty(emuiVersion) || emuiVersion.startsWith("EmotionUI_")) {
            return;
        }
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Properties properties = new Properties();
                File propFile = new File(Environment.getRootDirectory(), "build.prop");
                FileInputStream fis = null;
                if (propFile.exists()) {
                    try {
                        fis = new FileInputStream(propFile);
                        properties.load(fis);
                        fis.close();
                        fis = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        if (fis != null) {
                            try {
                                fis.close();
                            } catch (Exception e2) {
                                e2.printStackTrace();
                            }
                        }
                    }
                }
                if (properties.containsKey("ro.build.version.emui")) {
                    emuiVersion = properties.getProperty("ro.build.version.emui");
                } else {
                    emuiVersion = "";
                }
            }
        });
    }

    private boolean isBrightTheme() {
        return GetAppColor.getInstance().getAppColorRes() != R.color.skin_app_color_lgreen && GetAppColor.getInstance().getAppColorRes() != R.color.skin_app_color_pink;
    }

    public boolean isBrightTheme(int colorRes) {
        return colorRes != R.color.skin_app_color_lgreen && colorRes != R.color.skin_app_color_pink;
    }

    @TargetApi(4)
    private boolean isKITKAT() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP;
    }

    @TargetApi(4)
    private boolean isLOLLIPOP() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    @TargetApi(4)
    private boolean isMARSHMALLOW() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    }
}