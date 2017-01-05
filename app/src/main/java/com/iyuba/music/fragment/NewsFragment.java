package com.iyuba.music.fragment;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.NewsAdapter;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.BannerPicRequest;
import com.iyuba.music.request.newsrequest.NewsListRequest;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.banner.BannerView;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getNewsData(0, MySwipeRefreshLayout.TOP_REFRESH);
        swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (DownloadService.checkVip()) {
            newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.instance.setListFragmentPos(NewsFragment.this.getClass().getName());
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setSourceArticleList(newsList);
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setCurArticle(newsList.get(position - 1));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            recyclerView.setAdapter(newsAdapter);
        } else {
            mAdAdapter = new YouDaoRecyclerAdapter(getActivity(), newsAdapter,
                    YouDaoNativeAdPositioning
                            .newBuilder().addFixedPosition(3).enableRepeatingPositions(10).build());
            newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.instance.setListFragmentPos(NewsFragment.this.getClass().getName());
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(newsList);
                    StudyManager.instance.setCurArticle(newsList.get(mAdAdapter.getOriginalPosition(position) - 1));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            // 绑定界面组件与广告参数的映射关系，用于渲染广告
            final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
                    new ViewBinder.Builder(R.layout.native_ad_row)
                            .titleId(R.id.native_title)
                            .textId(R.id.native_text)
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
        if (newsList.size() == 0) {
            getDbData(0);
        }
        newsAdapter.setDataSet(newsList);
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
            BannerPicRequest.getInstance().exeRequest(BannerPicRequest.getInstance().generateUrl("class.iyumusic"), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    ArrayList<BannerEntity> bannerEntities = new ArrayList<>();
                    BannerEntity bannerEntity = new BannerEntity();
                    bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
                    bannerEntity.setOwnerid("2");
                    bannerEntities.add(bannerEntity);
                    newsAdapter.setAdSet(bannerEntities);
                }

                @Override
                public void onServerError(String msg) {
                    ArrayList<BannerEntity> bannerEntities = new ArrayList<>();
                    BannerEntity bannerEntity = new BannerEntity();
                    bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
                    bannerEntity.setOwnerid("2");
                    bannerEntities.add(bannerEntity);
                    newsAdapter.setAdSet(bannerEntities);
                }

                @Override
                public void response(Object object) {
                    ArrayList<BannerEntity> bannerEntities = (ArrayList<BannerEntity>) ((BaseListEntity) object).getData();
                    newsAdapter.setAdSet(bannerEntities);
                }
            });
        }
        NewsListRequest.getInstance().exeRequest(NewsListRequest.getInstance().generateUrl(maxid), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.article_local));
                getDbData(maxid);
                if (!StudyManager.instance.isStartPlaying() && newsList.size() != 0) {
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(newsList);
                    StudyManager.instance.setCurArticle(newsList.get(0));
                    StudyManager.instance.setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.article_local));
                getDbData(maxid);
                if (!StudyManager.instance.isStartPlaying() && newsList.size() != 0) {
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(newsList);
                    StudyManager.instance.setCurArticle(newsList.get(0));
                    StudyManager.instance.setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                switch (refreshType) {
                    case MySwipeRefreshLayout.TOP_REFRESH:
                        newsList = netData;
                        break;
                    case MySwipeRefreshLayout.BOTTOM_REFRESH:
                        if (netData.size() == 0) {
                            CustomToast.INSTANCE.showToast(R.string.article_load_all);
                        } else {
                            newsList.addAll(netData);
                        }
                        break;
                }
                if (!StudyManager.instance.isStartPlaying()) {
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(newsList);
                    StudyManager.instance.setCurArticle(newsList.get(0));
                    StudyManager.instance.setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
                newsAdapter.setDataSet(newsList);
                LocalInfo localinfo;
                for (Article temp : netData) {
                    temp.setApp(ConstantManager.instance.getAppId());
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

    private void getDbData(int maxId) {
        if (maxId == 0) {
            newsList = articleOp.findDataByAll(ConstantManager.instance.getAppId(), 0, 20);
        } else {
            newsList.addAll(articleOp.findDataByAll(ConstantManager.instance.getAppId(), newsList.size(), 20));
        }
        newsAdapter.setDataSet(newsList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (newsAdapter != null) {
                BannerView bannerView = ((BannerView) recyclerView.getLayoutManager().getChildAt(0).findViewById(R.id.banner));
                if (bannerView != null)
                    bannerView.startAd();
            }
        } else {
            if (newsAdapter != null) {
                BannerView bannerView = ((BannerView) recyclerView.getLayoutManager().getChildAt(0).findViewById(R.id.banner));
                if (bannerView != null)
                    bannerView.stopAd();
            }
        }
    }
}
