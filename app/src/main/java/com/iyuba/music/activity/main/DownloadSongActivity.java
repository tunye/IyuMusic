package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.DownloadNewsAdapter;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.ArticleOp;
import com.iyuba.music.entity.artical.LocalInfo;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.ground.VideoPlayer;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 10202 on 2016/3/7.
 */
public class DownloadSongActivity extends BaseActivity implements IOnClickListener {
    private RecyclerView newsRecycleView;
    private ArrayList<Article> newsList;
    private DownloadNewsAdapter newsAdapter;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_oper);
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
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        newsRecycleView = (RecyclerView) findViewById(R.id.news_recyclerview);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new DownloadNewsAdapter(context);
        newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String app = newsList.get(position).getApp();
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    ArrayList<Article> temp = new ArrayList<>();
                    temp.add(newsList.get(position));
                    Intent intent = new Intent(context, VideoPlayer.class);
                    intent.putExtra("articleList", temp);
                    context.startActivity(intent);
                } else {
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setListFragmentPos(DownloadSongActivity.this.getClass().getName());
                    StudyManager.instance.setSourceArticleList(newsList);
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setCurArticle(newsList.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        newsRecycleView.setAdapter(newsAdapter);
        newsRecycleView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
    }

    @Override
    protected void setListener() {
        super.setListener();
        findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarOper.getText().equals(context.getString(R.string.artical_edit))) {
                    newsAdapter.setDelete(true);
                    toolbarOper.setText(R.string.artical_edit_finish);
                } else {
                    newsAdapter.setDelete(false);
                    toolbarOper.setText(R.string.artical_edit);
                    newsList = newsAdapter.getDataSet();
                    Article temp;
                    for (Iterator<Article> it = newsList.iterator(); it.hasNext(); ) {
                        temp = it.next();
                        if (temp.isDelete()) {
                            it.remove();
                            deleteFile(temp.getId(), temp.getApp());
                            localInfoOp.updateDownload(temp.getId(), temp.getApp(), 0);
                        }
                    }
                    if (DownloadSongActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
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
        title.setText(R.string.classify_local);
        toolbarOper.setText(R.string.artical_edit);
        getData();
    }

    @Override
    public void onClick(View view, Object message) {
        newsRecycleView.scrollToPosition(0);
    }

    private void getData() {
        newsList = new ArrayList<>();
        ArrayList<LocalInfo> temp = localInfoOp.findDataByDownload();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getSeeTime());
            article.setId(local.getId());
            article.setApp(local.getApp());
            newsList.add(article);
        }
        newsAdapter.setDataSet(newsList);
        if (DownloadSongActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
            StudyManager.instance.setSourceArticleList(newsList);
        }
    }

    private void deleteFile(int id, String app) {
        String baseUrl = ConstantManager.instance.getMusicFolder() + File.separator;
        if (app.equals("209")) {
            File deleteFile = new File(baseUrl + id + ".mp3");
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
            deleteFile = new File(baseUrl + id + "s.mp3");
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
        } else if (app.equals("229") || app.equals("217") || app.equals("213")) {
            File deleteFile = new File(baseUrl + app + "-" + id + ".mp4");
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
        } else {
            File deleteFile = new File(baseUrl + app + "-" + id + ".mp3");
            if (deleteFile.exists()) {
                deleteFile.delete();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (newsAdapter.isDelete()) {
            newsAdapter.setDelete(false);
            toolbarOper.setText(R.string.artical_edit);
        } else {
            super.onBackPressed();
        }
    }
}
