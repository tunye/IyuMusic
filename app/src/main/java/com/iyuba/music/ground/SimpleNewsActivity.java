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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.ArticleOp;
import com.iyuba.music.entity.artical.LocalInfo;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.discoverrequest.SimpleNewsListRequest;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * 简版新闻列表界面
 *
 * @author chentong
 * @version 1.0
 * @para "type"新闻类别（VOA慢速、常速、BBC(3)、美语、视频）
 */
public class SimpleNewsActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private MySwipeRefreshLayout swipeRefreshLayout;
    private SimpleNewsAdapter simpleNewsAdapter;
    private ArrayList<Article> newsArrayList = new ArrayList<>();
    private RecyclerView newsList;
    private String curNewsType;
    private String getTitleUrl;
    private String downloadAppUrl;
    private String lesson;
    private String app;
    private int curPage;
    private boolean isLastPage = false;

    private ArticleOp articleOp;
    private LocalInfoOp localInfoOp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_oper);
        context = this;
        articleOp = new ArticleOp();
        localInfoOp = new LocalInfoOp();
        curNewsType = this.getIntent().getExtras().getString("type");
        initWidget();
        setListener();
        changeUIByPara();
        simpleNewsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (app.equals("229") || app.equals("217") || app.equals("213")) {
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("pos", position);
                    intent.putExtra("articleList", newsArrayList);
                    context.startActivity(intent);
                } else {
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setListFragmentPos(SimpleNewsActivity.this.getClass().getName());
                    StudyManager.instance.setSourceArticleList(newsArrayList);
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(lesson)));
                    StudyManager.instance.setCurArticle(newsArrayList.get(position));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        simpleNewsAdapter = new SimpleNewsAdapter(context);
        newsList = (RecyclerView) findViewById(R.id.news_recyclerview);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        newsList.setLayoutManager(new LinearLayoutManager(context));
        newsList.setAdapter(simpleNewsAdapter);
        newsList.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
    }

    @Override
    protected void setListener() {
        super.setListener();
        findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolbarOper.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Uri uri = Uri.parse(downloadAppUrl);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    final MaterialDialog dialog = new MaterialDialog(context);
                    dialog.setTitle(R.string.app_name).setMessage(R.string.about_market_error).
                            setPositiveButton(R.string.accept, new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dialog.dismiss();
                                }
                            });
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(curNewsType);
        toolbarOper.setText(R.string.download_app);
    }

    @Override
    public void onClick(View view, Object message) {
        newsList.scrollToPosition(0);
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        newsArrayList = new ArrayList<>();
        isLastPage = false;
        getData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (newsArrayList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.artical_load_all);
        }
    }

    /**
     *
     */
    private void getData() {
        downloadAppUrl = "market://details?id=";
        if (curNewsType.equals(context.getString(R.string.voa_speical))) {
            getTitleUrl = "http://apps.iyuba.com/voa/titleApi.jsp?maxid=0&pageNum=20&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.voa";
            lesson = "VOA慢速英语";
            app = "201";
        } else if (curNewsType.equals(context.getString(R.string.voa_cs))) {
            getTitleUrl = "http://apps.iyuba.com/voa/titleApi.jsp?maxid=0&pageNum=20&category=csvoa&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.CSvoa";
            lesson = "VOA常速英语";
            app = "212";
        } else if (curNewsType.equals(context.getString(R.string.voa_video))) {
            getTitleUrl = "http://apps.iyuba.com/voa/titleApi.jsp?maxid=0&pageNum=20&category=csvoa&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.VoaVideo";
            lesson = "VOA英语视频";
            app = "217";
        } else if (curNewsType.equals(context.getString(R.string.voa_bbc))) {
            getTitleUrl = "http://apps.iyuba.com/minutes/titleApi.jsp?type=android&format=json&parentID=2&pageNum=20&maxid=0&pages=" + curPage;
            downloadAppUrl += "com.iyuba.bbcws";
            lesson = "BBC职场英语";
            app = "231";
        } else if (curNewsType.equals(context.getString(R.string.voa_bbc6))) {
            getTitleUrl = "http://apps.iyuba.com/minutes/titleApi.jsp?type=android&format=json&parentID=1&pageNum=20&maxid=0&pages=" + curPage;
            downloadAppUrl += "com.iyuba.bbc";
            lesson = "BBC六分钟英语";
            app = "215";
        } else if (curNewsType.equals(context.getString(R.string.voa_ae))) {
            getTitleUrl = "http://apps.iyuba.com/voa/titleApi.jsp?maxid=0&pageNum=20&type=json&parentID=200&pages=" + curPage;
            downloadAppUrl += "com.iyuba.AE";
            lesson = "美语怎么说";
            app = "213";
        } else if (curNewsType.equals(context.getString(R.string.voa_bbcnews))) {
            getTitleUrl = "http://apps.iyuba.com/minutes/titleApi.jsp?type=android&format=json&parentID=3&pageNum=20&maxid=0&pages=" + curPage;
            downloadAppUrl += "com.iyuba.bbcinone";
            lesson = "BBC新闻";
            app = "221";
        } else if (curNewsType.equals(context.getString(R.string.voa_word))) {
            getTitleUrl = "http://apps.iyuba.com/voa/titleApi.jsp?maxid=0&pageNum=20&type=json&parentID=10&pages=" + curPage;
            downloadAppUrl += "com.iyuba.WordStory";
            lesson = "VOA单词故事";
            app = "218";
        } else if (curNewsType.equals(context.getString(R.string.voa_ted))) {
            getTitleUrl = "http://apps.iyuba.com/voa/titleTed2.jsp?maxid=0&pageNum=20&type=json&pages=" + curPage;
            downloadAppUrl += "com.iyuba.TEDVideo";
            lesson = "TED英语演讲";
            app = "229";
        }
        SimpleNewsListRequest.getInstance().exeRequest(getTitleUrl, app, new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.artical_local));
                getDbData();
                if (!StudyManager.instance.isStartPlaying()) {
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(newsArrayList);
                    StudyManager.instance.setCurArticle(newsArrayList.get(0));
                    StudyManager.instance.setApp(app);
                } else if (SimpleNewsActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(newsArrayList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.artical_local));
                getDbData();
                if (!StudyManager.instance.isStartPlaying()) {
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setSourceArticleList(newsArrayList);
                    StudyManager.instance.setCurArticle(newsArrayList.get(0));
                    StudyManager.instance.setApp(app);
                } else if (SimpleNewsActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(newsArrayList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity baseListEntity = (BaseListEntity) object;
                if (!baseListEntity.isLastPage()) {
                    ArrayList<Article> netData = (ArrayList<Article>) baseListEntity.getData();
                    newsArrayList.addAll(netData);
                    if (!StudyManager.instance.isStartPlaying()) {
                        StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                        StudyManager.instance.setSourceArticleList(newsArrayList);
                        StudyManager.instance.setCurArticle(newsArrayList.get(0));
                        StudyManager.instance.setApp(app);
                    } else if (SimpleNewsActivity.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                        StudyManager.instance.setSourceArticleList(newsArrayList);
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
                simpleNewsAdapter.setData(newsArrayList);
            }
        });
    }

    private void getDbData() {
        newsArrayList.addAll(articleOp.findDataByAll(app, newsArrayList.size(), 20));
        simpleNewsAdapter.setData(newsArrayList);
    }
}
