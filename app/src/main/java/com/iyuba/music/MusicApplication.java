package com.iyuba.music;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.multidex.MultiDex;
import android.support.v4.content.LocalBroadcastManager;

import com.buaa.ct.skin.SkinManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.network.NetWorkType;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.umeng.socialize.PlatformConfig;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/11/16.
 */
public class MusicApplication extends Application {
    private List<Activity> activityList;
    private int sleepSecond;
    private Handler baseHandler = new Handler();
    private PlayerService playerService;
    private Intent playServiceIntent;
    private Runnable baseRunnable = new Runnable() {
        @Override
        public void run() {
            if (sleepSecond == 0) {
                baseHandler.removeCallbacks(this);
                LocalBroadcastManager.getInstance(MusicApplication.this).sendBroadcast(new Intent("sleepFinish"));
            } else if (sleepSecond == 1) {
                CustomToast.INSTANCE.showToast(R.string.sleep_time_finish);
                sleepSecond--;
                baseHandler.postDelayed(this, 1000);
            } else {
                sleepSecond--;
                baseHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();//必须调用父类方法
        prepareForApp();
        activityList = new ArrayList<>();
        playServiceIntent = new Intent(this, PlayerService.class);
        startService(playServiceIntent);
        //LeakCanary.install(this);
        CrashHandler crashHandler = new CrashHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(crashHandler);
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // 开启Multiple-Dex
    }

    private void prepareForApp() {
        RuntimeManager.setApplication(this);
        RuntimeManager.setApplicationContext(this.getApplicationContext());
        ChangePropery.updateNightMode(ConfigManager.instance.loadBoolean("night", false));
        ChangePropery.updateLanguageMode(ConfigManager.instance.loadInt("language", 1));
        NetWorkState.getInstance().setNetWorkState(NetWorkType.getNetworkType(this));
        PlatformConfig.setWeixin("wx182643cdcfc2b59f", "5d5d3eaf4c6b69a278cf16c115014474");
        PlatformConfig.setSinaWeibo("3225411888", "16b68c9ca20e662001adca3ca5617294");
        PlatformConfig.setQQZone("1150062634", "7d9d7157c25ad3c67ff2de5ee69c280c");
        SkinManager.getInstance().init(this, "MusicSkin");
    }

    public void pushActivity(Activity activity) {
        activityList.add(activity);
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    public void clearActivityList() {
        for (Activity activity : activityList) {
            if (null != activity) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    public void exit() {
        stopService(playServiceIntent);
        ImageUtil.clearMemoryCache(this);
        clearActivityList();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public int getSleepSecond() {
        return sleepSecond;
    }

    public void setSleepSecond(int sleepSecond) {
        this.sleepSecond = sleepSecond;
        baseHandler.removeCallbacks(baseRunnable);
        if (sleepSecond != 0) {
            baseHandler.postDelayed(baseRunnable, 1000);
        }
    }

    public boolean isAppointForeground(String appoint) {
        if (activityList != null && activityList.size() > 0) {
            Activity activity = activityList.get(activityList.size() - 1);
            if (activity.getLocalClassName().contains(appoint)) {
                return true;
            }
        }
        return false;
    }

    public boolean onlyForeground(String appoint) {
        return activityList.size() == 1 && activityList.get(0).getLocalClassName().contains(appoint);
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }
}
