package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.AnnouncerNewsRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/1/2.
 */
public class AnnouncerNewsList extends BaseListActivity<Article> {
    private SimpleNewsAdapter newsAdapter;
    private Announcer announcer;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_oper);
        announcer = new AnnouncerOp().findById(getIntent().getStringExtra("announcer"));
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        RecyclerView newsRecycleView = findViewById(R.id.news_recyclerview);
        setRecyclerViewProperty(newsRecycleView);
        newsAdapter = new SimpleNewsAdapter(context);
        if (DownloadUtil.checkVip()) {
            newsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(AnnouncerNewsList.this.getClass().getName());
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
                    StudyManager.getInstance().setListFragmentPos(AnnouncerNewsList.this.getClass().getName());
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
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    SocialManager.getInstance().pushFriendId(announcer.getUid());
                    Intent intent = new Intent(context, PersonalHomeActivity.class);
                    intent.putExtra("needpop", true);
                    startActivity(intent);
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            SocialManager.getInstance().pushFriendId(announcer.getUid());
                            Intent intent = new Intent(context, PersonalHomeActivity.class);
                            intent.putExtra("needpop", true);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.article_announcer_home);
        title.setText(announcer.getName());
    }

    protected void getNetData() {
        AnnouncerNewsRequest.exeRequest(AnnouncerNewsRequest.generateUrl(announcer.getId(), curPage), new IProtocolResponse<BaseListEntity<ArrayList<Article>>>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(datas);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
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
                    if (curPage == 1) {
                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                    if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList((ArrayList<Article>) datas);
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

    @Override
    public void onBackPressed() {
        if (!mipush) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    private void getDbData() {
        datas.addAll(articleOp.findDataByAnnouncer(announcer.getId(), datas.size(), 20));
        newsAdapter.setDataSet(datas);
    }
}
