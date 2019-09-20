package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
                setStudyList();
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setCurArticle(getData().get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }
        });
        assembleRecyclerView();
        super.onViewCreated(view, savedInstanceState);
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
            if (!StudyManager.getInstance().getDailyLoadOnceMap().containsKey(getClassName() + "-Banner")) {
                RequestClient.requestAsync(new BannerPicRequest("class.iyumusic"), new SimpleRequestCallBack<BaseListEntity<List<BannerEntity>>>() {
                    @Override
                    public void onSuccess(BaseListEntity<List<BannerEntity>> result) {
                        StudyManager.getInstance().getDailyLoadOnceMap().put(getClassName() + "-Banner", "qier");
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
            if (StudyManager.getInstance().getDailyLoadOnceMap().containsKey(getClassName())) {
                getDbData(maxid);
                if (!StudyManager.getInstance().isStartPlaying() && getData().size() != 0) {
                    setStudyList();
                } else if (NewsFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
                swipeRefreshLayout.setRefreshing(false);
            } else {
                StudyManager.getInstance().getDailyLoadOnceMap().put(getClassName(), "qier");
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
                    owner.scrollToPosition(0);
                } else {
                    if (netData.size() == 0) {
                        CustomToast.getInstance().showToast(R.string.article_load_all);
                    } else {
                        ownerAdapter.addDatas(netData);
                        owner.scrollToPosition(getYouAdPos(getData().size() + 1 - netData.size()));
                    }
                }
                if (!StudyManager.getInstance().isStartPlaying()) {
                    setStudyList();
                } else if (getClassName().equals(StudyManager.getInstance().getListFragmentPos())) {
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
                int lastPos = ownerAdapter.getDatas().size() + 1;
                getDbData(maxid);
                if (getData().size() > lastPos) {
                    owner.scrollToPosition(getYouAdPos(lastPos));
                }
                if (!StudyManager.getInstance().isStartPlaying() && !getData().isEmpty()) {
                    setStudyList();
                } else if (getClassName().equals(StudyManager.getInstance().getListFragmentPos())) {
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
            bannerEntity.setOwnerid(BannerEntity.OWNER_EMPTY);
            bannerEntity.setPicUrl(String.valueOf(R.drawable.default_ad));
            bannerEntity.setDesc(context.getString(R.string.app_name));
            bannerEntities.add(bannerEntity);
        } else {
            bannerEntities = JSON.parseArray(preferenceData, BannerEntity.class);
        }
        ((NewsAdapter) ownerAdapter).setAdSet(bannerEntities);
    }

    @Override
    public void onResume() {
        super.onResume();
        BannerView bannerView = findBannerView();
        if (bannerView != null) {
            bannerView.startAd();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        BannerView bannerView = findBannerView();
        if (bannerView != null) {
            bannerView.stopAd();
        }
    }

    private BannerView findBannerView() {
        if (((LinearLayoutManager) (owner.getLayoutManager())).findFirstVisibleItemPosition() != 0) {
            return null;
        }
        View view = owner.getLayoutManager().getChildAt(0);
        if (view != null) {
            BannerView bannerView = view.findViewById(R.id.banner);
            if (bannerView != null && bannerView.hasData())
                return bannerView;
        }
        return null;
    }
}
