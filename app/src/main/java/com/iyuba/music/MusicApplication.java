package com.iyuba.music;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.buaa.ct.skin.SkinManager;
import com.buaa.ct.videocachelibrary.HttpProxyCacheServer;
import com.bumptech.glide.Glide;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.network.NetWorkType;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.widget.CustomToast;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.io.IOException;
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
    private HttpProxyCacheServer proxy;
    private ChangePropertyBroadcast changeProperty;
    private CountDownTimer timer;

    @Override
    public void onCreate() {
        super.onCreate();//必须调用父类方法
        RuntimeManager.initRuntimeManager(this);
        // android n 获取文件必须的权限配置
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
        if (shouldInit()) {
            activityList = new ArrayList<>();
            initApplication();
//            LeakCanary.install(this);
//            CrashHandler crashHandler = new CrashHandler(this);
//            Thread.setDefaultUncaughtExceptionHandler(crashHandler);
        }
    }

    private void pushSdkInit() {
        final String TAG = "mipush";
        if (ConfigManager.getInstance().isPush()) {
            MiPushClient.registerPush(this, ConstantManager.MIPUSH_APP_ID, ConstantManager.MIPUSH_APP_KEY);
        }
        LoggerInterface newLogger = new LoggerInterface() {

            @Override
            public void setTag(String tag) {
            }

            @Override
            public void log(String content, Throwable t) {
                if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                    Log.d(TAG, content, t);
                }
            }

            @Override
            public void log(String content) {
                if ((getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0) {
                    Log.d(TAG, content);
                }
            }
        };
        Logger.setLogger(this, newLogger);
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this); // 开启Multiple-Dex
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Glide.get(this).trimMemory(level);
    }

    private void initApplication() {
        ConfigManager.getInstance();
        SkinManager.getInstance().init(this, "MusicSkin");
        baseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                prepareLazy();
            }
        }, 800);
    }

    private void prepareLazy() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                // 文件目录的初始化
                File file = new File(ConstantManager.envir);
                if (!file.exists()) {
                    file.mkdirs();
                }
                file = new File(ConstantManager.envir + "/.nomedia");
                if (!file.exists()) {
                    try {
                        file.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        // 网络状态初始化
        NetWorkState.getInstance().setNetWorkState(NetWorkType.getNetworkType(this));
        // 皮肤等状态切换监听
        changeProperty = new ChangePropertyBroadcast();
        IntentFilter intentFilter = new IntentFilter(ChangePropertyBroadcast.FLAG);
        registerReceiver(changeProperty, intentFilter);
        // 共享平台
        PlatformConfig.setWeixin(ConstantManager.WXID, ConstantManager.WXSECRET);
        PlatformConfig.setSinaWeibo("3225411888", "16b68c9ca20e662001adca3ca5617294", "http://www.iyuba.com");
        PlatformConfig.setQQZone("1150062634", "7d9d7157c25ad3c67ff2de5ee69c280c");
        // 讯飞初始化
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=57fc4ab0");
        UMShareConfig config = new UMShareConfig();
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
        UMShareAPI.get(getApplicationContext()).setShareConfig(config);
        // 初始化推送
        pushSdkInit();
    }

    public void pushActivity(Activity activity) {
        activityList.add(activity);
        if (playerService == null) {
            baseHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startService(new Intent(getApplicationContext(), PlayerService.class));
                }
            }, 600);
        }
    }

    public void popActivity(Activity activity) {
        activityList.remove(activity);
    }

    public void clearActivityList() {
        Activity activity;
        for (int i = activityList.size() - 1; i > -1; i--) {
            activity = activityList.get(i);
            if (null != activity) {
                activity.finish();
            }
        }
        activityList.clear();
    }

    private void stopLessonRecord() {
        if (playerService.getCurArticleId() != 0) {
            StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0);
        }
    }

    public void exit() {
        stopService(new Intent(getApplicationContext(), PlayerService.class));
        stopLessonRecord();
        clearActivityList();
        ImageUtil.destroy(this);
        DownloadTask.shutDown();
        ThreadPoolUtil.getInstance().shutdown();
        ImportDatabase.getInstance().closeDatabase();
        try {
            if (proxy != null) {
                proxy.shutdown();
            }
            if (changeProperty != null) {
                unregisterReceiver(changeProperty);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    public int getSleepSecond() {
        return sleepSecond;
    }

    public void setSleepSecond(final int setTime) {
        if (timer != null) {
            timer.cancel();
        }
        if (setTime == 0) {
            sleepSecond = 0;
            return;
        }
        timer = new CountDownTimer(setTime * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sleepSecond = (int) millisUntilFinished / 1000;
                if (sleepSecond == 2) {
                    CustomToast.getInstance().showToast(R.string.sleep_time_finish);
                }
            }

            @Override
            public void onFinish() {
                exit();
            }
        };
        timer.start();
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

    public boolean noMain() {
        if (activityList != null && activityList.size() > 0) {
            for (Activity activity : activityList) {
                if (activity.getLocalClassName().contains("MainActivity")) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public void setPlayerService(PlayerService playerService) {
        this.playerService = playerService;
    }

    public HttpProxyCacheServer getProxy() {
        return proxy == null ? (proxy = new HttpProxyCacheServer(this)) : proxy;
    }
}
