package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.SPUtils;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.swiperefresh.MySwipeRefreshLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.main.ClassifyNewsList;
import com.iyuba.music.adapter.study.SongCategoryAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.mainpanel.SongCategory;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.BannerPicRequest;
import com.iyuba.music.request.mainpanelrequest.SongCategoryRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.banner.BannerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 10202 on 2015/11/6.
 */
public class SongCategoryFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private List<SongCategory> newsList;
    private SongCategoryAdapter newsAdapter;
    private int curPage;
    private boolean isLastPage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        newsList = new ArrayList<>();
        newsAdapter = new SongCategoryAdapter(context);
        newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(context, ClassifyNewsList.class);
                intent.putExtra("classify", newsAdapter.getItem(position).getId());
                intent.putExtra("classifyName", newsAdapter.getItem(position).getText());
                startActivity(intent);
            }

        });
        recyclerView.setAdapter(newsAdapter);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onRefresh(0);
        swipeRefreshLayout.setRefreshing(true);
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        getNewsData(MySwipeRefreshLayout.TOP_REFRESH);
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (newsList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getNewsData(MySwipeRefreshLayout.BOTTOM_REFRESH);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.article_load_all);
        }
    }

    private void getNewsData(final int refreshType) {
        if (refreshType == MySwipeRefreshLayout.TOP_REFRESH) {
            if (!StudyManager.getInstance().getSingleInstanceRequest().containsKey(this.getClass().getSimpleName())) {
                RequestClient.requestAsync(new BannerPicRequest("class.iyumusic.yuan"), new SimpleRequestCallBack<BaseListEntity<List<BannerEntity>>>() {
                    @Override
                    public void onSuccess(BaseListEntity<List<BannerEntity>> result) {
                        StudyManager.getInstance().getSingleInstanceRequest().put(this.getClass().getSimpleName(), "qier");
                        List<BannerEntity> bannerEntities = result.getData();
                        BannerEntity bannerEntity = new BannerEntity();
                        bannerEntity.setOwnerid("2");
                        bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
                        bannerEntity.setDesc("全部原声歌曲列表");
                        bannerEntities.add(bannerEntity);
                        SPUtils.putString(ConfigManager.getInstance().getPreferences(), "songbanner", JSON.toJSONString(bannerEntities));
                        newsAdapter.setAdSet(bannerEntities);
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {

                    }
                });
            } else {
                loadLocalBannerData();
            }
        }
        RequestClient.requestAsync(new SongCategoryRequest(curPage), new SimpleRequestCallBack<BaseListEntity<List<SongCategory>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<SongCategory>> listEntity) {
                List<SongCategory> netData = listEntity.getData();
                isLastPage = listEntity.isLastPage();
                switch (refreshType) {
                    case MySwipeRefreshLayout.TOP_REFRESH:
                        newsList = netData;
                        break;
                    case MySwipeRefreshLayout.BOTTOM_REFRESH:
                        if (!isLastPage) {
                            newsList.addAll(netData);
                        } else {
                            CustomToast.getInstance().showToast(R.string.article_load_all);
                        }
                        break;
                }
                swipeRefreshLayout.setRefreshing(false);
                newsAdapter.setDataSet(newsList);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadLocalBannerData() {
        newsAdapter.setAdSet(JSON.parseArray(SPUtils.loadString(ConfigManager.getInstance().getPreferences(), "songbanner"), BannerEntity.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        View view = recyclerView.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = (BannerView) view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.startAd();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = recyclerView.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = (BannerView) view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.stopAd();
        }
    }

    @Override
    public void onDestroy() {
        View view = recyclerView.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = (BannerView) view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.initData(null, null);
        }
        super.onDestroy();
    }
}
