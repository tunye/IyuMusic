package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.DownloadNewsAdapter;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.ground.VideoPlayerActivity;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2016/3/7.
 */
public class DownloadSongActivity extends BaseActivity implements IOnClickListener {
    private RecyclerView newsRecycleView;
    private ArrayList<Article> newsList;
    private DownloadNewsAdapter newsAdapter;
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
        ((SimpleItemAnimator) newsRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        newsAdapter = new DownloadNewsAdapter(context);
        newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String app = newsList.get(position).getApp();
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    ArrayList<Article> temp = new ArrayList<>();
                    temp.add(newsList.get(position));
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("articleList", temp);
                    context.startActivity(intent);
                } else {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(DownloadSongActivity.this.getClass().getName());
                    StudyManager.getInstance().setSourceArticleList(newsList);
                    StudyManager.getInstance().setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.getInstance().getAppName())));
                    StudyManager.getInstance().setCurArticle(newsList.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
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
                    dialog.setMessage(R.string.article_clear_download_hint);
                    dialog.setPositiveButton(R.string.article_search_clear_sure, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newsAdapter.setDataSet(new ArrayList<Article>());
                            Article temp;
                            for (Iterator<Article> it = newsList.iterator(); it.hasNext(); ) {
                                temp = it.next();
                                it.remove();
                                deleteFile(temp.getId(), temp.getApp());
                                localInfoOp.updateDownload(temp.getId(), temp.getApp(), 0);
                            }
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
            public void onClick(View v) {
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
                            deleteFile(temp.getId(), temp.getApp());
                            localInfoOp.updateDownload(temp.getId(), temp.getApp(), 0);
                        }
                    }
                    if (DownloadSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList(newsList);
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
        toolbarOper.setText(R.string.article_edit);
        toolBarOperSub.setText(R.string.article_clear);
        getData();
    }

    @Override
    public void onClick(View view, Object message) {
        newsRecycleView.scrollToPosition(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        newsAdapter.notifyDataSetChanged();
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
        if (DownloadSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(newsList);
        }
    }

    private void deleteFile(int id, String app) {
        String baseUrl = ConstantManager.getInstance().getMusicFolder() + File.separator;
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
            toolbarOper.setText(R.string.article_edit);
            toolBarOperSub.setText(R.string.article_clear);
        } else {
            super.onBackPressed();
        }
    }
}
