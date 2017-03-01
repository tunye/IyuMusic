package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.activity.me.ReplyDoingActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.adapter.discover.CircleAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.discoverrequest.CircleRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/4/21.
 */
public class CircleActivity extends BaseActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private RecyclerView circleRecycleView;
    private ArrayList<Circle> circles;
    private CircleAdapter circleAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int curPage;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
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
                        SocialManager.getInstance().pushDoing(doing);
                        Intent intent = new Intent(context, ReplyDoingActivity.class);
                        intent.putExtra(ReplyDoingActivity.VIP_FLG, circle.getVip() == 1);
                        startActivity(new Intent(context, ReplyDoingActivity.class));
                        break;
                    case "picid":
                        intent = new Intent(context, MeizhiPhotoActivity.class);
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

    @Override
    protected void setListener() {
        super.setListener();
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
                return true;
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.circle_send);
        title.setText(R.string.circle_title);
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
            CustomToast.getInstance().showToast(R.string.circle_load_all);
        }
    }

    private void getCircleData() {
        CircleRequest.exeRequest(CircleRequest.generateUrl(AccountManager.getInstance().getUserId(), curPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.getInstance().showToast(R.string.circle_load_all);
                } else {
                    circles.addAll((ArrayList<Circle>) listEntity.getData());
                    circleAdapter.setCircleList(circles);
                    if (curPage == 1) {

                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), 800);
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
}
