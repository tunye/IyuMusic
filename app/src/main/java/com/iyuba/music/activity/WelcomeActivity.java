package com.iyuba.music.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.AdPicRequest;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.RoundProgressBar;

/**
 * Created by 10202 on 2015/11/16.
 */
public class WelcomeActivity extends AppCompatActivity {
    public static final String NORMAL_START = "normalStart";
    private View escapeAd;
    private ImageView header;
    private RoundProgressBar welcomeAdProgressbar;              // 等待进度条
    private AdEntity adEntity;                                  // 开屏广告对象
    private boolean normalStart = true;                         // 是否正常进入程序
    private boolean showAd = false;                             // 是否进入广告
    private boolean showGuide = false;                          // 是否跳转开屏引导
    private Context context;
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);
        context = this;
        normalStart = getIntent().getBooleanExtra(NORMAL_START, true);
        if (RuntimeManager.getApplication().getPlayerService() != null && RuntimeManager.getApplication().getPlayerService().isPlaying()) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);
            }
            getBannerPic();
            initWidget();
            setListener();
            initialDatabase();
            RuntimeManager.getInstance().setShowSignInToast(true);
            ((MusicApplication) getApplication()).pushActivity(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacksAndMessages(null);
    }

    private void initWidget() {
        welcomeAdProgressbar = (RoundProgressBar) findViewById(R.id.welcome_ad_progressbar);
        escapeAd = findViewById(R.id.welcome_escape_ad);
        header = (ImageView) findViewById(R.id.welcome_header);
    }

    private void setListener() {
        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adEntity != null) {
                    showAd = true;
                    int nextActivity = normalStart ? 0 : 1;
                    WelcomeAdWebView.launch(context, TextUtils.isEmpty(adEntity.getLoadUrl()) ?
                            "http://app.iyuba.com/android/" : adEntity.getLoadUrl(), nextActivity);
                    ((MusicApplication) getApplication()).popActivity(WelcomeActivity.this);
                    finish();
                }
            }
        });
        escapeAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeMessages(1);
                handler.removeMessages(3);
                handler.sendEmptyMessage(1);
            }
        });
    }

    private void initWelcomeAdProgress() {
        welcomeAdProgressbar.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
        welcomeAdProgressbar.setProgress(150);                  // 为progress设置一个初始值
        welcomeAdProgressbar.setMax(4000);                      // 总计等待4s
        handler.sendEmptyMessageDelayed(3, 500);                // 半秒刷新进度
    }

    private void getBannerPic() {
        handler.sendEmptyMessage(2);
        AdPicRequest.exeRequest(AdPicRequest.generateUrl(), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                adEntity = (AdEntity) apiEntity.getData();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void initialDatabase() {
        int lastVersion = ConfigManager.getInstance().loadInt("version");
        int currentVersion = 0;
        PackageInfo info;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if (lastVersion == 0) {
            ImportDatabase db = ImportDatabase.getInstance();
            db.setVersion(0, 1);                                          // 有需要数据库更改使用
            db.openDatabase();
            appUpgrade(currentVersion);
        } else if (currentVersion > lastVersion) {
            if (lastVersion < 72 && ConfigManager.getInstance().getOriginalSize() == 14) {
                ConfigManager.getInstance().setOriginalSize(16);          // 修改默认文字大小
            }
            if (lastVersion < 83) {                                       // 广告获取方式改变
                ConfigManager.getInstance().setADUrl("");
                ConfigManager.getInstance().setDownloadMode(1);
            }
            appUpgrade(currentVersion);
        } else {
            initWelcomeAdProgress();
        }
        handler.sendEmptyMessageDelayed(1, 4500);
    }

    private void appUpgrade(int currentVersion) {
        showGuide = true;
        ConfigManager.getInstance().putInt("version", currentVersion);
        ConfigManager.getInstance().setUpgrade(true);
        escapeAd.setVisibility(View.GONE);
        welcomeAdProgressbar.setVisibility(View.GONE);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<WelcomeActivity> {
        @Override
        public void handleMessageByRef(WelcomeActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    ConfigManager.getInstance().setADUrl(activity.adEntity.getPicUrl()
                            + "@@@" + activity.adEntity.getLoadUrl());
                    break;
                case 1:
                    if (!activity.showAd) {
                        if (activity.normalStart) {
                            if (activity.showGuide) {
                                activity.startActivity(new Intent(activity, HelpUseActivity.class));
                            } else {
                                activity.startActivity(new Intent(activity, MainActivity.class));
                            }
                        } else {
                            activity.startActivity(new Intent(activity, LocalMusicActivity.class));
                            StudyManager.getInstance().setApp("101");
                        }
                        ((MusicApplication) activity.getApplication()).popActivity(activity);
                        activity.finish();
                    }
                    break;
                case 2:
                    String adUrl = ConfigManager.getInstance().getADUrl();
                    activity.adEntity = new AdEntity();
                    if (TextUtils.isEmpty(adUrl)) {
                        activity.adEntity.setPicUrl("http://app.iyuba.com/dev/upload/1478933401279.png");
                        activity.adEntity.setLoadUrl("");
                        activity.header.setImageResource(R.drawable.default_header);
                    } else if (!activity.isDestroyed()) {
                        String[] adUrls = adUrl.split("@@@");
                        activity.adEntity.setPicUrl(adUrls[0]);
                        activity.adEntity.setLoadUrl(adUrls[1]);
                        ImageUtil.loadImage(adUrls[0], activity.header);
                    }
                    break;
                case 3:
                    int progress = activity.welcomeAdProgressbar.getProgress();
                    if (progress < 4000) {
                        progress = progress < 500 ? 500 : progress + 500;
                        activity.welcomeAdProgressbar.setProgress(progress);
                        activity.handler.sendEmptyMessageDelayed(3, 500);
                    } else {
                        activity.welcomeAdProgressbar.setVisibility(View.INVISIBLE);
                    }
                    break;
            }
        }
    }
}
