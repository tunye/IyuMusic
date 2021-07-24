package com.iyuba.music.fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.adapter.CoreRecyclerViewAdapter;
import com.buaa.ct.core.listener.IOnClickListener;
import com.buaa.ct.core.listener.IOnDoubleClick;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.buaa.ct.core.view.swiperefresh.MySwipeRefreshLayout;
import com.iyuba.music.R;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.recycleview.ListRequestAllState;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.EnumSet;
import java.util.List;

/**
 * Created by 102 on 2016/10/31.
 */

public class BaseRecyclerViewFragment<T> extends BaseFragment implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    public RecyclerView owner;
    public CoreRecyclerViewAdapter<T, ?> ownerAdapter;
    public MySwipeRefreshLayout swipeRefreshLayout;
    public ListRequestAllState listRequestAllState;
    protected int curPage;
    protected boolean isLastPage = false;
    protected boolean useYouDaoAd = false;
    protected boolean enableSwipeWidget = true;

    //有道广告
    private YouDaoRecyclerAdapter mAdAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_recycler_view, null);
        owner = view.findViewById(R.id.recyclerview);
        listRequestAllState = view.findViewById(R.id.list_request_all_state);
        ((SimpleItemAnimator) owner.getItemAnimator()).setSupportsChangeAnimations(false);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_widget);
        return view;
    }

    public void decorateSwipeWidget() {
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(this);
    }

    public void assembleRecyclerView() {
        if (!DownloadUtil.checkVip() && useYouDaoAd && getActivity() != null) {
            mAdAdapter = new YouDaoRecyclerAdapter(getActivity(), ownerAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));
            setYouDaoMsg();
            owner.setAdapter(mAdAdapter);
        } else {
            owner.setAdapter(ownerAdapter);
        }
        owner.setLayoutManager(new LinearLayoutManager(this.context));
        owner.addItemDecoration(new DividerItemDecoration());
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

    public List<T> getData() {
        return ownerAdapter.getDatas();
    }

    @Override
    public void onClick(View view, Object message) {
        owner.scrollToPosition(0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && getActivity() != null) {
            getActivity().findViewById(R.id.toolbar_title_layout).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (enableSwipeWidget) {
            decorateSwipeWidget();
        } else {
            swipeRefreshLayout.setEnabled(false);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkVipState();
    }

    @Override
    public void onDestroy() {
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
        super.onDestroy();
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
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
        if (!isLastPage) {
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

    public void getNetData() {
    }

    public void onNetDataReturnSuccess(List<T> netData) {
        swipeRefreshLayout.setRefreshing(false);
        if (isLastPage) {
            if (getToastResource() != -1) {
                CustomToast.getInstance().showToast(getToastResource());
            }
        } else {
            handleBeforeAddAdapter(netData);
            ownerAdapter.addDatas(netData);
            if (curPage != 1) {
                owner.scrollToPosition(getYouAdPos(ownerAdapter.getDatas().size() - netData.size()));
            }
            handleAfterAddAdapter(netData);
        }
    }

    public void handleBeforeAddAdapter(List<T> netData) {
    }

    public void handleAfterAddAdapter(List<T> netData) {
    }

    public int getYouAdPos(int pos) {
        if (owner.getAdapter() instanceof YouDaoRecyclerAdapter) {
            return mAdAdapter.getAdjustedPosition(pos);
        } else {
            return pos;
        }
    }

    public void checkVipState() {
        if (!useYouDaoAd) {
            return;
        }
        if (DownloadUtil.checkVip() && owner.getAdapter() instanceof YouDaoRecyclerAdapter
                || !DownloadUtil.checkVip() && !(owner.getAdapter() instanceof YouDaoRecyclerAdapter)) {
            assembleRecyclerView();
        }
    }

    public String getClassName() {
        return this.getClass().getSimpleName();
    }

    public void setStudyList() {
        if (!getData().isEmpty() && getData().get(0) instanceof Article) {
            StudyManager.getInstance().setListFragmentPos(getClassName());
            StudyManager.getInstance().setLesson(ConstantManager.appEnglishNameSimple);
            StudyManager.getInstance().setSourceArticleList((List<Article>) getData());
            StudyManager.getInstance().setCurArticle((Article) getData().get(0));
            StudyManager.getInstance().setApp(ConstantManager.appId);
        }
    }
}
