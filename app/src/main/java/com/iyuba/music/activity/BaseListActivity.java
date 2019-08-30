package com.iyuba.music.activity;

import android.location.Location;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.ArrayList;
import java.util.EnumSet;


/**
 * Created by 10202 on 2015/10/23.
 */
public abstract class BaseListActivity<T> extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    protected MySwipeRefreshLayout swipeRefreshLayout;
    protected int curPage;
    protected boolean isLastPage = false;
    protected ArrayList<T> datas;
    protected YouDaoRecyclerAdapter mAdAdapter;

    private RecyclerView owner;

    @Override
    protected void initWidget() {
        super.initWidget();
        swipeRefreshLayout = findSwipeRefresh();
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
    }

    protected void setRecyclerViewProperty(RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.addItemDecoration(new DividerItemDecoration());
        owner = recyclerView;
    }

    protected void setYouDaoMsg() {
        // 绑定界面组件与广告参数的映射关系，用于渲染广告
        final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
                new ViewBinder.Builder(R.layout.native_ad_row)
                        .titleId(R.id.native_title)
                        .mainImageId(R.id.native_main_image).build());
        mAdAdapter.registerAdRenderer(adRenderer);
        final Location location = null;
        final String keywords = null;
        // 声明app需要的资源，这样可以提供高质量的广告，也会节省网络带宽
        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE, RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE, RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
        RequestParameters mRequestParameters = new RequestParameters.Builder()
                .location(location).keywords(keywords)
                .desiredAssets(desiredAssets).build();
        mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
    }

    @Override
    public void onClick(View view, Object message) {
        owner.scrollToPosition(0);
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        datas = new ArrayList<>();
        isLastPage = false;
        getNetData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (datas.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getNetData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(getToastResource());
        }
    }

    protected @StringRes
    int getToastResource() {
        return R.string.article_load_all;
    }

    protected void getNetData() {

    }
}
