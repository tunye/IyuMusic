package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.activity.me.ReplyDoingActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.adapter.discover.CircleAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.discoverrequest.CircleRequest;
import com.iyuba.music.widget.CustomToast;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/4/21.
 */
public class CircleActivity extends BaseListActivity<Circle> {
    private CircleAdapter circleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circle);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        RecyclerView circleRecycleView = findViewById(R.id.circle_recyclerview);
        setRecyclerViewProperty(circleRecycleView);
        circleAdapter = new CircleAdapter(context);
        circleAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Circle circle = datas.get(position);
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
                        intent.putExtra("url", "http://static1.iyuba.cn/data/attachment/album/" + circle.getImage());
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
        onRefresh(0);
    }

    @Override
    protected void setListener() {
        super.setListener();
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

    @Override
    protected void getNetData() {
        CircleRequest.exeRequest(CircleRequest.generateUrl(AccountManager.getInstance().getUserId(), curPage), new IProtocolResponse<BaseListEntity<ArrayList<Circle>>>() {
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
            public void response(BaseListEntity<ArrayList<Circle>> listEntity) {
                swipeRefreshLayout.setRefreshing(false);
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    CustomToast.getInstance().showToast(R.string.circle_load_all);
                } else {
                    datas.addAll(listEntity.getData());
                    circleAdapter.setCircleList(datas);
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
}
