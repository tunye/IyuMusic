package com.iyuba.music.activity.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.AnnouncerNewsRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.CustomDialog;

import java.util.List;

/**
 * Created by 10202 on 2016/1/2.
 */
public class AnnouncerNewsList extends BaseListActivity<Article> {
    public static final String ANNOUNCER = "announcer";
    private Announcer announcer;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        announcer = new AnnouncerOp().findById(getIntent().getStringExtra(ANNOUNCER));
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
                setStudyList();
                StudyManager.getInstance().setCurArticle(getData().get(position));
                StudyManager.getInstance().setStartPlaying(true);
                context.startActivity(new Intent(context, StudyActivity.class));
            }
        });
        assembleRecyclerView();
        onRefresh(0);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    SocialManager.getInstance().pushFriendId(announcer.getUid());
                    Intent intent = new Intent(context, PersonalHomeActivity.class);
                    startActivity(intent);
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            SocialManager.getInstance().pushFriendId(announcer.getUid());
                            Intent intent = new Intent(context, PersonalHomeActivity.class);
                            startActivity(intent);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        enableToolbarOper(R.string.article_announcer_home);
        title.setText(announcer.getName());
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new AnnouncerNewsRequest(announcer.getId(), curPage), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
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
                int lastPos = ownerAdapter.getDatas().size();
                getDbData();
                if (getData().size() > lastPos) {
                    owner.scrollToPosition(getYouAdPos(lastPos));
                }
                if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onNetDataReturnSuccess(List<Article> netData) {
        super.onNetDataReturnSuccess(netData);
        if (isLastPage) {
            return;
        }
        if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
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
        ownerAdapter.addDatas(articleOp.findDataByAnnouncer(announcer.getId(), getData().size(), 20));
    }
}
