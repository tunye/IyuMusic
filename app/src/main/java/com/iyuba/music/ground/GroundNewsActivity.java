/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.ground;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.download.DownloadUtil;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.discoverrequest.GroundNewsListRequest;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * 简版新闻列表界面
 *
 * @author chentong
 * @version 1.0
 * @para "type"新闻类别（VOA慢速、常速、BBC(3)、美语、视频）
 */
public class GroundNewsActivity extends BaseListActivity<Article> {
    private GroundNewsAdapter groundNewsAdapter;
    private RecyclerView newsList;
    private String curNewsType;
    private String getTitleUrl;
    private String downloadAppUrl;
    private String lesson;
    private String app;

    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;
    //有道广告
    private boolean isVipLastState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_oper);
        articleOp = new ArticleOp();
        localInfoOp = new LocalInfoOp();
        curNewsType = this.getIntent().getExtras().getString("type");
        initWidget();
        setListener();
        changeUIByPara();
        isVipLastState = DownloadUtil.checkVip();
        if (isVipLastState) {
            initVipRecyclerView();
        } else {
            initUnVipRecyclerView();
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        groundNewsAdapter = new GroundNewsAdapter(context);
        newsList = findViewById(R.id.news_recyclerview);
        setRecyclerViewProperty(newsList);
        onRefresh(0);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse(downloadAppUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    final MyMaterialDialog dialog = new MyMaterialDialog(context);
                    dialog.setTitle(R.string.app_name).setMessage(R.string.about_market_error).
                            setPositiveButton(R.string.app_accept, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            }
        });
        groundNewsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("articleList", datas);
                    context.startActivity(intent);
                } else {
                    StudyManager.getInstance().setStartPlaying(true);
                    StudyManager.getInstance().setListFragmentPos(GroundNewsActivity.this.getClass().getName());
                    StudyManager.getInstance().setSourceArticleList(datas);
                    StudyManager.getInstance().setLesson(ParameterUrl.encode(ParameterUrl.encode(lesson)));
                    StudyManager.getInstance().setCurArticle(datas.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(curNewsType);
        toolbarOper.setText(R.string.download_app);
    }


    private void initVipRecyclerView() {
        newsList.setAdapter(groundNewsAdapter);
    }

    private void initUnVipRecyclerView() {
        mAdAdapter = new YouDaoRecyclerAdapter(GroundNewsActivity.this, groundNewsAdapter, YouDaoNativeAdPositioning.clientPositioning().addFixedPosition(4).enableRepeatingPositions(5));
        // 绑定界面组件与广告参数的映射关系，用于渲染广告
        final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
                new ViewBinder.Builder(R.layout.native_ad_row)
                        .titleId(R.id.native_title)
                        .mainImageId(R.id.native_main_image).build());
        mAdAdapter.registerAdRenderer(adRenderer);
        final Location location = null;
        final String keywords = null;
        // 声明app需要的资源，这样可以提供高质量的广告，也会节省网络带宽
        final EnumSet<RequestParameters.NativeAdAsset> desiredAssets = EnumSet.of(
                RequestParameters.NativeAdAsset.TITLE, RequestParameters.NativeAdAsset.TEXT,
                RequestParameters.NativeAdAsset.ICON_IMAGE, RequestParameters.NativeAdAsset.MAIN_IMAGE,
                RequestParameters.NativeAdAsset.CALL_TO_ACTION_TEXT);
        RequestParameters mRequestParameters = new RequestParameters.Builder()
                .location(location).keywords(keywords)
                .desiredAssets(desiredAssets).build();
        newsList.setAdapter(mAdAdapter);
        mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isVipLastState != DownloadUtil.checkVip()) {
            isVipLastState = DownloadUtil.checkVip();
            if (isVipLastState) {
                initVipRecyclerView();
            } else {
                initUnVipRecyclerView();
            }
        }
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
        GroundNewsListRequest.exeRequest(getTitleUrl, app, new IProtocolResponse<BaseListEntity<ArrayList<Article>>>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (!StudyManager.getInstance().isStartPlaying()) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(datas);
                    if (datas != null) {
                        StudyManager.getInstance().setCurArticle(datas.get(0));
                    }
                    StudyManager.getInstance().setApp(app);
                } else if (GroundNewsActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(datas);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg + context.getString(R.string.article_local));
                getDbData();
                if (!StudyManager.getInstance().isStartPlaying()) {
                    StudyManager.getInstance().setLesson("music");
                    StudyManager.getInstance().setSourceArticleList(datas);
                    if (datas != null) {
                        StudyManager.getInstance().setCurArticle(datas.get(0));
                    }
                    StudyManager.getInstance().setApp(app);
                } else if (GroundNewsActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                    StudyManager.getInstance().setSourceArticleList(datas);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(BaseListEntity<ArrayList<Article>> baseListEntity) {
                swipeRefreshLayout.setRefreshing(false);
                if (!baseListEntity.isLastPage()) {
                    ArrayList<Article> netData = baseListEntity.getData();
                    datas.addAll(netData);
                    if (!StudyManager.getInstance().isStartPlaying()) {
                        StudyManager.getInstance().setLesson("music");
                        StudyManager.getInstance().setSourceArticleList(datas);
                        StudyManager.getInstance().setCurArticle(datas.get(0));
                        StudyManager.getInstance().setApp(app);
                    } else if (GroundNewsActivity.this.getClass().getName().equals(StudyManager.getInstance().getListFragmentPos())) {
                        StudyManager.getInstance().setSourceArticleList(datas);
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
                    articleOp.saveData(netData);
                }
                groundNewsAdapter.setData(datas);
            }
        });
    }

    private void getDbData() {
        datas.addAll(articleOp.findDataByAll(app, datas.size(), 20));
        groundNewsAdapter.setData(datas);
    }
}
