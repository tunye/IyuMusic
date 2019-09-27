package com.iyuba.music.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.buaa.ct.appskin.BaseSkinActivity;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.util.PermissionPool;
import com.buaa.ct.core.util.ThreadUtils;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.qrcode.QRCode;
import com.buaa.ct.qrcode.sample.CustomQRCodeTestActivity;
import com.buaa.ct.swipe.SmartSwipe;
import com.buaa.ct.swipe.SmartSwipeWrapper;
import com.buaa.ct.swipe.SwipeConsumer;
import com.buaa.ct.swipe.consumer.SlidingConsumer;
import com.buaa.ct.swipe.consumer.StretchConsumer;
import com.buaa.ct.swipe.listener.SimpleSwipeListener;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.fragment.MainFragment;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.receiver.NetWorkChangeBroadcastReceiver;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.view.SideFrameLayout;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;

public class MainActivity extends BaseSkinActivity {
    public static final int REQUEST_SCAN = 701;
    private Context context;
    private TextView search, scan;
    private View mainMask;
    private SideFrameLayout sideFrameLayout;
    private SlidingConsumer slidingConsumer;
    private MaterialMenuView menu;
    private NetWorkChangeBroadcastReceiver netWorkChange;
    private boolean isExit = false;// 是否点过退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangePropery.setAppConfig(this);
        if (hasPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            AccountManager.getInstance().getGPS();
        }
        setContentView(R.layout.main);
        context = this;
        initBroadcast();
        initWidget();
        setListener();
        if (ConfigManager.getInstance().isUpgrade()) {
            ThreadUtils.postOnUiThreadDelay(new Runnable() {
                @Override
                public void run() {
                    showWhatsNew();
                }
            }, 500);
        } else {
            StartFragment.checkTmpFile();
            if (getIntent().getBooleanExtra("pushIntent", false)) {
                directToFragment(2);
            }
            checkForUpdate();
        }
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("pushIntent", false)) {
            directToFragment(2);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        sideFrameLayout.refresh();
    }

    @Override
    public void onBackPressed() {
        if (slidingConsumer.isOpened()) {
            slidingConsumer.smoothClose();
        } else {
            pressAgainExit();
        }
    }

    private void initBroadcast() {
        if (ConfigManager.getInstance().isUpgrade()) {
            if (ConfigManager.getInstance().isPush()) {
                MiPushClient.enablePush(RuntimeManager.getInstance().getContext());
            } else {
                MiPushClient.disablePush(RuntimeManager.getInstance().getContext());
            }
        }
        netWorkChange = new NetWorkChangeBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkChange, intentFilter);
    }

    public void initWidget() {
        View toolBar = findViewById(R.id.toolbar);
        toolBar.setPadding(0, RuntimeManager.getInstance().getTopNavBarHeight(), 0, 0);
        search = findViewById(R.id.main_search);
        scan = findViewById(R.id.main_qrcode);
        menu = findViewById(R.id.material_menu);
        mainMask = findViewById(R.id.main_mask);

        MainFragment mainFragment = new MainFragment();
        mainFragment.setiOperationResultInt(new IOperationResultInt() {
            @Override
            public void performance(int index) {
                if (index == 0) {
                    slidingConsumer.setEdgeSize((int) (0.75f * RuntimeManager.getInstance().getScreenWidth()));
                } else {
                    slidingConsumer.setEdgeSize(-1);
                }
            }
        });
        sideFrameLayout = new SideFrameLayout(context);
        sideFrameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        SmartSwipeWrapper horizontalMenuWrapper = SmartSwipe.wrap(sideFrameLayout).addConsumer(new StretchConsumer()).enableVertical().getWrapper();
        slidingConsumer = new SlidingConsumer()
                .setDrawerExpandable(true)
                .setLeftDrawerView(horizontalMenuWrapper)
                .showScrimAndShadowOutsideContentView()
                .setScrimColor(0x44000000)
                .setShadowSize(RuntimeManager.getInstance().dip2px(3))
                .setShadowColor(0x80000000)
                .addListener(new SimpleSwipeListener() {
                    @Override
                    public void onSwipeProcess(SmartSwipeWrapper wrapper, SwipeConsumer consumer, int direction, boolean settling, float progress) {
                        super.onSwipeProcess(wrapper, consumer, direction, settling, progress);
                        menu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW, 2 - progress);
                        if (direction == SwipeConsumer.DIRECTION_LEFT) {
                            mainMask.setAlpha(progress);
                            if (progress == 0) {
                                mainMask.setVisibility(View.GONE);
                            } else {
                                mainMask.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                })
                .setEdgeSize((int) (0.75f * RuntimeManager.getInstance().getScreenWidth()))
                .as(SlidingConsumer.class);
        slidingConsumer.setRelativeMoveFactor(0.5F);
        SmartSwipe.wrap(this).addConsumer(slidingConsumer);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mainFragment).commit();
    }

    public void setListener() {
        menu.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (menu.getDrawable().getIconState().equals(MaterialMenuDrawable.IconState.BURGER)) {
                    slidingConsumer.smoothLeftOpen();
                    menu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
                } else {
                    slidingConsumer.smoothClose();
                    menu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
                }
            }
        });
        search.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });
        scan.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View v) {
                if (!hasPermission(Manifest.permission.CAMERA)) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, PermissionPool.CAMERA);
                } else {
                    CustomQRCodeTestActivity.start(MainActivity.this, REQUEST_SCAN, com.buaa.ct.qrcode.R.style.QRCodeTheme_Custom);
                }
            }
        });
        mainMask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do nothing
            }
        });
    }

    private void showWhatsNew() {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            StartFragment.resetDownLoadData();
        }
        ConfigManager.getInstance().setUpgrade(false);
        StartFragment.showVersionFeature(context);
    }

    private boolean hasPermission(String permission){
        return ContextCompat.checkSelfPermission(this,permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void directToFragment(final int pos){
        ThreadUtils.postOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                ((MainFragment) (getSupportFragmentManager().findFragmentById(R.id.content_frame))).setShowItem(pos);
                if (pos != 0) {
                    slidingConsumer.setEdgeSize((int) (0.75f * RuntimeManager.getInstance().getScreenWidth()));
                }
            }
        }, 100);
    }

    private void checkForUpdate() {
        StartFragment.checkUpdate(context, new IOperationResult() {
            @Override
            public void success(Object object) {
                CustomDialog.updateDialog(context, object.toString());
            }

            @Override
            public void fail(Object object) {
            }
        });
        StartFragment.cleanLocalData();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sideFrameLayout.destroy();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 1 && permissions[0].equalsIgnoreCase(Manifest.permission.CAMERA)) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                CustomQRCodeTestActivity.start(this, REQUEST_SCAN, com.buaa.ct.qrcode.R.style.QRCodeTheme_Custom);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SCAN && resultCode == Activity.RESULT_OK && data.getExtras() != null) {
            String result = data.getExtras().getString(QRCode.RESULT_DATA);
            if (TextUtils.isEmpty(result)) {
                CustomToast.getInstance().showToast("未发现二维码");
            } else if (result.startsWith(NullActivity.appScheme)) {
                NullActivity.exePushData(context, result);
            } else if (result.startsWith(NullActivity.webScheme)) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", result);
                context.startActivity(intent);
            } else {
                CustomToast.getInstance().showToast("无法识别该二维码");
            }
        } else {
            sideFrameLayout.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void finish() {
        super.finish();
        unRegistBroadcast();
    }

    private void pressAgainExit() {
        if (isExit) {
            if (((MusicApplication) getApplication()).getPlayerService().isPlaying()) {   // 后台播放
                ((MusicApplication) getApplication()).clearActivityList();
            } else {
                new LocalInfoOp().changeDownloadToStop();
                Utils.getMusicApplication().exit();
            }
        } else {
            if (((MusicApplication) getApplication()).getPlayerService().isPlaying()) {//后台播放
                CustomToast.getInstance().showToast(R.string.alert_home, CustomToast.LENGTH_LONG);
            } else {
                CustomToast.getInstance().showToast(R.string.alert_exit, CustomToast.LENGTH_LONG);
            }
            doExitInOneSecond();
        }
    }

    private void doExitInOneSecond() {
        isExit = true;
        ThreadUtils.postOnUiThreadDelay(new Runnable() {
            @Override
            public void run() {
                isExit = false;
            }
        }, 2000);// 2秒内再点有效
    }

    private void unRegistBroadcast() {
        if (netWorkChange != null) {
            unregisterReceiver(netWorkChange);
        }
    }
}
