package com.iyuba.music;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.multidex.MultiDex;

import com.buaa.ct.skin.SkinManager;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkChangeBroadcastReceiver;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.network.NetWorkType;
import com.iyuba.music.service.BigNotificationService;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;

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
    private NetWorkChangeBroadcastReceiver netWorkChange;
    private Runnable baseRunnable = new Runnable() {
        @Override
        public void run() {
            if (sleepSecond == 0) {
                baseHandler.removeCallbacks(this);
                exit();
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
        PushAgent mPushAgent = PushAgent.getInstance(this);
        mPushAgent.register(new IUmengRegisterCallback() {

            @Override
            public void onSuccess(String deviceToken) {
                //注册成功会返回device token
            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
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
        RuntimeManager.initRuntimeManager(this);
        // 程序皮肤、字符集、夜间模式初始化
        ChangePropery.updateNightMode(ConfigManager.instance.loadBoolean("night", false));
        ChangePropery.updateLanguageMode(ConfigManager.instance.loadInt("language", 0));
        SkinManager.getInstance().init(this, "MusicSkin");
        // 注册网络监听
        netWorkChange = new NetWorkChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkChange, intentFilter);
        NetWorkState.getInstance().setNetWorkState(NetWorkType.getNetworkType(this));
        // 共享平台
        PlatformConfig.setWeixin(ConstantManager.WXID, ConstantManager.WXSECRET);
        PlatformConfig.setSinaWeibo("3225411888", "16b68c9ca20e662001adca3ca5617294","http://www.iyuba.com");
        PlatformConfig.setQQZone("1150062634", "7d9d7157c25ad3c67ff2de5ee69c280c");
        UMShareConfig config = new UMShareConfig();
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
        UMShareAPI.get(this).setShareConfig(config);
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

    private void removeNotification() {
        if (BigNotificationService.INSTANCE.isAlive) {
            Intent i = new Intent(this, BigNotificationService.class);
            i.setAction(BigNotificationService.NOTIFICATION_SERVICE);
            i.putExtra(BigNotificationService.COMMAND, BigNotificationService.COMMAND_REMOVE);
            BigNotificationService.INSTANCE.setNotificationCommand(i);
        }
    }

    private void stopPlayService() {
        if (getPlayerService().getPlayer().isPlaying()) {
            getPlayerService().getPlayer().stopPlayback();
            StudyRecordUtil.recordStop(StudyManager.instance.getLesson(), 0);
        }
        stopService(playServiceIntent);
    }

    public void exit() {
        unregisterReceiver(netWorkChange);
        removeNotification();
        stopPlayService();
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
