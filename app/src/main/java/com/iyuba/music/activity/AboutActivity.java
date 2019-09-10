package com.iyuba.music.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.AddRippleEffect;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.util.PermissionPool;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.eggshell.EggShellActivity;
import com.iyuba.music.download.AppUpdateThread;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.apprequest.UpdateRequest;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.RoundProgressBar;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.VersionInfoDialog;
import com.iyuba.music.widget.roundview.RoundRelativeLayout;

import java.io.File;
import java.util.Calendar;

/**
 * 关于界面
 *
 * @author chentong
 */

public class AboutActivity extends BaseActivity implements WeakReferenceHandler.IHandlerMessageByRef<AboutActivity> {
    private static final int HANDLER_REFRESH_CODE = 0;

    private TextView version, copyright;
    private RoundRelativeLayout appUpdate, praise, developer, website;
    private View appNewImg, root, icon;
    private String newVersionCode;
    private String appUpdateUrl;// 版本号
    private RoundProgressBar progressBar;
    private boolean isCurrent;
    private int cookie;
    private Handler handler = new WeakReferenceHandler<>(this, this);

    @Override
    public int getLayoutId() {
        return R.layout.about;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        root = findViewById(R.id.root);
        icon = findViewById(R.id.icon);
        praise = findViewById(R.id.praise);
        AddRippleEffect.addRippleEffect(praise, 300);
        website = findViewById(R.id.website);
        AddRippleEffect.addRippleEffect(website, 300);
        developer = findViewById(R.id.developer);
        AddRippleEffect.addRippleEffect(developer, 300);
        appUpdate = findViewById(R.id.update);
        AddRippleEffect.addRippleEffect(appUpdate, 300);
        appNewImg = findViewById(R.id.newApp);
        version = findViewById(R.id.version);
        copyright = findViewById(R.id.copyright);
        progressBar = findViewById(R.id.roundProgressBar);
    }

    @Override
    public void setListener() {
        super.setListener();
        icon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                new VersionInfoDialog(context);
            }
        });
        praise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    final MyMaterialDialog dialog = new MyMaterialDialog(context);
                    dialog.setTitle(R.string.about_praise).setMessage(R.string.about_market_error).
                            setPositiveButton(R.string.app_accept, new OnClickListener() {
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
                intent.putExtra("url", "http://www.iyuba.cn/");
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
    public void onActivityCreated() {
        super.onActivityCreated();
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

        boolean update = getIntent().getBooleanExtra("update", false);
        if (update) {
            this.appUpdateUrl = getIntent().getStringExtra("url");
            this.newVersionCode = getIntent().getStringExtra("version");
            appNewImg.setVisibility(View.VISIBLE);
            requestMultiPermission(new int[]{PermissionPool.WRITE_EXTERNAL_STORAGE}, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }
    }

    @Override
    public void onActivityResumed() {
        super.onActivityResumed();
        isCurrent = false;
        cookie = 5;
        DownloadFile file;
        for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {
            file = DownloadManager.getInstance().fileList.get(i);
            if (file.id == -1) {
                handler.obtainMessage(HANDLER_REFRESH_CODE, file).sendToTarget();
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    }

    /**
     * 检查新版本
     */
    public void checkAppUpdate(final boolean fromUser) {
        int currentVersion = 0;
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        RequestClient.requestAsync(new UpdateRequest(currentVersion), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> apiEntity) {
                if (BaseApiEntity.isFail(apiEntity)) {
                    appNewImg.setVisibility(View.INVISIBLE);
                    if (fromUser) {
                        isCurrent = true;
                        CustomToast.getInstance().showToast(R.string.about_update_noneed);
                    }
                } else {
                    String[] para = apiEntity.getValue().split("@@@");
                    newVersionCode = para[0];
                    appUpdateUrl = para[2];
                    appNewImg.setVisibility(View.VISIBLE);
                    acceptForUpdate();
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(R.string.about_update_fail);
            }
        });
    }

    private void acceptForUpdate() {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.about_update).
                setMessage(context.getString(R.string.about_update_message, newVersionCode))
                .setPositiveButton(R.string.about_update_accept, new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        requestMultiPermission(new int[]{PermissionPool.WRITE_EXTERNAL_STORAGE}, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                        dialog.dismiss();
                    }
                }).setNegativeButton(R.string.app_cancel, new OnClickListener() {
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
        File file = new File(ConstantManager.updateFolder);
        if (!file.exists()) {
            file.mkdirs();
        }
        for (int i = 0; i < DownloadManager.getInstance().fileList.size(); i++) {
            if (DownloadManager.getInstance().fileList.get(i).id == -1) {
                return;
            }
        }
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.id = -1;
        downloadFile.downloadState = "start";
        downloadFile.fileAppend = ".apk";
        downloadFile.downLoadAddress = appUpdateUrl;
        downloadFile.filePath = ConstantManager.updateFolder + File.separator;
        downloadFile.fileName = "iyumusic";
        DownloadManager.getInstance().fileList.add(downloadFile);
        AppUpdateThread appUpdateThread = new AppUpdateThread();
        appUpdateThread.start();
        handler.obtainMessage(HANDLER_REFRESH_CODE, downloadFile).sendToTarget();
    }

    private void openCookie() {
        if (ConfigManager.getInstance().isEggShell()) {
            new VersionInfoDialog(context);
        } else if (cookie == 0) {
            ConfigManager.getInstance().setEggShell(true);
            CustomSnackBar.make(root, context.getString(R.string.about_eggshell_open)).info(context.getString(R.string.about_go_eggshell), new OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(context, EggShellActivity.class));
                }
            });
        } else {
            CustomToast.getInstance().showToast(context.getString(R.string.about_eggshell_opening, String.valueOf(cookie)));
            cookie--;
        }
    }

    @Override
    public void onAccreditSucceed(int requestCode) {
        super.onAccreditSucceed(requestCode);
        startDownLoad();
    }

    @Override
    public void onAccreditFailure(int requestCode) {
        super.onAccreditFailure(requestCode);
    }

    @Override
    public void handleMessageByRef(AboutActivity activity, Message msg) {
        DownloadFile file = (DownloadFile) msg.obj;
        if (file.downloadState.equals("start")) {
            activity.progressBar.setCricleProgressColor(GetAppColor.getInstance().getAppColor());
            activity.progressBar.setMax(file.fileSize);
            activity.progressBar.setProgress(file.downloadSize);
            Message message = new Message();
            message.what = HANDLER_REFRESH_CODE;
            message.obj = file;
            activity.handler.sendMessageDelayed(message, 1500);
        } else if (file.downloadState.equals("finish")) {
            activity.handler.removeMessages(HANDLER_REFRESH_CODE);
            activity.progressBar.setVisibility(View.GONE);
            activity.appNewImg.setVisibility(View.INVISIBLE);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            DownloadFile downloadFile = (DownloadFile) msg.obj;
            String path = downloadFile.filePath + downloadFile.fileName + downloadFile.fileAppend;
            intent.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive");
            activity.startActivity(intent);
        }
    }
}
