package com.iyuba.music.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.util.ImageUtil;
import com.buaa.ct.core.util.NotchUtils;
import com.buaa.ct.core.util.PermissionPool;
import com.buaa.ct.core.util.SPUtils;
import com.iyuba.music.R;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.AdPicRequest;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.RoundProgressBar;
import com.youdao.sdk.nativeads.NativeErrorCode;
import com.youdao.sdk.nativeads.NativeResponse;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.YouDaoNative;

/**
 * Created by 10202 on 2015/11/16.
 */
public class WelcomeActivity extends BaseActivity {
    public static final int HANDLER_REFRESH_PROGRESS = 0;
    public static final String NORMAL_START = "normalStart";
    private View escapeAd;
    private ImageView header;
    private RoundProgressBar welcomeAdProgressbar;              // 等待进度条
    private boolean normalStart = true;                         // 是否正常进入程序
    private boolean showGuide = false;                          // 是否跳转开屏引导
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }

        normalStart = getIntent().getBooleanExtra(NORMAL_START, true);
        if (Utils.getMusicApplication().getPlayerService() != null && Utils.getMusicApplication().getPlayerService().isPlaying()) {
            startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            finish();
        } else {
            Utils.getMusicApplication().setShowSignInToast(true);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.welcome;
    }

    @Override
    public void onActivityResumed() {
        super.onActivityResumed();
        requestMultiPermission(new int[]{PermissionPool.ACCESS_COARSE_LOCATION, PermissionPool.WRITE_EXTERNAL_STORAGE},
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeMessages(HANDLER_REFRESH_PROGRESS);
    }

    @Override
    public void onAccreditSucceed(int requestCode) {
        if (welcomeAdProgressbar.getProgress() > 200) {
            handler.sendEmptyMessage(HANDLER_REFRESH_PROGRESS);
        } else {
            initWelcomeAdProgress();
        }
    }

    @Override
    public void onAccreditFailure(int requestCode) {
        boolean hasStoragePermission = hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        onRequestPermissionDenied(hasStoragePermission ? getString(R.string.location_permission_content) : getString(R.string.storage_permission_content),
                new int[]{PermissionPool.ACCESS_COARSE_LOCATION, PermissionPool.WRITE_EXTERNAL_STORAGE},
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void initWidget() {
        welcomeAdProgressbar = findViewById(R.id.welcome_ad_progressbar);
        escapeAd = findViewById(R.id.welcome_escape_ad);
        header = findViewById(R.id.welcome_header);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && NotchUtils.hasNotchScreen())) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) escapeAd.getLayoutParams();
            layoutParams.topMargin += NotchUtils.getNotchOffset();
            escapeAd.setLayoutParams(layoutParams);
        }
        getBannerPic();
        initialDatabase();
    }

    @Override
    public void setListener() {
        escapeAd.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                handler.removeMessages(HANDLER_REFRESH_PROGRESS);
                startNexActivity();
            }
        });
    }

    private void initWelcomeAdProgress() {
        welcomeAdProgressbar.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
        welcomeAdProgressbar.setProgress(150);                  // 为progress设置一个初始值
        welcomeAdProgressbar.setMax(4000);                      // 总计等待4s
        handler.sendEmptyMessageDelayed(HANDLER_REFRESH_PROGRESS, 500); // 半秒刷新进度
    }

    private void getBannerPic() {
        String adUrl = ConfigManager.getInstance().getADUrl();
        if (TextUtils.isEmpty(adUrl)) {
            header.setImageResource(R.drawable.default_header);
            loadAdAgain(false);
        } else {
            AdEntity adEntity = JSON.parseObject(adUrl, AdEntity.class);
            if (adEntity != null) {
                parseAd(adEntity);
                loadAdAgain(false);
            } else {
                loadAdAgain(true);
            }
        }
    }

    private void parseAd(final AdEntity adEntity) {
        if (adEntity.youDaoAd()) {
            if (isNetworkAvailable()) {
                loadYouDaoSplash();
            } else {
                header.setImageResource(R.drawable.default_header);
            }
        } else {
            if (adEntity.webExpire()) {
                loadAdAgain(true);
            }
            if (isNetworkAvailable()) {
                AppImageUtil.loadImage(adEntity.getPicUrl(), header, R.drawable.default_header);
                header.setOnClickListener(new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        int nextActivity = normalStart ? 0 : 1;
                        WelcomeAdWebView.launch(context, TextUtils.isEmpty(adEntity.getLoadUrl()) ?
                                "http://app.iyuba.cn/android/" : adEntity.getLoadUrl(), nextActivity);
                        finish();
                    }
                });
            } else {
                header.setImageResource(R.drawable.default_header);
            }
        }
    }

    private void loadYouDaoSplash() {
        YouDaoNative youdaoNative = new YouDaoNative(context, "a710131df1638d888ff85698f0203b46",
                new YouDaoNative.YouDaoNativeNetworkListener() {
                    @Override
                    public void onNativeLoad(final NativeResponse nativeResponse) {
                        header.setOnClickListener(new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                nativeResponse.handleClick(header);
                                int nextActivity = normalStart ? 0 : 1;
                                WelcomeAdWebView.launch(context, nativeResponse.getClickDestinationUrl(), nextActivity);
                                finish();
                            }
                        });
                        ImageUtil.loadImage(nativeResponse.getMainImageUrl(), header, null, new ImageUtil.OnBitmapLoaded() {
                            @Override
                            public void onImageLoaded(Bitmap bitmap) {
                                nativeResponse.recordImpression(header);
                            }

                            @Override
                            public void onImageLoadFailed() {
                                header.setImageResource(R.drawable.default_header);
                            }
                        });
                    }

                    @Override
                    public void onNativeFail(NativeErrorCode nativeErrorCode) {
                        header.setImageResource(R.drawable.default_header);
                    }
                });
        RequestParameters requestParameters = new RequestParameters.Builder().build();
        youdaoNative.makeRequest(requestParameters);
    }

    private void loadAdAgain(final boolean useThisTime) {
        RequestClient.requestAsync(new AdPicRequest(), new SimpleRequestCallBack<AdEntity>() {
            @Override
            public void onSuccess(AdEntity adEntity) {
                ConfigManager.getInstance().setADUrl(JSON.toJSONString(adEntity));
                if (useThisTime) {
                    parseAd(adEntity);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                if (useThisTime) {
                    header.setImageResource(R.drawable.default_header);
                }
            }
        });
    }

    private void startNexActivity() {
        if (normalStart) {
            if (showGuide) {
                startActivity(new Intent(context, HelpUseActivity.class));
            } else {
                startActivity(new Intent(context, MainActivity.class));
            }
        } else {
            startActivity(new Intent(context, LocalMusicActivity.class));
            StudyManager.getInstance().setApp("101");
        }
        finish();
    }

    private void initialDatabase() {
        int lastVersion = SPUtils.loadInt(ConfigManager.getInstance().getPreferences(), "version");
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
        }
    }

    private void appUpgrade(int currentVersion) {
        showGuide = true;
        SPUtils.putInt(ConfigManager.getInstance().getPreferences(), "version", currentVersion);
        ConfigManager.getInstance().setUpgrade(true);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo activeInfo = connectivityManager.getActiveNetworkInfo();
            if (activeInfo != null) {
                return activeInfo.isConnected();
            } else {
                return false;
            }
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<WelcomeActivity> {
        @Override
        public void handleMessageByRef(WelcomeActivity activity, Message msg) {
            int progress = activity.welcomeAdProgressbar.getProgress();
            if (progress < 4000) {
                progress = progress < 500 ? 500 : progress + 500;
                activity.welcomeAdProgressbar.setProgress(progress);
                activity.handler.sendEmptyMessageDelayed(HANDLER_REFRESH_PROGRESS, 500);
            } else {
                activity.welcomeAdProgressbar.setVisibility(View.INVISIBLE);
                activity.startNexActivity();
            }
        }
    }
}
