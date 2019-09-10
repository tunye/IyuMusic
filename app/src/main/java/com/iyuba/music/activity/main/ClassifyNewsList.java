package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.ClassifyNewsRequest;
import com.iyuba.music.util.Utils;

import java.util.List;

/**
 * Created by 10202 on 2016/1/2.
 */
public class ClassifyNewsList extends BaseListActivity<Article> {
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    private int classify;
    private String classifyName;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        classify = getIntent().getIntExtra("classify", 0);
        classifyName = getIntent().getStringExtra("classifyName");
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
        useYouDaoAd = true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        owner = findViewById(R.id.recyclerview_widget);
        ownerAdapter = new SimpleNewsAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos(ClassifyNewsList.this.getClass().getName());
                StudyManager.getInstance().setSourceArticleList(getData());
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setCurArticle(getData().get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }
        });
        assembleRecyclerView();
        onRefresh(0);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        if (classifyName.length() < 10) {
            title.setText(classifyName);
        } else {
            title.setText(classifyName.substring(0, 8) + "...");
        }
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new ClassifyNewsRequest(classify, curPage), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                isLastPage = listEntity.isLastPage();
                onNetDataReturnSuccess(listEntity.getData());
                if (!isLastPage && curPage != 1) {
                    CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper) + context.getString(R.string.article_local));
                getDbData();
                if (ClassifyNewsList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void handleAfterAddAdapter(List<Article> netData) {
        super.handleAfterAddAdapter(netData);
        if (ClassifyNewsList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(getData());
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

    @Override
    public void onBackPressed() {
        if (!mipush) {
            super.onBackPressed();
        } else {
            startActivity(new Intent(context, MainActivity.class));
        }
    }

    private void getDbData() {
        ownerAdapter.addDatas(articleOp.findDataByCategory(ConstantManager.appId, classify, getData().size(), 20));
    }
}
