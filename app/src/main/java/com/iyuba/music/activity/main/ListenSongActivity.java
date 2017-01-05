package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.AnnouncerNewsAdapter;
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

/**
 * Created by 10202 on 2016/3/7.
 */
public class ListenSongActivity extends BaseActivity implements IOnClickListener {
    private RecyclerView newsRecycleView;
    private ArrayList<Article> newsList;
    private AnnouncerNewsAdapter newsAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private MySwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_without_oper);
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
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setEnabled(false);
        newsRecycleView = (RecyclerView) findViewById(R.id.news_recyclerview);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new AnnouncerNewsAdapter(context, 1);
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
        findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.classify_history);
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
}
