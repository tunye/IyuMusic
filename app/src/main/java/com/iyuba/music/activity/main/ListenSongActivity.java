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
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 10202 on 2016/3/7.
 */
public class ListenSongActivity extends BaseListActivity<Article> {
    private SimpleNewsAdapter newsAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private TextView toolBarOperSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_opersub);
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        changeUIByPara();
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
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos(ListenSongActivity.this.getClass().getName());
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
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarOperSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toolBarOperSub.getText().equals(getString(R.string.select_all))) {
                    newsAdapter.setDeleteAll();
                } else {
                    final MyMaterialDialog dialog = new MyMaterialDialog(context);
                    dialog.setTitle(R.string.article_clear_all);
                    dialog.setMessage(R.string.article_clear_hint);
                    dialog.setPositiveButton(R.string.article_search_clear_sure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newsAdapter.setDataSet(new ArrayList<Article>());
                            localInfoOp.clearSee();
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toolbarOper.getText().equals(context.getString(R.string.article_edit))) {
                    newsAdapter.setDelete(true);
                    toolBarOperSub.setText(R.string.article_select_all);
                    toolbarOper.setText(R.string.app_del);
                } else {
                    newsAdapter.setDelete(false);
                    toolbarOper.setText(R.string.article_edit);
                    toolBarOperSub.setText(R.string.article_clear);
                    datas = newsAdapter.getDataSet();
                    Article temp;
                    for (Iterator<Article> it = datas.iterator(); it.hasNext(); ) {
                        temp = it.next();
                        if (temp.isDelete()) {
                            it.remove();
                            localInfoOp.deleteSee(temp.getId(), temp.getApp());
                        }
                    }
                    if (ListenSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
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
        title.setText(R.string.classify_history);
        toolbarOper.setText(R.string.article_edit);
        toolBarOperSub.setText(R.string.article_clear);
        getData();
    }

    private void getData() {
        datas = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByListen();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getSeeTime());
            datas.add(article);
        }
        newsAdapter.setDataSet(datas);
        if (ListenSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(datas);
        }
    }

    @Override
    public void onBackPressed() {
        if (newsAdapter.isDelete()) {
            newsAdapter.setDelete(false);
            toolbarOper.setText(R.string.article_edit);
            toolBarOperSub.setText(R.string.article_clear);
        } else {
            super.onBackPressed();
        }
    }

}
