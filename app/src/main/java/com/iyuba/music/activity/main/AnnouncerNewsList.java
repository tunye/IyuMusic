package com.iyuba.music.activity.main;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.adapter.study.AnnouncerNewsAdapter;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.ArticleOp;
import com.iyuba.music.entity.artical.LocalInfo;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.AnnouncerNewsRequest;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.youdao.sdk.nativeads.RequestParameters;
import com.youdao.sdk.nativeads.ViewBinder;
import com.youdao.sdk.nativeads.YouDaoNativeAdPositioning;
import com.youdao.sdk.nativeads.YouDaoNativeAdRenderer;
import com.youdao.sdk.nativeads.YouDaoRecyclerAdapter;

import java.util.ArrayList;
import java.util.EnumSet;

/**
 * Created by 10202 on 2016/1/2.
 */
public class AnnouncerNewsList extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private RecyclerView newsRecycleView;
    private ArrayList<Article> newsList;
    private AnnouncerNewsAdapter newsAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int curPage;
    private boolean isLastPage = false;
    private Announcer announcer;
    private LocalInfoOp localInfoOp;
    private ArticleOp articleOp;
    //有道广告
    private YouDaoRecyclerAdapter mAdAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.classify_with_oper);
        context = this;
        announcer = new AnnouncerOp().findById(getIntent().getStringExtra("announcer"));
        isLastPage = false;
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
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        newsRecycleView.setLayoutManager(new LinearLayoutManager(context));
        newsAdapter = new AnnouncerNewsAdapter(context);
        if (DownloadService.checkVip()) {
            newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setListFragmentPos(AnnouncerNewsList.this.getClass().getName());
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
        } else {
            mAdAdapter = new YouDaoRecyclerAdapter(this, newsAdapter,
                    YouDaoNativeAdPositioning
                            .newBuilder().addFixedPosition(3).enableRepeatingPositions(10).build());
            newsAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    StudyManager.instance.setStartPlaying(true);
                    StudyManager.instance.setListFragmentPos(AnnouncerNewsList.this.getClass().getName());
                    StudyManager.instance.setSourceArticleList(newsList);
                    StudyManager.instance.setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.instance.getAppName())));
                    StudyManager.instance.setCurArticle(newsList.get(mAdAdapter.getOriginalPosition(position)));
                    context.startActivity(new Intent(context, StudyActivity.class));
                }

                @Override
                public void onItemLongClick(View view, int position) {
                }
            });
            // 绑定界面组件与广告参数的映射关系，用于渲染广告
            final YouDaoNativeAdRenderer adRenderer = new YouDaoNativeAdRenderer(
                    new ViewBinder.Builder(R.layout.native_ad_row)
                            .titleId(R.id.native_title)
                            .textId(R.id.native_text)
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
            newsRecycleView.setAdapter(mAdAdapter);
            mAdAdapter.loadAds(ConstantManager.YOUDAOSECRET, mRequestParameters);
        }
        newsRecycleView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
    }

    @Override
    protected void setListener() {
        super.setListener();
        findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.instance.checkUserLogin()) {
                    SocialManager.instance.pushFriendId(announcer.getUid());
                    Intent intent = new Intent(context, PersonalHomeActivity.class);
                    intent.putExtra("needpop", true);
                    startActivity(intent);
                } else {
                    CustomDialog.showLoginDialog(context);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.artical_announcer_home);
        title.setText(announcer.getName());
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        newsList = new ArrayList<>();
        isLastPage = false;
        getNewsData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (newsList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getNewsData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.artical_load_all);
        }
    }

    private void getNewsData() {
        AnnouncerNewsRequest.getInstance().exeRequest(AnnouncerNewsRequest.getInstance().generateUrl(announcer.getId(), curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.artical_local));
                getDbData();
                if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg + context.getString(R.string.artical_local));
                getDbData();
                if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                    StudyManager.instance.setSourceArticleList(newsList);
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.INSTANCE.showToast(R.string.artical_load_all);
                } else {
                    newsList.addAll(netData);
                    newsAdapter.setDataSet(newsList);
                    if (curPage == 1) {
                    } else {
                        CustomToast.INSTANCE.showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                    if (AnnouncerNewsList.this.getClass().getName().equals(StudyManager.instance.getListFragmentPos())) {
                        StudyManager.instance.setSourceArticleList(newsList);
                    }
                    LocalInfo localinfo;
                    for (Article temp : netData) {
                        temp.setApp(ConstantManager.instance.getAppId());
                        localinfo = localInfoOp.findDataById(temp.getApp(), temp.getId());
                        if (localinfo.getId() == 0) {
                            localinfo.setApp(temp.getApp());
                            localinfo.setId(temp.getId());
                            localInfoOp.saveData(localinfo);
                        } else {

                        }
                    }
                    articleOp.saveData(netData);
                }
            }
        });
    }

    @Override
    public void onClick(View view, Object message) {
        newsRecycleView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mAdAdapter != null) {
            mAdAdapter.destroy();
        }
    }

    private void getDbData() {
        newsList.addAll(articleOp.findDataByAnnouncer(announcer.getId(), newsList.size(), 20));
        newsAdapter.setDataSet(newsList);
    }
}
