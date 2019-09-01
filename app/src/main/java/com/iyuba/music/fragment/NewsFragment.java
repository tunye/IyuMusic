package com.iyuba.music.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.NewsAdapter;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.BannerPicRequest;
import com.iyuba.music.request.newsrequest.NewsListRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.banner.BannerView;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.EnumSet;


/**
 * Created by 10202 on 2015/11/6.
 */
public class NewsFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private ArrayList<Article> newsList;
    private NewsAdapter newsAdapter;
    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;
    private boolean isVipLastState;
    //有道广告
    private YouDaoRecyclerAdapter mAdAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        articleOp = new ArticleOp();
        localInfoOp = new LocalInfoOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(context);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isVipLastState = DownloadUtil.checkVip();
        newsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.getInstance().setListFragmentPos(NewsFragment.this.getClass().getName());
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setSourceArticleList(newsList);
                StudyManager.getInstance().setCurArticle(newsList.get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        if (isVipLastState) {
            initVipRecyclerView();
        } else {
            initUnVipRecyclerView();
        }
        getNewsData(0, MySwipeRefreshLayout.TOP_REFRESH);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVipLastState != DownloadUtil.checkVip()) {
            isVipLastState = DownloadUtil.checkVip();
            if (isVipLastState) {
                initVipRecyclerView();
            } else {
                initUnVipRecyclerView();
            }
        }
        if (newsList.size() == 0) {
            getDbData(0);
        }
        newsAdapter.setDataSet(newsList);
        View view = recyclerView.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = (BannerView) view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.startAd();
        }
    }

    private void initVipRecyclerView() {
        recyclerView.setAdapter(newsAdapter);
    }

    private void initUnVipRecyclerView() {
        mAdAdapter = new YouDaoRecyclerAdapter(getActivity(), newsAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));
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
        recyclerView.setAdapter(mAdAdapter);
        mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
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

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        getNewsData(index, MySwipeRefreshLayout.TOP_REFRESH);
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (newsList.size() != 0) {
            getNewsData(newsList.get(newsList.size() - 1).getId(), MySwipeRefreshLayout.BOTTOM_REFRESH);
        }
    }

    private void getNewsData(final int maxid, final int refreshType) {
        if (refreshType == MySwipeRefreshLayout.TOP_REFRESH) {
            if (!StudyManager.getInstance().getSingleInstanceRequest().containsKey("newsBanner")) {
                BannerPicRequest.exeRequest(BannerPicRequest.generateUrl("class.iyumusic"), new IProtocolResponse<BaseListEntity<ArrayList<BannerEntity>>>() {
                    @Override
                    public void onNetError(String msg) {
                        loadLocalBannerData();
                    }

                    @Override
                    public void onServerError(String msg) {
                        loadLocalBannerData();
                    }

                    @Override
                    public void response(BaseListEntity<ArrayList<BannerEntity>> result) {
                        StudyManager.getInstance().getSingleInstanceRequest().put("newsBanner", "qier");
                        ArrayList<BannerEntity> bannerEntities = result.getData();
                        ConfigManager.getInstance().putString("newsbanner", new Gson().toJson(bannerEntities));
                        newsAdapter.setAdSet(bannerEntities);
                    }
                });
            } else {
                loadLocalBannerData();
            }
        }
        if (maxid == 0) {
            if (StudyManager.getInstance().getSingleInstanceRequest().containsKey(this.getClass().getSimpleName())) {
                getDbData(maxid);
                if (!StudyManager.getInstance().isStartPlaying() && newsList.size() != 0) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(newsList);
                    StudyManager.getInstance().setCurArticle(newsList.get(0));
                    StudyManager.getInstance().setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            } else {
                StudyManager.getInstance().getSingleInstanceRequest().put(this.getClass().getSimpleName(), "qier");
                loadNetData(maxid, refreshType);
            }
        } else {
            loadNetData(maxid, refreshType);
        }
    }

    private void getDbData(int maxId) {
        if (maxId == 0) {
            newsList = articleOp.findDataByAll(ConstantManager.appId, 0, 20);
        } else {
            newsList.addAll(articleOp.findDataByAll(ConstantManager.appId, newsList.size(), 20));
        }
        newsAdapter.setDataSet(newsList);
    }

    private void loadNetData(final int maxid, final int refreshType) {
        NewsListRequest.exeRequest(NewsListRequest.generateUrl(maxid), new IProtocolResponse<BaseListEntity<ArrayList<Article>>>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData(maxid);
                if (!StudyManager.getInstance().isStartPlaying() && newsList.size() != 0) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(newsList);
                    StudyManager.getInstance().setCurArticle(newsList.get(0));
                    StudyManager.getInstance().setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData(maxid);
                if (!StudyManager.getInstance().isStartPlaying() && newsList.size() != 0) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(newsList);
                    StudyManager.getInstance().setCurArticle(newsList.get(0));
                    StudyManager.getInstance().setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(BaseListEntity<ArrayList<Article>> listEntity) {
                ArrayList<Article> netData =listEntity.getData();
                switch (refreshType) {
                    case MySwipeRefreshLayout.TOP_REFRESH:
                        newsList = netData;
                        break;
                    case MySwipeRefreshLayout.BOTTOM_REFRESH:
                        if (netData.size() == 0) {
                            CustomToast.getInstance().showToast(R.string.article_load_all);
                        } else {
                            newsList.addAll(netData);
                        }
                        break;
                }
                if (!StudyManager.getInstance().isStartPlaying()) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(newsList);
                    StudyManager.getInstance().setCurArticle(newsList.get(0));
                    StudyManager.getInstance().setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
                newsAdapter.setDataSet(newsList);
                LocalInfo localinfo;
                for (Article temp : netData) {
                    temp.setApp(ConstantManager.appId);
                    localinfo = localInfoOp.findDataById(temp.getApp(), temp.getId());
                    if (localinfo.getId() == 0) {
                        localinfo.setApp(temp.getApp());
                        localinfo.setId(temp.getId());
                        localInfoOp.saveData(localinfo);
                    }
                }
                articleOp.saveData(netData);
            }
        });
    }

    private void loadLocalBannerData() {
        Type listType = new TypeToken<ArrayList<BannerEntity>>() {
        }.getType();
        String preferenceData = ConfigManager.getInstance().loadString("newsbanner");
        ArrayList<BannerEntity> bannerEntities;
        if (TextUtils.isEmpty(preferenceData)) {
            bannerEntities = new ArrayList<>();
            BannerEntity bannerEntity = new BannerEntity();
            bannerEntity.setOwnerid("2");
            bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
            bannerEntity.setDesc(context.getString(R.string.app_name));
            bannerEntities.add(bannerEntity);
        } else {
            bannerEntities = new Gson().fromJson(ConfigManager.getInstance().loadString("newsbanner"), listType);
        }
        newsAdapter.setAdSet(bannerEntities);
    }

    @Override
    public void onDestroy() {
        View view = recyclerView.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = (BannerView) view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.initData(null, null);
        }
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
        super.onDestroy();
    }
}
