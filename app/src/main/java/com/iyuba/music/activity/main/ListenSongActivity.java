package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;
import java.util.Iterator;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2016/3/7.
 */
public class ListenSongActivity extends BaseActivity implements IOnClickListener {
    private RecyclerView newsRecycleView;
    private ArrayList<Article> newsList;
    private SimpleNewsAdapter newsAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private TextView toolBarOperSub;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_opersub);
        context = this;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        initWidget();
        setListener();
        changeUIByPara();
    }


    @Override
    protected void initWidget() {
        super.initWidget();
        toolBarOperSub = (TextView) findViewById(R.id.toolbar_oper_sub);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setEnabled(false);
        newsRecycleView = (RecyclerView) findViewById(R.id.news_recyclerview);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new SimpleNewsAdapter(context, 1);
        newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.instance.setStartPlaying(true);
                StudyManager.instance.setListFragmentPos(ListenSongActivity.this.getClass().getName());
                StudyManager.instance.setSourceArticleList(newsList);
                StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                StudyManager.instance.setCurArticle(newsList.get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        newsRecycleView.setAdapter(newsAdapter);
        newsRecycleView.addItemDecoration(new DividerItemDecoration());
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolBarOperSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toolBarOperSub.getText().equals(getString(R.string.select_all))) {
                    newsAdapter.setDeleteAll();
                } else {
                    final MaterialDialog dialog = new MaterialDialog(context);
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
                    newsList = newsAdapter.getDataSet();
                    Article temp;
                    for (Iterator<Article> it = newsList.iterator(); it.hasNext(); ) {
                        temp = it.next();
                        if (temp.isDelete()) {
                            it.remove();
                            localInfoOp.deleteSee(temp.getId(), temp.getApp());
                        }
                    }
                    if (ListenSongActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                        StudyManager.instance.setSourceArticleList(newsList);
                    }
                    newsAdapter.setDataSet(newsList);
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

    @Override
    public void onClick(View view, Object message) {
        newsRecycleView.scrollToPosition(0);
    }

    private void getData() {
        newsList = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByListen();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getSeeTime());
            newsList.add(article);
        }
        newsAdapter.setDataSet(newsList);
        if (ListenSongActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
            StudyManager.instance.setSourceArticleList(newsList);
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
