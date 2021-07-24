package com.iyuba.music;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.support.multidex.MultiDex;

import com.buaa.ct.appskin.SkinManager;
import com.buaa.ct.appskin.callback.ISkinChangedListener;
import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.network.NetWorkState;
import com.buaa.ct.core.network.NetWorkType;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.util.ThreadUtils;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.swipe.SmartSwipeBack;
import com.buaa.ct.videocache.httpproxy.HttpProxyCacheServer;
import com.bumptech.glide.Glide;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.WelcomeActivity;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.Utils;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;
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
    private PlayerService playerService;
    private HttpProxyCacheServer proxy;
    private ChangePropertyBroadcast changeProperty;
    private CountDownTimer timer;
    private boolean showSignInToast;

    @Override
    public void onCreate() {
        super.onCreate();//必须调用父类方法
        RuntimeManager.getInstance().initRuntimeManager(this);
        ChangePropery.updateLanguageMode(ConfigManager.getInstance().getLanguage());
        SmartSwipeBack.activityBezierBack(this, activitySwipeBackFilter);
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
        SkinManager.getInstance().addChangedListener(new ISkinChangedListener() {
            @Override
            public void onSkinChanged() {
                Utils.getMusicApplication().setTheme(GetAppColor.getInstance().getAppTheme());
//                IyubaDialog.styleId = GetAppColor.getInstance().getDialogTheme();
//                MyMaterialDialog.styleId = GetAppColor.getInstance().getMaterialDialogTheme();
            }
        });
        prepareLazy();
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
        PlatformConfig.setSinaWeibo("3225411888", "16b68c9ca20e662001adca3ca5617294", "http://www.iyuba.cn");
        PlatformConfig.setQQZone("1150062634", "7d9d7157c25ad3c67ff2de5ee69c280c");
        UMShareConfig config = new UMShareConfig();
        config.setSinaAuthType(UMShareConfig.AUTH_TYPE_SSO);
        UMShareAPI.get(getApplicationContext()).setShareConfig(config);
        // 讯飞初始化
        SpeechUtility.createUtility(getApplicationContext(), SpeechConstant.APPID + "=57fc4ab0");
        // 初始化推送
        if (ConfigManager.getInstance().isPush()) {
            MiPushClient.registerPush(this, ConstantManager.MIPUSH_APP_ID, ConstantManager.MIPUSH_APP_KEY);
        }
    }

    public void pushActivity(final Activity activity) {
        activityList.add(activity);
        if (playerService == null) {
            ThreadUtils.postOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                        startForegroundService(new Intent(getApplicationContext(), PlayerService.class));
                    } else {
                        startService(new Intent(getApplicationContext(), PlayerService.class));
                    }
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

    public void exit() {
        DownloadTask.shutDown();
        stopService(new Intent(getApplicationContext(), PlayerService.class));
        if (playerService.getCurArticleId() != 0) {
            StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0, false);
        }
        ImportDatabase.getInstance().closeDatabase();
        if (proxy != null) {
            proxy.shutdown();
        }
        if (changeProperty != null) {
            unregisterReceiver(changeProperty);
        }
        clearActivityList();
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

    public Activity getForeground() {
        if (activityList != null && activityList.size() > 0) {
            return activityList.get(activityList.size() - 1);
        }
        return null;
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

    public boolean isShowSignInToast() {
        return showSignInToast;
    }

    public void setShowSignInToast(boolean showSignInToast) {
        this.showSignInToast = showSignInToast;
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

    private SmartSwipeBack.ActivitySwipeBackFilter activitySwipeBackFilter = new SmartSwipeBack.ActivitySwipeBackFilter() {
        @Override
        public boolean onFilter(Activity activity) {
            return !(activity instanceof MainActivity) && !(activity instanceof WelcomeActivity);
        }
    };
}
