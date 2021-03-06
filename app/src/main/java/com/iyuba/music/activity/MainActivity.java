package com.iyuba.music.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.buaa.ct.appskin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.fragment.MainFragment;
import com.iyuba.music.fragment.MainLeftFragment;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.network.NetWorkType;
import com.iyuba.music.network.PingIPThread;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.lang.ref.WeakReference;

public class MainActivity extends BaseSkinActivity {
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;
    private static final int WRITE_EXTERNAL_TASK_NO_EXE_CODE = 2;
    private static final int ACCESS_COARSE_LOCATION_TASK_CODE = 3;
    private Context context;
    private DrawerLayout drawerLayout;
    private View drawView, root;
    private TextView toolbarOper;
    private MaterialMenuView menu;
    private NetWorkChangeBroadcastReceiver netWorkChange;
    private boolean isExit = false;// 是否点过退出
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ChangePropery.setAppConfig(this);
        setContentView(R.layout.main);
        context = this;
        requestLocation();
        initBroadcast();
        initWidget();
        setListener();
        if (ConfigManager.getInstance().isUpgrade()) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showWhatsNew();
                }
            }, 500);
        } else {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_NO_EXE_CODE);
            }
            StartFragment.checkTmpFile();
            if (getIntent().getBooleanExtra("pushIntent", false)) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((MainFragment) (getSupportFragmentManager().findFragmentById(R.id.content_frame))).setShowItem(2);
                    }
                }, 200);
            }
            handler.postDelayed(new Runnable() {
                public void run() {
                    checkForUpdate();
                }
            }, 1000);
        }
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.getBooleanExtra("pushIntent", false)) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainFragment) (getSupportFragmentManager().findFragmentById(R.id.content_frame))).setShowItem(2);
                }
            }, 200);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                switch (NetWorkState.getInstance().getNetWorkState()) {
                    case NetWorkState.WIFI_NONET:
                        PingIPThread pingIPThread = new PingIPThread(new IOperationResult() {
                            @Override
                            public void success(Object object) {
                                NetWorkState.getInstance().setNetWorkState(NetWorkState.WIFI);
                            }

                            @Override
                            public void fail(Object object) {
                                checkWifiSignIn();
                            }
                        });
                        pingIPThread.start();
                        break;
                    case NetWorkState.NO_NET:
                        setNetwork(context);
                        break;
                    case NetWorkState.TWOG:
                        setBetterNetwork(context);
                        break;
                }
            }
        }, 500);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.START)) {
            drawerLayout.closeDrawer(Gravity.START);
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
        netWorkChange = new NetWorkChangeBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkChange, intentFilter);
    }

    protected void initWidget() {
        root = findViewById(R.id.root);
        toolbarOper = findViewById(R.id.toolbar_oper);
        menu = findViewById(R.id.material_menu);
        drawerLayout = findViewById(R.id.drawer_layout);
        MainLeftFragment mainLeftFragment = new MainLeftFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.left_drawer, mainLeftFragment).commitAllowingStateLoss();
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, mainFragment).commitAllowingStateLoss();
        drawView = findViewById(R.id.left_drawer);
    }

    protected void setListener() {
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (menu.getDrawable().getIconState().equals(MaterialMenuDrawable.IconState.BURGER)) {
                    drawerLayout.openDrawer(drawView);
                    menu.animatePressedState(MaterialMenuDrawable.IconState.BURGER);
                } else {
                    drawerLayout.closeDrawer(drawView);
                    menu.animatePressedState(MaterialMenuDrawable.IconState.ARROW);
                }
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, SearchActivity.class));
            }
        });
        drawerLayout.addDrawerListener(new DrawerLayoutStateListener(this));
    }

    private void requestLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_TASK_CODE);
        } else {
            AccountManager.getInstance().getGPS();
        }
    }

    private void showWhatsNew() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_CODE);
        } else {
            StartFragment.resetDownLoadData();
        }
        ConfigManager.getInstance().setUpgrade(false);
        StartFragment.showVersionFeature(context);
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
    public void finish() {
        super.finish();
        unRegistBroadcast();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_COARSE_LOCATION_TASK_CODE && grantResults.length != 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AccountManager.getInstance().getGPS();
            }
        } else if (requestCode == WRITE_EXTERNAL_TASK_CODE && grantResults.length != 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                StartFragment.resetDownLoadData();
            } else {
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setTitle(R.string.storage_permission);
                materialDialog.setMessage(R.string.storage_permission_content);
                materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_TASK_CODE);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.show();
            }
        }
    }

    private void pressAgainExit() {
        if (isExit) {
            if (((MusicApplication) getApplication()).getPlayerService().isPlaying()) {   // 后台播放
//                直接返回桌面
//                Intent i = new Intent(Intent.ACTION_MAIN);
//                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                i.addCategory(Intent.CATEGORY_HOME);
//                startActivity(i);
                ((MusicApplication) getApplication()).clearActivityList();
            } else {
                new LocalInfoOp().changeDownloadToStop();
                ((MusicApplication) getApplication()).exit();
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
        handler.postDelayed(new Runnable() {
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

    private void checkWifiSignIn() {
        CustomSnackBar.make(root, context.getString(R.string.net_wifi_sign_in)).danger(context.getString(R.string.net_sign_in), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse("http://m.baidu.com");
                    intent.setData(content_url);
                    startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setNetwork(Context context) {
        CustomSnackBar.make(root, context.getString(R.string.net_no_net)).warning(context.getString(R.string.net_set), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private void setBetterNetwork(Context context) {
        CustomSnackBar.make(root, context.getString(R.string.net_better_net)).warning(context.getString(R.string.net_set), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private static class DrawerLayoutStateListener extends
            DrawerLayout.SimpleDrawerListener {
        private final WeakReference<MainActivity> mWeakReference;

        public DrawerLayoutStateListener(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        /**
         * 当导航菜单滑动的时候被执行
         */
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            if (mWeakReference.get() != null) {
                mWeakReference.get().menu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW, 2 - slideOffset);
            }
        }

        /**
         * 当导航菜单打开时执行
         */
        @Override
        public void onDrawerOpened(View drawerView) {
        }

        /**
         * 当导航菜单关闭时执行
         */
        @Override
        public void onDrawerClosed(View drawerView) {
        }
    }

    static class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {
        private final WeakReference<MainActivity> mWeakReference;

        public NetWorkChangeBroadcastReceiver(MainActivity activity) {
            mWeakReference = new WeakReference<>(activity);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (mWeakReference.get() != null) {
                String oldState = NetWorkState.getInstance().getNetWorkState();
                String netWorkState = NetWorkType.getNetworkType(context);
                NetWorkState.getInstance().setNetWorkState(netWorkState);
                if (!netWorkState.equals(oldState)) {
                    if (TextUtils.equals(netWorkState, NetWorkState.NO_NET)) {
                        mWeakReference.get().setNetwork(context);
                    } else if (TextUtils.equals(oldState, NetWorkState.WIFI)) {
                        CustomSnackBar.make(mWeakReference.get().root, context.getString(R.string.net_cut_wifi)).warning();
                    } else if (TextUtils.equals(netWorkState, NetWorkState.WIFI)) {
                        PingIPThread pingIPThread = new PingIPThread(new IOperationResult() {
                            @Override
                            public void success(Object object) {
                            }

                            @Override
                            public void fail(Object object) {
                                NetWorkState.getInstance().setNetWorkState(NetWorkState.WIFI_NONET);
                                mWeakReference.get().checkWifiSignIn();
                            }
                        });
                        pingIPThread.start();
                    }
                }
            }
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<MainActivity> {
        @Override
        public void handleMessageByRef(MainActivity mainActivity, Message msg) {

        }
    }
}
