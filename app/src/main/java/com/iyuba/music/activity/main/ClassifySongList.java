package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.ClassifyNewsRequest;
import com.iyuba.music.widget.CustomToast;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/1/2.
 */
public class ClassifySongList extends BaseListActivity<Article> {
    private SimpleNewsAdapter newsAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private int classify;
    private String classifyName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_without_oper);
        classify = getIntent().getIntExtra("classify", 0);
        classifyName = getIntent().getStringExtra("classifyName");
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        RecyclerView newsRecycleView = findViewById(R.id.news_recyclerview);
        setRecyclerViewProperty(newsRecycleView);
        newsAdapter = new SimpleNewsAdapter(context);
        if (DownloadUtil.checkVip()) {
            newsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(ClassifySongList.this.getClass().getName());
                    StudyManager.getInstance().setSourceArticleList(datas);
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setCurArticle(datas.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            newsRecycleView.setAdapter(newsAdapter);
        } else {
            mAdAdapter = new YouDaoRecyclerAdapter(this, newsAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));
            setYouDaoMsg();
            newsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(ClassifySongList.this.getClass().getName() + classify);
                    StudyManager.getInstance().setSourceArticleList(datas);
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setCurArticle(datas.get(mAdAdapter.getOriginalPosition(position)));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            newsRecycleView.setAdapter(mAdAdapter);
        }
        onRefresh(0);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        if (classifyName.length() < 10) {
            title.setText(classifyName);
        } else {
            title.setText(classifyName.substring(0, 8) + "...");
        }
    }

    @Override
    public void onBackPressed() {
        if (!mipush) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    @Override
    protected void getNetData() {
        ClassifyNewsRequest.exeRequest(ClassifyNewsRequest.generateUrl(classify, curPage), new IProtocolResponse<BaseListEntity<ArrayList<Article>>>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if ((ClassifySongList.this.getClass().getName() + classify).equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(datas);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (ClassifySongList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(datas);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(BaseListEntity<ArrayList<Article>> listEntity) {
                swipeRefreshLayout.setRefreshing(false);
                ArrayList<Article> netData = listEntity.getData();
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.getInstance().showToast(R.string.article_load_all);
                } else {
                    datas.addAll(netData);
                    newsAdapter.setDataSet(datas);
                    if (curPage != 1) {
                        CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                    if (ClassifySongList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList(datas);
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
                    articleOp.saveData(netData);
                }
            }
        });
    }

    private void getDbData() {
        datas.addAll(articleOp.findDataByCategory(ConstantManager.appId, classify, datas.size(), 20));
        newsAdapter.setDataSet(datas);
    }
}
