package com.iyuba.music.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.fragment.MainFragment;
import com.iyuba.music.fragment.MainLeftFragment;
import com.iyuba.music.fragment.StartFragment;
import com.iyuba.music.listener.ILocationListener;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.network.NetWorkType;
import com.iyuba.music.network.PingIPThread;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.LocationUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends BaseSkinActivity implements ILocationListener {
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;
    private static final int WRITE_EXTERNAL_TASK_NO_EXE_CODE = 2;
    private static final int ACCESS_COARSE_LOCATION_TASK_CODE = 3;
    private Context context;
    private DrawerLayout drawerLayout;
    private View drawView, root;
    private TextView toolbarOper;
    private MaterialMenuView menu;
    private NetWorkChangeBroadcastReceiver netWorkChange;
    private ChangePropertyBroadcast changeProperty;
    private boolean isExit = false;// 是否点过退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        prepareForApp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(GetAppColor.getInstance().getAppColor(this));
            window.setNavigationBarColor(GetAppColor.getInstance().getAppColor(this));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.main);
        context = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initBroadcast();
        initWidget();
        setListener();
        includeFragment();
        showWhatsNew();
        drawerLayout.postDelayed(new Runnable() {
            public void run() {
                checkForUpdate();
            }
        }, 10000);
        if (!TextUtils.isEmpty(getIntent().getStringExtra("pushIntent"))) {
            drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainFragment) (getSupportFragmentManager().getFragments().get(1))).setShowItem(2);
                }
            }, 200);
        }
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (!TextUtils.isEmpty(intent.getStringExtra("pushIntent"))) {
            drawerLayout.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ((MainFragment) (getSupportFragmentManager().getFragments().get(1))).setShowItem(2);
                }
            }, 200);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        toolbarOper.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (NetWorkState.getInstance().getNetWorkState().equals(NetWorkState.WIFI_NONET)) {
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
                } else if (NetWorkState.getInstance().getNetWorkState().equals(NetWorkState.NO_NET)) {
                    setNetwork(context);
                } else if (NetWorkState.getInstance().getNetWorkState().equals(NetWorkState.TWOG)) {
                    setBetterNetwork(context);
                }
            }
        }, 2000);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            drawerLayout.closeDrawer(Gravity.LEFT);
        } else {
            pressAgainExit();
        }
    }

    private void initBroadcast() {
        MiPushClient.resumePush(context, null);
        if (SettingConfigManager.getInstance().isUpgrade()) {
            if (SettingConfigManager.getInstance().isPush()) {
                MiPushClient.enablePush(context);
            } else {
                MiPushClient.disablePush(context);
            }
        }
        changeProperty = new ChangePropertyBroadcast();
        IntentFilter intentFilter = new IntentFilter("changeProperty");
        LocalBroadcastManager.getInstance(this).registerReceiver(changeProperty, intentFilter);
        netWorkChange = new NetWorkChangeBroadcastReceiver();
        intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(netWorkChange, intentFilter);
    }

    protected void initWidget() {
        root = findViewById(R.id.root);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        menu = (MaterialMenuView) findViewById(R.id.material_menu);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment fragment = new MainLeftFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.left_drawer, fragment).commit();
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
        drawerLayout.addDrawerListener(new DrawerLayoutStateListener());
    }

    private void prepareForApp() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    ACCESS_COARSE_LOCATION_TASK_CODE);
        } else {
            LocationUtil.getInstance().initLocationUtil();
            LocationUtil.getInstance().refreshGPS(this);
        }
    }

    private void showWhatsNew() {
        if (SettingConfigManager.getInstance().isUpgrade()) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_CODE);
            } else {
                resetDownLoadData();
            }
            SettingConfigManager.getInstance().setUpgrade(false);
            StartFragment.showVersionFeature(context);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_NO_EXE_CODE);
            }
        }
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

    private void includeFragment() {
        Fragment fragment = new MainFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.content_frame, fragment).commit();
        drawerLayout.closeDrawer(drawView);
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == ACCESS_COARSE_LOCATION_TASK_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LocationUtil.getInstance().initLocationUtil();
                LocationUtil.getInstance().refreshGPS(this);
            }
        } else if (requestCode == WRITE_EXTERNAL_TASK_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                resetDownLoadData();
            } else {
                final MaterialDialog materialDialog = new MaterialDialog(context);
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
            if (((MusicApplication) getApplication()).getPlayerService().isPlaying()) {//后台播放
                Intent i = new Intent(Intent.ACTION_MAIN);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addCategory(Intent.CATEGORY_HOME);
                startActivity(i);
            } else {
                ((MusicApplication) RuntimeManager.getApplication()).exit();
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
        drawerLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                isExit = false;
            }
        }, 2000);// 2秒内再点有效
    }

    private void unRegistBroadcast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(changeProperty);
        unregisterReceiver(netWorkChange);
        LocationUtil.getInstance().destroy();
    }

    private void resetDownLoadData() {
        File packageFile = new File(ConstantManager.getInstance().getMusicFolder());
        LocalInfoOp lOp = new LocalInfoOp();
        final ArticleOp articleOp = new ArticleOp();
        if (packageFile.exists() && packageFile.list() != null) {
            StringBuilder StringBuilder = new StringBuilder();
            for (String fileName : packageFile.list()) {
                if (fileName.endsWith(".mp3")) {
                    fileName = fileName.split("\\.")[0];
                    if (fileName.contains("-")) {

                    } else {
                        String regEx = "[^0-9]";
                        Pattern p = Pattern.compile(regEx);
                        Matcher m = p.matcher(fileName);
                        int id = Integer.parseInt(m.replaceAll("").trim());
                        LocalInfo temp = lOp.findDataById(ConstantManager.getInstance().getAppId(), id);
                        if (temp == null || temp.getId() == 0) {
                            temp = new LocalInfo();
                            temp.setId(id);
                            temp.setApp(ConstantManager.getInstance().getAppId());
                            temp.setDownload(1);
                            temp.setDownTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                            lOp.saveData(temp);
                        } else {
                            lOp.updateDownload(id, ConstantManager.getInstance().getAppId(), 1);
                        }
                        StringBuilder.append(id).append(',');
                    }
                } else {
                    new File(ConstantManager.getInstance().getMusicFolder() + File.separator + fileName).delete();
                }
            }
            NewsesRequest.exeRequest(NewsesRequest.generateUrl(StringBuilder.toString()), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {

                }

                @Override
                public void onServerError(String msg) {

                }

                @Override
                public void response(Object object) {
                    BaseListEntity listEntity = (BaseListEntity) object;
                    ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                    for (Article temp : netData) {
                        temp.setApp(ConstantManager.getInstance().getAppId());
                    }
                    articleOp.saveData(netData);
                }
            });
        }
    }

    @Override
    public void notifyChange(int arg, String des) {

    }

    private void checkWifiSignIn() {
        final Snackbar snackbar = Snackbar.make(root, RuntimeManager.getString(R.string.net_wifi_sign_in),
                Snackbar.LENGTH_LONG).setAction(R.string.net_sign_in, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = Uri.parse("https://www.baidu.com");
                intent.setData(content_url);
                startActivity(intent);
            }
        });
        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void setNetwork(Context context) {
        Snackbar snackbar = Snackbar.make(root, context.getString(R.string.net_no_net),
                Snackbar.LENGTH_LONG).setAction(R.string.net_set, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void setBetterNetwork(Context context) {
        Snackbar snackbar = Snackbar.make(root, context.getString(R.string.net_better_net),
                Snackbar.LENGTH_LONG).setAction(R.string.net_set, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(intent);
            }
        });
        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.show();
    }

    private class DrawerLayoutStateListener extends
            DrawerLayout.SimpleDrawerListener {
        /**
         * 当导航菜单滑动的时候被执行
         */
        @Override
        public void onDrawerSlide(View drawerView, float slideOffset) {
            menu.setTransformationOffset(MaterialMenuDrawable.AnimationState.BURGER_ARROW, 2 - slideOffset);
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

    class ChangePropertyBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ((MusicApplication) getApplication()).clearActivityList();
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String oldState = NetWorkState.getInstance().getNetWorkState();
            String netWorkState = NetWorkType.getNetworkType(context);
            NetWorkState.getInstance().setNetWorkState(netWorkState);
            if (!netWorkState.equals(oldState)) {
                if (TextUtils.equals(netWorkState, NetWorkState.NO_NET)) {
                    setNetwork(context);
                } else if (TextUtils.equals(oldState, NetWorkState.WIFI)) {
                    final Snackbar snackbar = Snackbar.make(root, context.getString(R.string.net_cut_wifi),
                            Snackbar.LENGTH_LONG).setAction(R.string.app_know, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
                    snackbar.show();
                } else if (TextUtils.equals(netWorkState, NetWorkState.WIFI)) {
                    PingIPThread pingIPThread = new PingIPThread(new IOperationResult() {
                        @Override
                        public void success(Object object) {
                        }

                        @Override
                        public void fail(Object object) {
                            NetWorkState.getInstance().setNetWorkState(NetWorkState.WIFI_NONET);
                            checkWifiSignIn();
                        }
                    });
                    pingIPThread.start();
                }
            }
        }
    }
}
