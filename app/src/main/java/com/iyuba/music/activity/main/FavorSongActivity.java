package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.ground.VideoPlayerActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.FavorSynRequest;
import com.iyuba.music.request.newsrequest.FavorRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

/**
 * Created by 10202 on 2016/3/7.
 */
public class FavorSongActivity extends BaseListActivity<Article> {
    private SimpleNewsAdapter newsAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private TextView toolBarOperSub;
    private IyubaDialog waittingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_opersub);
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.article_fav_synchroing));
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolBarOperSub = findViewById(R.id.toolbar_oper_sub);
        toolbarOper = findViewById(R.id.toolbar_oper);
        swipeRefreshLayout.setEnabled(false);
        RecyclerView newsRecycleView = findViewById(R.id.news_recyclerview);
        setRecyclerViewProperty(newsRecycleView);
        newsAdapter = new SimpleNewsAdapter(context, 1);
        newsAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String app = datas.get(position).getApp();
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    ArrayList<Article> temp = new ArrayList<>();
                    temp.add(datas.get(position));
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("articleList", temp);
                    context.startActivity(intent);
                } else {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(FavorSongActivity.this.getClass().getName());
                    StudyManager.getInstance().setSourceArticleList(datas);
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setCurArticle(datas.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        newsRecycleView.setAdapter(newsAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarOperSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolBarOperSub.getText().equals(getString(R.string.article_synchro))) {
                    if (AccountManager.getInstance().checkUserLogin()) {
                        getYunFavor();
                    } else {
                        CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                            @Override
                            public void finish() {
                                getYunFavor();
                            }
                        });
                    }
                } else {
                    newsAdapter.setDeleteAll();
                }
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarOper.getText().equals(context.getString(R.string.article_edit))) {
                    newsAdapter.setDelete(true);
                    toolbarOper.setText(R.string.app_del);
                    toolBarOperSub.setText(R.string.article_select_all);
                } else {
                    newsAdapter.setDelete(false);
                    toolbarOper.setText(R.string.article_edit);
                    toolBarOperSub.setText(R.string.article_synchro);
                    datas = newsAdapter.getDataSet();
                    Article temp;
                    for (Iterator<Article> it = datas.iterator(); it.hasNext(); ) {
                        temp = it.next();
                        if (temp.isDelete()) {
                            it.remove();
                            cancelFavor(temp);
                        }
                    }
                    if (FavorSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList(datas);
                    }
                    newsAdapter.setDataSet(datas);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.classify_favor);
        toolBarOperSub.setText(R.string.article_synchro);
        toolbarOper.setText(R.string.article_edit);
    }

    protected void changeUIResumeByPara() {
        getData();
    }

    private void getData() {
        datas = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByFavourite();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getFavTime());
            datas.add(article);
        }
        newsAdapter.setDataSet(datas);
        if (FavorSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(datas);
        }
    }

    private void cancelFavor(final Article article) {
        FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), article.getId(), "del"), new IProtocolResponse<String>() {
            @Override
            public void onNetError(String msg) {
                if (!article.getApp().equals("209")) {
                    localInfoOp.updateFavor(article.getId(), article.getApp(), 0);
                }
            }

            @Override
            public void onServerError(String msg) {
                if (!article.getApp().equals("209")) {
                    localInfoOp.updateFavor(article.getId(), article.getApp(), 0);
                }
            }

            @Override
            public void response(String result) {
                if (result.equals("del")) {
                    localInfoOp.updateFavor(article.getId(), article.getApp(), 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (newsAdapter.isDelete()) {
            newsAdapter.setDelete(false);
            toolbarOper.setText(R.string.article_edit);
            toolBarOperSub.setText(R.string.article_synchro);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {// 登录的返回结果
            getYunFavor();
        }
    }


    private void getYunFavor() {
        waittingDialog.show();
        FavorSynRequest.exeRequest(FavorSynRequest.generateUrl(AccountManager.getInstance().getUserId()), new IProtocolResponse<BaseListEntity<ArrayList<Article>>>() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(BaseListEntity<ArrayList<Article>> listEntity) {
                ArrayList<Article> netData = listEntity.getData();
                if (netData.size() != 0) {
                    LocalInfo localinfo;
                    for (Article temp : netData) {
                        temp.setApp(ConstantManager.appId);
                        localinfo = localInfoOp.findDataById(temp.getApp(), temp.getId());
                        if (localinfo.getId() == 0) {
                            localinfo.setApp(temp.getApp());
                            localinfo.setId(temp.getId());
                            localinfo.setFavourite(1);
                            localinfo.setFavTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                            localInfoOp.saveData(localinfo);
                        } else {
                            if (localinfo.getFavourite() != 1) {
                                localInfoOp.updateFavor(localinfo.getId(), localinfo.getApp(), 1);
                            }
                        }
                    }
                    articleOp.saveData(netData);
                    toolBarOperSub.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            waittingDialog.dismiss();
                            getData();
                        }
                    }, 500);
                }
            }
        });
    }
}
