package com.iyuba.music.activity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialmenu.MaterialMenuView;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.main.DownloadSongActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.ArticleOp;
import com.iyuba.music.entity.artical.LocalInfo;
import com.iyuba.music.entity.artical.LocalInfoOp;
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
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.LocationUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.drakeet.materialdialog.MaterialDialog;

public class MainActivity extends BaseSkinActivity implements ILocationListener {
    private static final int WRITE_EXTERNAL_TASK_CODE = 1;
    private static final int ACCESS_COARSE_LOCATION_TASK_CODE = 3;
    protected RelativeLayout toolBarLayout;
    private Context context;
    private DrawerLayout drawerLayout;
    private View drawView;
    private TextView toolbarOper;
    private MaterialMenuView menu;
    private ChangePropertyBroadcast changeProperty;
    private boolean isExit = false;// 是否点过退出

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        prepareForApp();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(GetAppColor.instance.getAppColor(this));
            window.setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.main);
        context = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initBroadcast();
        initWidget();
        setListener();
        changeUIByPara();
        includeFragment();
        showWhatsNew();
        drawerLayout.postDelayed(new Runnable() {
            public void run() {
                checkForUpdate();
            }
        }, 8000);
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
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
        //MobclickAgent.updateOnlineConfig(context);//友盟在线参数配置，在analyse不支持了
        PushAgent mPushAgent = PushAgent.getInstance(context);//推送配置
        if (SettingConfigManager.instance.isPush()) {
            mPushAgent.enable();
            PushAgent.getInstance(context).onAppStart();
        } else {
            mPushAgent.disable();
        }
        changeProperty = new ChangePropertyBroadcast();
        IntentFilter intentFilter = new IntentFilter("changeProperty");
        LocalBroadcastManager.getInstance(this).registerReceiver(changeProperty, intentFilter);
    }

    protected void initWidget() {
        toolBarLayout = (RelativeLayout) findViewById(R.id.toolbar_title_layout);
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

    protected void changeUIByPara() {
        if (TextUtils.equals(NetWorkState.getInstance().getNetWorkState(), "NO-NET")) {
            final MaterialDialog mMaterialDialog = new MaterialDialog(context);
            mMaterialDialog.setTitle(R.string.app_name)
                    .setMessage(R.string.no_net_to_local)
                    .setPositiveButton(R.string.to_local_go, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                            context.startActivity(new Intent(context, DownloadSongActivity.class));
                        }
                    })
                    .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mMaterialDialog.dismiss();
                        }
                    });
            mMaterialDialog.show();
        }
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
        if (SettingConfigManager.instance.isUpgrade()) {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle("新版本特性");
            StringBuilder sb = new StringBuilder();
            sb.append("1.[软件维护] 修复使用过程中的若干异常问题").append("\n");
            sb.append("2.[海量曲库] 用户上传的经典乐曲，畅享英文歌曲");
            materialDialog.setMessage(sb.toString());
            materialDialog.setPositiveButton(R.string.know, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SettingConfigManager.instance.setUpgrade(false);
                    materialDialog.dismiss();
                }
            });
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_TASK_CODE);
            } else {
                resetDownLoadData();
            }
            materialDialog.show();
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
                materialDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
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
                CustomToast.INSTANCE.showToast(R.string.alert_home, CustomToast.LENGTH_LONG);
            } else {
                CustomToast.INSTANCE.showToast(R.string.alert_exit, CustomToast.LENGTH_LONG);
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
        LocationUtil.getInstance().destroy();
    }

    private void resetDownLoadData() {
        File packageFile = new File(ConstantManager.instance.getMusicFolder());
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
                        LocalInfo temp = lOp.findDataById("209", id);
                        if (temp == null || temp.getId() == 0) {
                            temp = new LocalInfo();
                            temp.setId(id);
                            temp.setApp("209");
                            temp.setDownload(1);
                            temp.setDownTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                            lOp.saveData(temp);
                        } else {
                            lOp.updateDownload(id, "209", 1);
                        }
                        StringBuilder.append(id).append(',');
                    }
                } else {
                    new File(ConstantManager.instance.getMusicFolder() + File.separator + fileName).delete();
                }
            }
            NewsesRequest.getInstance().exeRequest(NewsesRequest.getInstance().generateUrl(StringBuilder.toString()), new IProtocolResponse() {
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
                        temp.setApp(ConstantManager.instance.getAppId());
                    }
                    articleOp.saveData(netData);
                }
            });
        }
    }

    @Override
    public void notifyChange(int arg, String des) {

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
}
