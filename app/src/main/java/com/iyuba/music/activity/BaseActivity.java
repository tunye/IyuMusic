package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.buaa.ct.core.CoreBaseActivity;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by 10202 on 2015/10/23.
 */
public abstract class BaseActivity extends CoreBaseActivity {
    protected MaterialMenu backIcon;

    protected boolean changeProperty;
    protected boolean mipush;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        changeProperty = getIntent().getBooleanExtra(ChangePropertyBroadcast.RESULT_FLAG, false);
        mipush = getIntent().getBooleanExtra("pushIntent", false);
        ChangePropery.setAppConfig(this);
        ((MusicApplication) getApplication()).pushActivity(this);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changeProperty = intent.getBooleanExtra(ChangePropertyBroadcast.RESULT_FLAG, false);
        mipush = intent.getBooleanExtra("pushIntent", false);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        backIcon = findViewById(R.id.back_material);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
    }

    @Override
    public void onRequestPermissionDenied(String dialogContent, final int[] codes, final String[] permissions) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(dialogContent);
        materialDialog.setPositiveButton(R.string.app_sure, new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                materialDialog.dismiss();
                for (int i = 0; i < codes.length; i++) {
                    permissionDispose(codes[i], permissions[i]);
                }
            }
        });
        materialDialog.show();
    }
}
