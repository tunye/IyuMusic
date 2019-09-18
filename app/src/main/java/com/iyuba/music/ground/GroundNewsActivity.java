/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.ground;

import android.content.Intent;
import android.net.Uri;
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
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.discoverrequest.GroundNewsListRequest;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * 简版新闻列表界面
 *
 * @author chentong
 * @version 1.0
 * @para "type"新闻类别（VOA慢速、常速、BBC(3)、美语、视频）
 */
public class GroundNewsActivity extends BaseListActivity<Article> {
    private String curNewsType;
    private String getTitleUrl;
    private String downloadAppUrl;
    private String lesson;
    private String app;

    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        articleOp = new ArticleOp();
        localInfoOp = new LocalInfoOp();
        curNewsType = this.getIntent().getExtras().getString("type");
        useYouDaoAd = true;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        ownerAdapter = new GroundNewsAdapter(context);
        owner = findViewById(R.id.recyclerview_widget);
        assembleRecyclerView();
        onRefresh(0);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                try {
                    Uri uri = Uri.parse(downloadAppUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    final MyMaterialDialog dialog = new MyMaterialDialog(context);
                    dialog.setTitle(R.string.app_name).setMessage(R.string.about_market_error).
                            setPositiveButton(R.string.app_accept, new INoDoubleClick() {
                                @Override
                                public void activeClick(View view) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            }
        });
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("articleList", (ArrayList) ownerAdapter.getDatas());
                    context.startActivity(intent);
                } else {
                    StudyManager.getInstance().setLesson(ParameterUrl.encode(ParameterUrl.encode(lesson)));
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setCurArticle(ownerAdapter.getDatas().get(position));
                    StudyManager.getInstance().setListFragmentPos(getClassName());
                    StudyManager.getInstance().setSourceArticleList(ownerAdapter.getDatas());
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(curNewsType);
        enableToolbarOper(R.string.download_app);
    }

    @Override
    public void getNetData() {
        downloadAppUrl = "market://details?id=";
        if (curNewsType.equals(context.getString(R.string.voa_speical))) {
            getTitleUrl = "http://apps.iyuba.cn/voa/titleApi.jsp?maxid=0&pageNum=20&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.voa";
            lesson = "VOA慢速英语";
            app = "201";
        } else if (curNewsType.equals(context.getString(R.string.voa_cs))) {
            getTitleUrl = "http://apps.iyuba.cn/voa/titleApi.jsp?maxid=0&pageNum=20&category=csvoa&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.CSvoa";
            lesson = "VOA常速英语";
            app = "212";
        } else if (curNewsType.equals(context.getString(R.string.voa_video))) {
            getTitleUrl = "http://apps.iyuba.cn/voa/titleApi.jsp?maxid=0&pageNum=20&category=csvoa&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.VoaVideo";
            lesson = "VOA英语视频";
            app = "217";
        } else if (curNewsType.equals(context.getString(R.string.voa_bbc))) {
            getTitleUrl = "http://apps.iyuba.cn/minutes/titleApi.jsp?type=android&format=json&parentID=2&pageNum=20&maxid=0&pages=" + curPage;
            downloadAppUrl += "com.iyuba.bbcws";
            lesson = "BBC职场英语";
            app = "231";
        } else if (curNewsType.equals(context.getString(R.string.voa_bbc6))) {
            getTitleUrl = "http://apps.iyuba.cn/minutes/titleApi.jsp?type=android&format=json&parentID=1&pageNum=20&maxid=0&pages=" + curPage;
            downloadAppUrl += "com.iyuba.bbc";
            lesson = "BBC六分钟英语";
            app = "215";
        } else if (curNewsType.equals(context.getString(R.string.voa_ae))) {
            getTitleUrl = "http://apps.iyuba.cn/voa/titleApi.jsp?maxid=0&pageNum=20&type=json&parentID=200&pages=" + curPage;
            downloadAppUrl += "com.iyuba.AE";
            lesson = "美语怎么说";
            app = "213";
        } else if (curNewsType.equals(context.getString(R.string.voa_bbcnews))) {
            getTitleUrl = "http://apps.iyuba.cn/minutes/titleApi.jsp?type=android&format=json&parentID=3&pageNum=20&maxid=0&pages=" + curPage;
            downloadAppUrl += "com.iyuba.bbcinone";
            lesson = "BBC新闻";
            app = "221";
        } else if (curNewsType.equals(context.getString(R.string.voa_word))) {
            getTitleUrl = "http://apps.iyuba.cn/voa/titleApi.jsp?maxid=0&pageNum=20&type=json&parentID=10&pages=" + curPage;
            downloadAppUrl += "com.iyuba.WordStory";
            lesson = "VOA单词故事";
            app = "218";
        } else if (curNewsType.equals(context.getString(R.string.voa_ted))) {
            getTitleUrl = "http://apps.iyuba.cn/voa/titleTed2.jsp?maxid=0&pageNum=20&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.TEDVideo";
            lesson = "TED英语演讲";
            app = "229";
        }
        RequestClient.requestAsync(new GroundNewsListRequest(getTitleUrl, app), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> baseListEntity) {
                isLastPage = baseListEntity.isLastPage();
                onNetDataReturnSuccess(baseListEntity.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                swipeRefreshLayout.setRefreshing(false);
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper) + context.getString(R.string.article_local));
                int lastPos = getData().size();
                getDbData();
                if (getData().size() != lastPos) {
                    owner.scrollToPosition(getYouAdPos(lastPos));
                }
                if (!StudyManager.getInstance().isStartPlaying()) {
                    StudyManager.getInstance().setLesson(ParameterUrl.encode(ParameterUrl.encode(lesson)));
                    StudyManager.getInstance().setSourceArticleList(getData());
                    if (!getData().isEmpty()) {
                        StudyManager.getInstance().setCurArticle(getData().get(0));
                    }
                    StudyManager.getInstance().setApp(app);
                    StudyManager.getInstance().setListFragmentPos(getClassName());
                } else if (getClassName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(getData());
                }
            }
        });
    }

    @Override
    public void handleAfterAddAdapter(List<Article> netData) {
        super.handleAfterAddAdapter(netData);
        if (!StudyManager.getInstance().isStartPlaying()) {
            StudyManager.getInstance().setApp(app);
            StudyManager.getInstance().setLesson(ParameterUrl.encode(ParameterUrl.encode(lesson)));
            setStudyList();
        } else if (GroundNewsActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
            StudyManager.getInstance().setSourceArticleList(getData());
        }
        LocalInfo localinfo;
        for (Article temp : netData) {
            localinfo = localInfoOp.findDataById(temp.getApp(), temp.getId());
            if (localinfo.getId() == 0) {
                localinfo.setApp(temp.getApp());
                localinfo.setId(temp.getId());
                localInfoOp.saveData(localinfo);
            }
        }
    }

    private void getDbData() {
        ownerAdapter.addDatas(articleOp.findDataByAll(app, ownerAdapter.getItemCount(), 20));
    }
}
