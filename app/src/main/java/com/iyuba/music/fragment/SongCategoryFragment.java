package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.activity.main.ClassifySongList;
import com.iyuba.music.adapter.study.SongCategoryAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.mainpanel.SongCategory;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.request.apprequest.BannerPicRequest;
import com.iyuba.music.request.mainpanelrequest.SongCategoryRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.CustomSwipeToRefresh;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;


/**
 * Created by 10202 on 2015/11/6.
 */
public class SongCategoryFragment extends BaseFragment implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private Context context;
    private RecyclerView newsRecyclerView;
    private ArrayList<SongCategory> newsList;
    private SongCategoryAdapter newsAdapter;
    private CustomSwipeToRefresh swipeRefreshLayout;
    private int curPage;
    private boolean isLastPage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.news, null);
        newsRecyclerView = (RecyclerView) view.findViewById(R.id.news_recyclerview);
        swipeRefreshLayout = (CustomSwipeToRefresh) view.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(context));
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
        newsRecyclerView.setAdapter(newsAdapter);
        newsRecyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        onRefresh(0);
        swipeRefreshLayout.setRefreshing(true);
        return view;
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        getNewsData(index, MySwipeRefreshLayout.TOP_REFRESH);
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
            getNewsData(newsList.get(newsList.size() - 1).getId(), MySwipeRefreshLayout.BOTTOM_REFRESH);
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.artical_load_all);
        }
    }

    private void getNewsData(final int maxid, final int refreshType) {
        if (refreshType == MySwipeRefreshLayout.TOP_REFRESH) {
            BannerPicRequest.getInstance().exeRequest(BannerPicRequest.getInstance().generateUrl("class.iyumusic.yuan"), new IProtocolResponse() {
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
                    bannerEntities.add(bannerEntity);
                    newsAdapter.setAdSet(bannerEntities);
                }
            });
        }
        SongCategoryRequest.getInstance().exeRequest(SongCategoryRequest.getInstance().generateUrl(curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.artical_local));
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
                            CustomToast.INSTANCE.showToast(R.string.artical_load_all);
                        }
                        break;
                }
                swipeRefreshLayout.setRefreshing(false);
                newsAdapter.setDataSet(newsList);
            }
        });
    }

    @Override
    public void onClick(View view, Object message) {
        newsRecyclerView.scrollToPosition(0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        }
    }
}
