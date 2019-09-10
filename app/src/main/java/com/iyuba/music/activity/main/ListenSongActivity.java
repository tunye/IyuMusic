package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 10202 on 2016/3/7.
 */
public class ListenSongActivity extends BaseListActivity<Article> {
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private TextView toolBarOperSub;

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
        toolBarOperSub = findViewById(R.id.toolbar_oper_sub);
        swipeRefreshLayout.setEnabled(false);
        owner = findViewById(R.id.recyclerview_widget);
        ownerAdapter = new SimpleNewsAdapter(context, 1);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos(ListenSongActivity.this.getClass().getName());
                StudyManager.getInstance().setSourceArticleList(getData());
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setCurArticle(getData().get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }
        });
        assembleRecyclerView();
    }

    @Override
    public void setListener() {
        super.setListener();
        toolBarOperSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (toolBarOperSub.getText().equals(getString(R.string.select_all))) {
                    ((SimpleNewsAdapter) ownerAdapter).setDeleteAll();
                } else {
                    final MyMaterialDialog dialog = new MyMaterialDialog(context);
                    dialog.setTitle(R.string.article_clear_all);
                    dialog.setMessage(R.string.article_clear_hint);
                    dialog.setPositiveButton(R.string.article_search_clear_sure, new INoDoubleClick() {
                        @Override
                        public void onClick(View view) {
                            super.onClick(view);
                            ownerAdapter.setDataSet(new ArrayList<Article>());
                            localInfoOp.clearSee();
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
                        @Override
                        public void onClick(View view) {
                            super.onClick(view);
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
                    ((SimpleNewsAdapter) ownerAdapter).setDelete(true);
                    toolBarOperSub.setText(R.string.article_select_all);
                    toolbarOper.setText(R.string.app_del);
                } else {
                    ((SimpleNewsAdapter) ownerAdapter).setDelete(false);
                    toolbarOper.setText(R.string.article_edit);
                    toolBarOperSub.setText(R.string.article_clear);
                    List<Article> datas = new ArrayList<>(getData());
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
                    ownerAdapter.setDataSet(datas);
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.classify_history);
        toolbarOper.setText(R.string.article_edit);
        toolBarOperSub.setText(R.string.article_clear);
        loadDataFromDb();
    }

    private void loadDataFromDb() {
        List<Article> datas = new ArrayList<>();
        List<LocalInfo> temp = localInfoOp.findDataByListen();
        Article article;
        for (LocalInfo local : temp) {
            article = articleOp.findById(local.getApp(), local.getId());
            article.setExpireContent(local.getSeeTime());
            datas.add(article);
        }
        ownerAdapter.setDataSet(datas);
        if (ListenSongActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(datas);
        }
    }

    @Override
    public void onBackPressed() {
        if (((SimpleNewsAdapter) ownerAdapter).isDelete()) {
            ((SimpleNewsAdapter) ownerAdapter).setDelete(false);
            toolbarOper.setText(R.string.article_edit);
            toolBarOperSub.setText(R.string.article_clear);
        } else {
            super.onBackPressed();
        }
    }
}
