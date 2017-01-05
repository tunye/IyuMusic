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
import com.iyuba.music.adapter.study.MusicAdapter;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.MusicRequest;
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
 * Created by 10202 on 2016/3/4.
 */
public class MusicFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private ArrayList<Article> musicList;
    private MusicAdapter musicAdapter;
    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;
    private int curPage;
    private boolean isLastPage = false;
    //有道广告
    private YouDaoRecyclerAdapter mAdAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLastPage = false;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        musicList = new ArrayList<>();
        musicAdapter = new MusicAdapter(context);
        if (DownloadService.checkVip()) {
            musicAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.instance.setListFragmentPos(MusicFragment.this.getClass().getName());
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(musicList);
                    StudyManager.instance.setCurArticle(musicList.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            recyclerView.setAdapter(musicAdapter);
        } else {
            mAdAdapter = new YouDaoRecyclerAdapter(getActivity(), musicAdapter,
                    YouDaoNativeAdPositioning
                            .newBuilder().addFixedPosition(3).enableRepeatingPositions(10).build());
            musicAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.instance.setListFragmentPos(MusicFragment.this.getClass().getName());
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(musicList);
                    StudyManager.instance.setCurArticle(musicList.get(mAdAdapter.getOriginalPosition(position)));
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
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
    }

    private void getData() {
        MusicRequest.getInstance().exeRequest(MusicRequest.getInstance().generateUrl(curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (!StudyManager.instance.isStartPlaying()) {
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(musicList);
                    StudyManager.instance.setCurArticle(musicList.get(0));
                    StudyManager.instance.setApp("209");
                } else if (MusicFragment.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(musicList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (!StudyManager.instance.isStartPlaying()) {
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(musicList);
                    StudyManager.instance.setCurArticle(musicList.get(0));
                    StudyManager.instance.setApp("209");
                } else if (MusicFragment.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(musicList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.INSTANCE.showToast(R.string.article_load_all);
                } else {
                    musicList.addAll(netData);
                    musicAdapter.setDataSet(musicList);
                    if (curPage == 1) {
                    } else {
                        CustomToast.INSTANCE.showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                    if (!StudyManager.instance.isStartPlaying()) {
                        StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                        StudyManager.instance.setSourceArticleList(musicList);
                        StudyManager.instance.setCurArticle(musicList.get(0));
                        StudyManager.instance.setApp("209");
                    } else if (MusicFragment.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                        StudyManager.instance.setSourceArticleList(musicList);
                    }
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
                    articleOp.saveData(musicList);
                }
            }
        });
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        musicList = new ArrayList<>();
        isLastPage = false;
        getData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (musicList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.article_load_all);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
    }

    private void getDbData() {
        musicList.addAll(articleOp.findDataByMusic(musicList.size(), 20));
        musicAdapter.setDataSet(musicList);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (musicAdapter != null) {
                BannerView bannerView = ((BannerView) recyclerView.getLayoutManager().getChildAt(0).findViewById(R.id.banner));
                if (bannerView != null)
                    bannerView.startAd();
            }
        } else {
            if (musicAdapter != null) {
                BannerView bannerView = ((BannerView) recyclerView.getLayoutManager().getChildAt(0).findViewById(R.id.banner));
                if (bannerView != null)
                    bannerView.stopAd();
            }
        }
    }
}
