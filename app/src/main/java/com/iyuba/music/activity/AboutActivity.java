package com.iyuba.music.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.eggshell.EggShellActivity;
import com.iyuba.music.download.AppUpdateThread;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.request.apprequest.UpdateRequest;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.RoundProgressBar;

import java.io.File;
import java.util.Calendar;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * 关于界面
 *
 * @author chentong
 */

public class AboutActivity extends BaseActivity {
    private static final int WRITE_EXTERNAL_STORAGE_TASK_CODE = 1;

    private TextView version, copyright;
    private RoundRelativeLayout appUpdate, praise, developer, website;
    private View appNewImg, root;
    private String newVersionCode;
    private String appUpdateUrl;// 版本号
    private RoundProgressBar progressBar;
    private boolean update;
    private boolean isCurrent;
    private int cookie;
    private Snackbar snackbar;
    private Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
        update = getIntent().getBooleanExtra("update", false);
        if (update) {
            this.appUpdateUrl = getIntent().getStringExtra("url");
            this.newVersionCode = getIntent().getStringExtra("version");
            appNewImg.setVisibility(View.VISIBLE);
            handler.sendEmptyMessage(0);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        root = findViewById(R.id.root);
        praise = (RoundRelativeLayout) findViewById(R.id.praise);
        website = (RoundRelativeLayout) findViewById(R.id.website);
        developer = (RoundRelativeLayout) findViewById(R.id.developer);
        appUpdate = (RoundRelativeLayout) findViewById(R.id.update);
        appNewImg = findViewById(R.id.newApp);
        version = (TextView) findViewById(R.id.version);
        copyright = (TextView) findViewById(R.id.copyright);
        progressBar = (RoundProgressBar) findViewById(R.id.roundProgressBar);
    }

    @Override
    protected void setListener() {
        super.setListener();
        praise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    final MaterialDialog dialog = new MaterialDialog(context);
                    dialog.setTitle(R.string.about_praise).setMessage(R.string.about_market_error).
                            setPositiveButton(R.string.accept, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            }
        });

        version.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openCookie();
            }
        });

        developer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, DeveloperActivity.class));
            }
        });

        website.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(context, WebViewActivity.class);
                intent.putExtra("url", "http://www.iyuba.com/");
                intent.putExtra("title", context.getString(R.string.about_website));
                startActivity(intent);
            }
        });

        appUpdate.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (appNewImg.isShown()) {
                    acceptForUpdate();
                } else {
                    if (!isCurrent) {
                        checkAppUpdate(true);
                    } else {
                        openCookie();
                    }
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        String versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = "0";
        }
        title.setText(R.string.about_title);
        version.setText(context.getString(R.string.about_version, versionCode));
        copyright.setText(context.getString(R.string.about_company,
                Calendar.getInstance().get(Calendar.YEAR)));
    }

    protected void changeUIResumeByPara() {
        isCurrent = false;
        cookie = 5;
        DownloadFile file;
        for (int i = 0; i < DownloadManager.sInstance.fileList.size(); i++) {
            file = DownloadManager.sInstance.fileList.get(i);
            if (file.id == -1) {
                Message message = new Message();
                message.what = 2;
                message.obj = file;
                handler.sendMessage(message);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 检查新版本
     */
    public void checkAppUpdate(final boolean fromUser) {
        final UpdateRequest updateRequest = UpdateRequest.getInstance();
        int currentVersion = 0;
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        updateRequest.exeRequest(updateRequest.generateUrl(currentVersion), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(R.string.about_update_fail);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(R.string.about_update_fail);
            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                if (apiEntity.getState().equals(BaseApiEntity.State.FAIL)) {
                    appNewImg.setVisibility(View.INVISIBLE);
                    if (fromUser) {
                        isCurrent = true;
                        CustomToast.INSTANCE.showToast(R.string.about_update_noneed);
                    }
                } else {
                    String[] para = apiEntity.getValue().split("@@@");
                    newVersionCode = para[0];
                    appUpdateUrl = para[2];
                    appNewImg.setVisibility(View.VISIBLE);
                    acceptForUpdate();
                }
            }
        });
    }

    private void acceptForUpdate() {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(R.string.about_update).
                setMessage(context.getString(R.string.about_update_message, newVersionCode))
                .setPositiveButton(R.string.about_update_accept, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handler.sendEmptyMessage(0);
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void startDownLoad() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        File file = new File(ConstantManager.instance.getUpdateFolder());
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 0; i < DownloadManager.sInstance.fileList.size(); i++) {
            if (DownloadManager.sInstance.fileList.get(i).id == -1) {
                return;
            }
        }
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.id = -1;
        downloadFile.downloadState = "start";
        downloadFile.fileAppend = ".apk";
        downloadFile.downLoadAddress = appUpdateUrl;
        downloadFile.filePath = ConstantManager.instance.getUpdateFolder() + File.separator;
        downloadFile.fileName = "iyumusic";
        DownloadManager.sInstance.fileList.add(downloadFile);
        AppUpdateThread appUpdateThread = new AppUpdateThread();
        appUpdateThread.start();
        Message message = new Message();
        message.what = 2;
        message.obj = downloadFile;
        handler.sendMessage(message);
    }

    private void openCookie() {
        if (SettingConfigManager.instance.isEggShell()) {
            startActivity(new Intent(context, EggShellActivity.class));
        } else if (cookie == 0) {
            SettingConfigManager.instance.setEggShell(true);
            snackbar = Snackbar.make(root, context.getString(R.string.about_eggshell_open),
                    Snackbar.LENGTH_LONG).setAction(R.string.about_go_eggshell, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, EggShellActivity.class));
                }
            });
            ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
            snackbar.show();
        } else {
            CustomToast.INSTANCE.showToast(context.getString(R.string.about_eggshell_opening, String.valueOf(cookie)));
            cookie--;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_TASK_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownLoad();
            } else {
                final MaterialDialog materialDialog = new MaterialDialog(context);
                materialDialog.setTitle(R.string.storage_permission);
                materialDialog.setMessage(R.string.storage_permission_content);
                materialDialog.setPositiveButton(R.string.sure, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(AboutActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_STORAGE_TASK_CODE);
                        materialDialog.dismiss();
                    }
                });
                materialDialog.show();
            }
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<AboutActivity> {
        @Override
        public void handleMessageByRef(AboutActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        //申请WRITE_EXTERNAL_STORAGE权限
                        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                WRITE_EXTERNAL_STORAGE_TASK_CODE);
                    } else {
                        activity.startDownLoad();
                    }
                    break;
                case 1:
                    activity.progressBar.setVisibility(View.GONE);
                    activity.appNewImg.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    DownloadFile file = (DownloadFile) msg.obj;
                    if (file.downloadState.equals("start")) {
                        activity.progressBar.setCricleProgressColor(0xff87c973);
                        activity.progressBar.setMax(file.fileSize);
                        activity.progressBar.setProgress(file.downloadSize);
                        Message message = new Message();
                        message.what = 2;
                        message.obj = file;
                        activity.handler.sendMessageDelayed(message, 1500);
                    } else if (file.downloadState.equals("finish")) {
                        activity.handler.sendEmptyMessage(1);
                        activity.handler.removeMessages(2);
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        DownloadFile downloadFile = (DownloadFile) msg.obj;
                        String path = downloadFile.filePath + downloadFile.fileName + downloadFile.fileAppend;
                        intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
                        activity.startActivity(intent);
                    }
                    break;
            }
        }
    }
}
