package com.iyuba.music.activity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.view.View;

import com.buaa.ct.core.activity.CoreBaseListActivity;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.umeng.analytics.MobclickAgent;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.EnumSet;
import java.util.List;


/**
 * Created by 10202 on 2015/10/23.
 */
public abstract class BaseListActivity<T> extends CoreBaseListActivity<T> {
    protected YouDaoRecyclerAdapter mAdAdapter;
    protected boolean changeProperty;
    protected boolean mipush;
    protected boolean useYouDaoAd;

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
    public void assembleRecyclerView() {
        if (!DownloadUtil.checkVip() && useYouDaoAd) {
            mAdAdapter = new YouDaoRecyclerAdapter(this, ownerAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));
            setYouDaoMsg();
            setRecyclerViewProperty(owner);
            owner.setAdapter(mAdAdapter);
        } else {
            super.assembleRecyclerView();
        }
    }

    protected void setYouDaoMsg() {
        // 绑定界面组件与广告参数的映射关系，用于渲染广告
        final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
                new ViewBinder.Builder(R.layout.native_ad_row)
                        .titleId(R.id.native_title)
                        .mainImageId(R.id.native_main_image).build());
        mAdAdapter.registerAdRenderer(adRenderer);
        // 声明app需要的资源，这样可以提供高质量的广告，也会节省网络带宽
        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE, RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE, RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);

        Location location = new Location("appPos");
        location.setLatitude(AccountManager.getInstance().getLatitude());
        location.setLongitude(AccountManager.getInstance().getLongitude());
        location.setAccuracy(100);

        RequestParameters mRequestParameters = new RequestParameters.Builder()
                .location(location)
                .desiredAssets(desiredAssets).build();
        mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
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
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
        ((MusicApplication) getApplication()).popActivity(this);
    }

    public List<T> getData() {
        return ownerAdapter.getDatas();
    }

    @Override
    public @StringRes
    int getToastResource() {
        return R.string.article_load_all;
    }

    @Override
    public void onRequestPermissionDenied(String dialogContent, final int[] codes, final String[] permissions) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(dialogContent);
        materialDialog.setPositiveButton(R.string.app_sure, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                materialDialog.dismiss();
                for (int i = 0; i < codes.length; i++) {
                    permissionDispose(codes[i], permissions[i]);
                }
            }
        });
        materialDialog.show();
    }
}
