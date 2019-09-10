package com.iyuba.music.activity.discover;

import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.activity.me.ReplyDoingActivity;
import com.iyuba.music.activity.me.WriteStateActivity;
import com.iyuba.music.adapter.discover.CircleAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.discoverrequest.CircleRequest;
import com.iyuba.music.util.Utils;

import java.util.List;

/**
 * Created by 10202 on 2016/4/21.
 */
public class CircleActivity extends BaseListActivity<Circle> {
    @Override
    public void initWidget() {
        super.initWidget();
        owner = findViewById(R.id.recyclerview_widget);
        ownerAdapter = new CircleAdapter(context);
        assembleRecyclerView();
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Circle circle = getData().get(position);
                switch (circle.getIdtype()) {
                    case "doid":
                        Doing doing = new Doing();
                        doing.setDoid(String.valueOf(circle.getId()));
                        doing.setReplynum(circle.getReplynum());
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
//                        intent = new Intent(context, MeizhiPhotoActivity.class);
//                        intent.putExtra("url", "http://static1.iyuba.cn/data/attachment/album/" + circle.getImage());
//                        context.startActivity(intent);
                        break;
                    case "blogid":
                        intent = new Intent();
                        intent.setClass(context, BlogActivity.class);
                        intent.putExtra("blogid", circle.getId());
                        startActivity(intent);
                        break;
                }
            }
        });
        onRefresh(0);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
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
    public void onActivityCreated() {
        super.onActivityCreated();
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
    public int getToastResource() {
        return R.string.circle_load_all;
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new CircleRequest(AccountManager.getInstance().getUserId(), curPage), new SimpleRequestCallBack<BaseListEntity<List<Circle>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Circle>> listEntity) {
                onNetDataReturnSuccess(listEntity.getData());
                if (!isLastPage) {
                    if (curPage == 1) {

                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + (listEntity.getTotalCount() / 20 + (listEntity.getTotalCount() % 20 == 0 ? 0 : 1)), Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                swipeRefreshLayout.setRefreshing(false);
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
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
