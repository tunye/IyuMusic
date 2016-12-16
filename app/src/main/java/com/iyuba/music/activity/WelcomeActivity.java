package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.ImageView;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.request.apprequest.AdPicRequest;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.WeakReferenceHandler;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/16.
 */
public class WelcomeActivity extends AppCompatActivity {
    private static final int WRITE_EXTERNAL_STORAGE_TASK_CODE = 1;
    boolean autoStart = true;
    Context context;

    ImageView footer, header;
    ArrayList<AdEntity> adEntities;

    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.welcome);
        context = this;
        autoStart = getIntent().getBooleanExtra("autoStart", true);
        footer = (ImageView) findViewById(R.id.welcome_footer);
        header = (ImageView) findViewById(R.id.welcome_header);
        getBannerPic();
        DBoper();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    private void getBannerPic() {
        AdPicRequest.getInstance().exeRequest(AdPicRequest.getInstance().generateUrl(ConstantManager.instance.getAppId()), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                handler.sendEmptyMessage(3);
            }

            @Override
            public void onServerError(String msg) {
                handler.sendEmptyMessage(3);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                adEntities = (ArrayList<AdEntity>) listEntity.getData();
                handler.sendEmptyMessage(0);
            }
        });
    }

    private void DBoper() {
        int lastVersion = ConfigManager.instance.loadInt("version");
        int currentVersion = 0;
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
            currentVersion = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if ((info != null && info.firstInstallTime == info.lastUpdateTime) || lastVersion == 0) {
            ImportDatabase db = ImportDatabase.getInstance();
            db.setVersion(0, 1);// 有需要数据库更改使用
            db.openDatabase();
            SettingConfigManager.instance.setUpgrade(true);
            handler.sendEmptyMessageDelayed(2, 4000);
        } else if (currentVersion == lastVersion) {
            handler.sendEmptyMessageDelayed(1, 4000);
        } else if (currentVersion > lastVersion) {
            if (lastVersion < 72 && SettingConfigManager.instance.getOriginalSize() == 14) {
                SettingConfigManager.instance.setOriginalSize(16);
            }
            ConfigManager.instance.putInt("version", currentVersion);
            SettingConfigManager.instance.setUpgrade(true);
            handler.sendEmptyMessageDelayed(2, 4000);
        }
    }

    private void addShortcut(Class cls, String name, int picResId) {
        Intent shortcutintent = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
        // 不允许重复创建
        shortcutintent.putExtra("duplicate", false);
        // 需要现实的名称
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME, name);
        // 快捷图片
        Parcelable icon = Intent.ShortcutIconResource.fromContext(getApplicationContext(), picResId);
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
        // 发送广播。OK
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), cls);
        intent.setAction("android.intent.action.MAIN");
        intent.addCategory("android.intent.category.LAUNCHER");
        shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
        sendBroadcast(shortcutintent);
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<WelcomeActivity> {
        @Override
        public void handleMessageByRef(WelcomeActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    ImageUtil.loadImage(activity.adEntities.get(0).getPicUrl(), activity.header);
                    ImageUtil.loadImage(activity.adEntities.get(1).getPicUrl(), activity.footer);
                    SettingConfigManager.instance.setADUrl(activity.adEntities.get(0).getPicUrl() + "@@@" + activity.adEntities.get(1).getPicUrl());
                    break;
                case 1:
                    ((MusicApplication) activity.getApplication()).popActivity(activity);
                    if (activity.autoStart) {
                        activity.startActivity(new Intent(activity, MainActivity.class));
                    } else {
                        activity.finish();
                    }
                    break;
                case 2:
                    ((MusicApplication) activity.getApplication()).popActivity(activity);
                    activity.startActivity(new Intent(activity, MainActivity.class));
                    activity.startActivity(new Intent(activity, HelpUseActivity.class));
                    break;
                case 3:
                    String adUrl = SettingConfigManager.instance.getADUrl();
                    if (TextUtils.isEmpty(adUrl)) {
                        activity.footer.setImageResource(R.drawable.default_footer);
                    } else {
                        String[] adUrls = adUrl.split("@@@");
                        ImageUtil.loadImage(adUrls[0], activity.header);
                        ImageUtil.loadImage(adUrls[1], activity.footer);
                    }
                    break;
            }
        }
    }
}
