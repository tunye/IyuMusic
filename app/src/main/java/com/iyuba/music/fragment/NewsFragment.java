package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.NewsAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.apprequest.BannerPicRequest;
import com.iyuba.music.request.newsrequest.NewsListRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.banner.BannerView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by 10202 on 2015/11/6.
 */
public class NewsFragment extends BaseRecyclerViewFragment<Article> {
    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useYouDaoAd = true;
        articleOp = new ArticleOp();
        localInfoOp = new LocalInfoOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ownerAdapter = new NewsAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.getInstance().setListFragmentPos(NewsFragment.this.getClass().getName());
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setSourceArticleList(getData());
                StudyManager.getInstance().setCurArticle(getData().get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }
        });
        assembleRecyclerView();
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        assembleRecyclerView();
        if (getData().size() == 0) {
            getDbData(0);
        }
        View view = owner.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.startAd();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        View view = owner.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.stopAd();
        }
    }

    @Override
    public void onRefresh(int index) {
        getNewsData(0);
    }

    @Override
    public void onLoad(int index) {
        if (getData().size() != 0) {
            getNewsData(getData().get(getData().size() - 1).getId());
        }
    }

    private void getNewsData(final int maxid) {
        if (maxid == 0) {
            if (!StudyManager.getInstance().getSingleInstanceRequest().containsKey("newsBanner")) {
                RequestClient.requestAsync(new BannerPicRequest("class.iyumusic"), new SimpleRequestCallBack<BaseListEntity<List<BannerEntity>>>() {
                    @Override
                    public void onSuccess(BaseListEntity<List<BannerEntity>> result) {
                        StudyManager.getInstance().getSingleInstanceRequest().put("newsBanner", "qier");
                        List<BannerEntity> bannerEntities = result.getData();
                        SPUtils.putString(ConfigManager.getInstance().getPreferences(), "newsbanner", JSON.toJSONString(bannerEntities));
                        ((NewsAdapter) ownerAdapter).setAdSet(bannerEntities);
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {
                        loadLocalBannerData();
                    }
                });
            } else {
                loadLocalBannerData();
            }
        }
        if (maxid == 0) {
            if (StudyManager.getInstance().getSingleInstanceRequest().containsKey(this.getClass().getSimpleName())) {
                getDbData(maxid);
                if (!StudyManager.getInstance().isStartPlaying() && getData().size() != 0) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(getData());
                    StudyManager.getInstance().setCurArticle(getData().get(0));
                    StudyManager.getInstance().setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
                swipeRefreshLayout.setRefreshing(false);
            } else {
                StudyManager.getInstance().getSingleInstanceRequest().put(this.getClass().getSimpleName(), "qier");
                loadNetData(maxid);
            }
        } else {
            loadNetData(maxid);
        }
    }

    private void getDbData(int maxId) {
        if (maxId == 0) {
            ownerAdapter.setDataSet(articleOp.findDataByAll(ConstantManager.appId, 0, 20));
        } else {
            ownerAdapter.addDatas(articleOp.findDataByAll(ConstantManager.appId, getData().size(), 20));
        }
    }

    private void loadNetData(final int maxid) {
        RequestClient.requestAsync(new NewsListRequest(maxid), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                swipeRefreshLayout.setRefreshing(false);
                List<Article> netData = listEntity.getData();
                if (maxid == 0) {
                    ownerAdapter.setDataSet(netData);
                } else {
                    if (netData.size() == 0) {
                        CustomToast.getInstance().showToast(R.string.article_load_all);
                    } else {
                        ownerAdapter.addDatas(netData);
                        owner.scrollToPosition(ownerAdapter.getItemCount() - 1 - netData.size());
                    }
                }
                if (!StudyManager.getInstance().isStartPlaying()) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(getData());
                    StudyManager.getInstance().setCurArticle(getData().get(0));
                    StudyManager.getInstance().setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
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
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper) + context.getString(R.string.article_local));
                getDbData(maxid);
                if (!StudyManager.getInstance().isStartPlaying() && getData().size() != 0) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(getData());
                    StudyManager.getInstance().setCurArticle(getData().get(0));
                    StudyManager.getInstance().setApp("209");
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void loadLocalBannerData() {
        String preferenceData = SPUtils.loadString(ConfigManager.getInstance().getPreferences(), "newsbanner");
        List<BannerEntity> bannerEntities;
        if (TextUtils.isEmpty(preferenceData)) {
            bannerEntities = new ArrayList<>();
            BannerEntity bannerEntity = new BannerEntity();
            bannerEntity.setOwnerid("2");
            bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
            bannerEntity.setDesc(context.getString(R.string.app_name));
            bannerEntities.add(bannerEntity);
        } else {
            bannerEntities = JSON.parseArray(preferenceData, BannerEntity.class);
        }
        ((NewsAdapter) ownerAdapter).setAdSet(bannerEntities);
    }

    @Override
    public void onDestroy() {
        View view = owner.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                bannerView.initData(null, null);
        }
        super.onDestroy();
    }
}
