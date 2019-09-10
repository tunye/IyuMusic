package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.view.MaterialRippleLayout;
import com.iyuba.music.BuildConfig;
import com.iyuba.music.R;

/**
 * Created by 10202 on 2017-04-01.
 */

public class VersionInfoDialog {
    private Context context;
    private IyubaDialog iyubaDialog;

    public VersionInfoDialog(Context context) {
        this.context = context;
        init();
    }

    private void init() {
        View root = LayoutInflater.from(context).inflate(R.layout.version_info, null);
        TextView versionName, versionCode, buildTime, builder, versionType;
        MaterialRippleLayout sure;
        versionCode = root.findViewById(R.id.version_code);
        versionName = root.findViewById(R.id.version_name);
        versionType = root.findViewById(R.id.version_type);
        buildTime = root.findViewById(R.id.version_build_time);
        builder = root.findViewById(R.id.version_builder);
        sure = root.findViewById(R.id.version_know);
        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iyubaDialog.dismiss();
            }
        });
        int versionCodeString;
        String versionNameString, releaseTime, flavors, buildType;
        try {
            versionNameString = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
            versionCodeString = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
            flavors = BuildConfig.FLAVOR;
//            releaseTime = BuildConfig.RELEASE_TIME;
            // TODO: 2019-09-12 remeber here
            releaseTime = BuildConfig.APPLICATION_ID;
            buildType = "渠道名称：" + flavors + " " + ((context.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0 ? "debug版" : "release版");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            versionCodeString = 0;
            versionNameString = "0";
            releaseTime = "2000-01-01";
            buildType = "未知类型";
        }
        versionCode.setText("Build " + versionCodeString);
        versionName.setText(versionNameString);
        builder.setText("ct");
        buildTime.setText(releaseTime);
        versionType.setText(buildType);
        iyubaDialog = new IyubaDialog(context, root, true, 24);
        iyubaDialog.show();
    }
}
