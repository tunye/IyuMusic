package com.iyuba.music.activity.discover;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.skin.BaseSkinActivity;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.activity.me.ReplyDoingActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.adapter.discover.CircleAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.discoverrequest.CircleRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.bitmap.MyPalette;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/4/21.
 */
public class CircleActivity extends BaseSkinActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener, AppBarLayout.OnOffsetChangedListener {
    private Context context;
    private RecyclerView circleRecycleView;
    private ArrayList<Circle> circles;
    private CircleAdapter circleAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int curPage;
    private boolean isLastPage = false;

    private CollapsingToolbarLayout collapsingToolbar;
    private MyPalette myPalette;
    private AppBarLayout appBarLayout;
    private TextView toolbarOper;
    private int toolbarOperColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(GetAppColor.instance.getAppColor(this));
            getWindow().setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        }
        setContentView(R.layout.circle);
        context = this;
        myPalette = new MyPalette();
        initToolBar();
        initWidget();
        setListener();
        changeUIByPara();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    private void initToolBar() {
        appBarLayout = (AppBarLayout) findViewById(R.id.apptoolbar);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        upArrow.setColorFilter(0xffededed, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationIcon(upArrow);
        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_layout);
        collapsingToolbar.setTitle(context.getString(R.string.circle_title));
        collapsingToolbar.setCollapsedTitleTextColor(0xffededed);
        collapsingToolbar.setContentScrimColor(GetAppColor.instance.getAppColor(context));
        ImageView toolbarImage = (ImageView) findViewById(R.id.toolbar_image);
        ImageUtil.loadImage(toolbarImage, "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=" + AccountManager.instance.getUserId(),
                0, new ImageUtil.OnDrawableLoadListener() {
                    @Override
                    public void onSuccess(GlideDrawable drawable) {
                        myPalette.getByDrawable(drawable, new IOperationFinish() {
                            @Override
                            public void finish() {
                                if (myPalette.getPalette().getVibrantSwatch() == null) {
                                    toolbarOperColor = 0xffededed;
                                } else {
                                    toolbarOperColor = myPalette.getPalette().getVibrantSwatch().getTitleTextColor();
                                }
                                collapsingToolbar.setExpandedTitleColor(toolbarOperColor);
                                toolbarOper.setTextColor(toolbarOperColor);
                            }
                        });
                    }

                    @Override
                    public void onFail(Exception e) {

                    }
                });
    }

    protected void initWidget() {
        toolbarOper = (TextView) findViewById(R.id.circle_send);
        circleRecycleView = (RecyclerView) findViewById(R.id.circle_recyclerview);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        circleRecycleView.setLayoutManager(new LinearLayoutManager(context));
        circleAdapter = new CircleAdapter(context);
        circleAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Circle circle = circles.get(position);
                switch (circle.getIdtype()) {
                    case "doid":
                        Doing doing = new Doing();
                        doing.setDoid(String.valueOf(circle.getId()));
                        doing.setReplynum(String.valueOf(circle.getReplynum()));
                        doing.setMessage(getContent(circle));
                        doing.setUid(circle.getUid());
                        doing.setDateline(String.valueOf(circle.getDateline()));
                        doing.setUsername(circle.getUsername());
                        SocialManager.instance.pushDoing(doing);
                        startActivity(new Intent(context, ReplyDoingActivity.class));
                        break;
                    case "picid":
                        Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                        intent.putExtra("url", "http://static1.iyuba.com/data/attachment/album/" + circle.getImage());
                        context.startActivity(intent);
                        break;
                    case "blogid":
                        intent = new Intent();
                        intent.setClass(context, BlogActivity.class);
                        intent.putExtra("blogid", circle.getId());
                        startActivity(intent);
                        break;
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        circleRecycleView.setAdapter(circleAdapter);
        circleRecycleView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
    }

    protected void setListener() {
        findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, SendPhotoActivity.class), 101);
            }
        });
        toolbarOper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivityForResult(new Intent(context, WriteStateActivity.class), 101);
                return false;
            }
        });
        circleRecycleView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
                // 当不滚动时
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    //获取最后一个完全显示的ItemPosition
                    int lastVisibleItem = manager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = manager.getItemCount();
                    // 判断是否滚动到底部，并且是向右滚动
                    if (manager.getChildCount() > 0 && lastVisibleItem == (totalItemCount - 1)) {
                        //加载更多功能的代码
                        onLoad(0);
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });

    }

    protected void changeUIByPara() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 101 && resultCode == 1) {//发布
            onRefresh(0);
        }
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        curPage = 1;
        circles = new ArrayList<>();
        isLastPage = false;
        getCircleData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (circles.size() == 0) {
        } else if (!isLastPage) {
            curPage++;
            getCircleData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.circle_load_all);
        }
    }

    private void getCircleData() {
        CircleRequest.getInstance().exeRequest(CircleRequest.getInstance().generateUrl(AccountManager.instance.getUserId(), curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.INSTANCE.showToast(R.string.circle_load_all);
                } else {
                    circles.addAll((ArrayList<Circle>) listEntity.getData());
                    circleAdapter.setCircleList(circles);
                    if (curPage == 1) {

                    } else {
                        CustomToast.INSTANCE.showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
                    }
                }
            }
        });
    }

    private String getContent(Circle circle) {
        String title = circle.getTitle();
        String username = circle.getUsername();
        title = title.replace(username, "");
        return title.substring(1, title.length());
    }

    @Override
    public void onClick(View view, Object message) {
        circleRecycleView.scrollToPosition(0);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        appBarLayout.removeOnOffsetChangedListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            swipeRefreshLayout.setEnabled(true);
            toolbarOper.setTextColor(toolbarOperColor);
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            toolbarOper.setTextColor(0xffededed);
        } else {
            swipeRefreshLayout.setEnabled(false);
        }
    }
}
