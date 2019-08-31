package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.buaa.ct.appskin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.listener.NoDoubleClickListener;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.util.PermissionPool;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.view.MaterialRippleLayout;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by 10202 on 2015/10/23.
 */
public abstract class BaseActivity extends BaseSkinActivity {
    protected Context context;
    protected MaterialRippleLayout back;
    protected MaterialMenu backIcon;
    protected FrameLayout toolBarLayout;
    protected TextView title, toolbarOper;

    protected boolean changeProperty;
    protected boolean mipush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        ChangePropery.setAppConfig(this);
        changeProperty = getIntent().getBooleanExtra(ChangePropertyBroadcast.RESULT_FLAG, false);
        mipush = getIntent().getBooleanExtra("pushIntent", false);
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changeProperty = intent.getBooleanExtra(ChangePropertyBroadcast.RESULT_FLAG, false);
        mipush = intent.getBooleanExtra("pushIntent", false);
    }

    protected void initWidget() {
        back = findViewById(R.id.back);
        backIcon = findViewById(R.id.back_material);
        toolBarLayout = findViewById(R.id.toolbar_title_layout);
        title = findViewById(R.id.toolbar_title);
    }

    protected void setListener() {
        back.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
    }

    protected void setRecyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration());
    }

    protected MySwipeRefreshLayout findSwipeRefresh() {
        MySwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setRefreshing(true);
        return swipeRefreshLayout;
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
        MobclickAgent.onPause(this);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
    }

    public void requestMultiPermission(@PermissionPool.PermissionCode final int[] codes, @PermissionPool.PermissionName final String[] permissions) {
        requestMultiPermission(codes, permissions, RuntimeManager.getString(R.string.storage_permission_content));
    }

    public void requestMultiPermission(@PermissionPool.PermissionCode final int[] codes, @PermissionPool.PermissionName final String[] permissions, String dialogContent) {
        boolean result = true;
        for (String permission : permissions) {
            result = result && (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED);
        }
        if (this.isFinishing() || result) {
            return;
        }

        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(dialogContent);
        materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                for (int i = 0; i < codes.length; i++) {
                    permissionDispose(codes[i], permissions[i]);
                }
            }
        });
        materialDialog.show();
    }

    public void permissionDispose(@PermissionPool.PermissionCode int code, @PermissionPool.PermissionName String permissionName) {
        if (ContextCompat.checkSelfPermission(this, permissionName) != PackageManager.PERMISSION_GRANTED) {
            //没有权限,开始申请
            ActivityCompat.requestPermissions(this, new String[]{permissionName}, code);
        } else {
            //有权限
            onAccreditSucceed(code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //授权成功
            onAccreditSucceed(requestCode);
        } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            //授权失败
            onAccreditFailure(requestCode);
        }
    }

    public void onAccreditSucceed(int requestCode) {
    }


    public void onAccreditFailure(int requestCode) {
    }
}
