package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.ThreadUtils;
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
import java.util.List;

/**
 * Created by 10202 on 2016/3/7.
 */
public class FavorSongActivity extends BaseListActivity<Article> {
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private IyubaDialog waittingDialog;

    @Override
    public int getLayoutId() {
        return R.layout.classify_with_opersub;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.article_fav_synchroing));
        swipeRefreshLayout.setEnabled(false);
        owner = findViewById(R.id.recyclerview_widget);
        ownerAdapter = new SimpleNewsAdapter(context, 1);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String app = getData().get(position).getApp();
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    ArrayList<Article> temp = new ArrayList<>();
                    temp.add(getData().get(position));
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("articleList", temp);
                    context.startActivity(intent);
                } else {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(FavorSongActivity.this.getClass().getName());
                    StudyManager.getInstance().setSourceArticleList(getData());
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setCurArticle(getData().get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }

        });
        assembleRecyclerView();
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOperSub.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (toolbarOperSub.getText().equals(getString(R.string.article_synchro))) {
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
                    ((SimpleNewsAdapter) ownerAdapter).setDeleteAll();
                }
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (toolbarOper.getText().equals(context.getString(R.string.article_edit))) {
                    ((SimpleNewsAdapter) ownerAdapter).setDelete(true);
                    enableToolbarOper(R.string.app_del);
                    toolbarOperSub.setText(R.string.article_select_all);
                } else {
                    ((SimpleNewsAdapter) ownerAdapter).setDelete(false);
                    enableToolbarOper(R.string.article_edit);
                    toolbarOperSub.setText(R.string.article_synchro);
                    List<Article> datas = new ArrayList<>(getData());
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
                    ownerAdapter.setDataSet(datas);
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.classify_favor);
        enableToolbarOper(R.string.article_edit);
        enableToolbarOperSub(R.string.article_synchro);
    }

    public void onActivityResumed() {
        loadDataFromDb();
    }

    private void loadDataFromDb() {
        List<Article> datas = new ArrayList<>();
        List<LocalInfo> temp = localInfoOp.findDataByFavourite();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getFavTime());
            datas.add(article);
        }
        ownerAdapter.setDataSet(datas);
        if (FavorSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(datas);
        }
    }

    private void cancelFavor(final Article article) {
        RequestClient.requestAsync(new FavorRequest(AccountManager.getInstance().getUserId(), article.getId(), "del"), new SimpleRequestCallBack<String>() {
            @Override
            public void onSuccess(String s) {
                if (s.equals("del")) {
                    localInfoOp.updateFavor(article.getId(), article.getApp(), 0);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                if (!article.getApp().equals("209")) {
                    localInfoOp.updateFavor(article.getId(), article.getApp(), 0);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (((SimpleNewsAdapter) ownerAdapter).isDelete()) {
            ((SimpleNewsAdapter) ownerAdapter).setDelete(false);
            enableToolbarOper(R.string.article_edit);
            toolbarOperSub.setText(R.string.article_synchro);
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
        RequestClient.requestAsync(new FavorSynRequest(AccountManager.getInstance().getUserId()), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                List<Article> netData = listEntity.getData();
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
                    ThreadUtils.postOnUiThreadDelay(new Runnable() {
                        @Override
                        public void run() {
                            waittingDialog.dismiss();
                            loadDataFromDb();
                        }
                    }, 500);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }
}
