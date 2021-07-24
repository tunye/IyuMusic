package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.SimpleNewsAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.MusicRequest;
import com.iyuba.music.util.Utils;

import java.util.List;

/**
 * Created by 10202 on 2016/3/4.
 */
public class MusicFragment extends BaseRecyclerViewFragment<Article> {
    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        useYouDaoAd = true;
        localInfoOp = new LocalInfoOp();
        articleOp = new ArticleOp();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ownerAdapter = new SimpleNewsAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                setStudyList();
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setCurArticle(getData().get(position));
                context.startActivity(new Intent(context, StudyActivity.class));
            }

        });
        assembleRecyclerView();
        return view;
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new MusicRequest(curPage), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                isLastPage = listEntity.isLastPage();
                onNetDataReturnSuccess(listEntity.getData());
                if (!isLastPage && curPage != 1) {
                    CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                }
                setStudyList();
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper) + context.getString(R.string.article_local));
                int lastPos = getData().size();
                getDbData();
                if (getData().size() != lastPos) {
                    owner.scrollToPosition(getYouAdPos(lastPos));
                }
                if (!StudyManager.getInstance().isStartPlaying() && !getData().isEmpty()) {
                    setStudyList();
                } else if (MusicFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void handleAfterAddAdapter(List<Article> netData) {
        super.handleAfterAddAdapter(netData);
        if (!StudyManager.getInstance().isStartPlaying()) {
            StudyManager.getInstance().setLesson("music");
            StudyManager.getInstance().setSourceArticleList(getData());
            if (getData().size() != 0) {
                StudyManager.getInstance().setCurArticle(getData().get(0));
            }
            StudyManager.getInstance().setApp("209");
        } else if (MusicFragment.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
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
    }

    private void getDbData() {
        ownerAdapter.addDatas(articleOp.findDataByMusic(getData().size(), 20));
    }
}
