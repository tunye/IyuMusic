package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.activity.main.ClassifySongList;
import com.iyuba.music.adapter.study.SongCategoryAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.mainpanel.SongCategory;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.request.apprequest.BannerPicRequest;
import com.iyuba.music.request.mainpanelrequest.SongCategoryRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;

import java.util.ArrayList;


/**
 * Created by 10202 on 2015/11/6.
 */
public class SongCategoryFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private ArrayList<SongCategory> newsList;
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
                Intent intent = new Intent(context, ClassifySongList.class);
                intent.putExtra("classify", newsAdapter.getItem(position).getId());
                intent.putExtra("classifyName", newsAdapter.getItem(position).getText());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        recyclerView.setAdapter(newsAdapter);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayList<BannerEntity> bannerEntities = new ArrayList<>(1);
        BannerEntity bannerEntity = new BannerEntity();
        bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
        bannerEntity.setOwnerid("2");
        bannerEntities.add(bannerEntity);
        newsAdapter.setAdSet(bannerEntities);
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
            BannerPicRequest.exeRequest(BannerPicRequest.generateUrl("class.iyumusic.yuan"), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                }

                @Override
                public void onServerError(String msg) {
                }

                @Override
                public void response(Object object) {
                    ArrayList<BannerEntity> bannerEntities = (ArrayList<BannerEntity>) ((BaseListEntity) object).getData();
                    BannerEntity bannerEntity = new BannerEntity();
                    bannerEntity.setOwnerid("2");
                    bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
                    bannerEntity.setDesc("全部原声歌曲列表");
                    bannerEntities.add(bannerEntity);
                    newsAdapter.setAdSet(bannerEntities);
                }
            });
        }
        SongCategoryRequest.exeRequest(SongCategoryRequest.generateUrl(curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<SongCategory> netData = (ArrayList<SongCategory>) listEntity.getData();
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
        });
    }
}
