package com.iyuba.music.activity.me;

import android.content.Intent;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Toast;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.me.MessageAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.MessageRequest;
import com.iyuba.music.request.merequest.SetMessageReadRequest;
import com.iyuba.music.util.Utils;

import java.util.List;

/**
 * Created by 10202 on 2016/1/2.
 */
public class MessageActivity extends BaseListActivity<MessageLetter> {
    private MessageAdapter messageAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.message;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        owner = findViewById(R.id.message_recyclerview);
        ((SimpleItemAnimator) owner.getItemAnimator()).setSupportsChangeAnimations(false);
        ownerAdapter = new MessageAdapter(context);
        messageAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MessageLetter messageLetter = getData().get(position);
                SocialManager.getInstance().pushFriendId(messageLetter.getFriendid());
                SocialManager.getInstance().pushFriendName(messageLetter.getFriendName());
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
                RequestClient.requestAsync(new SetMessageReadRequest(AccountManager.getInstance().getUserId(), messageLetter.getMessageid()), new SimpleRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String s) {

                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {

                    }
                });
                messageAdapter.setReaded(position);
            }
        });
        assembleRecyclerView();
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                Intent intent = new Intent(context, FriendCenter.class);
                intent.putExtra("type", "0");
                intent.putExtra("intenttype", "chat");
                startActivity(intent);
            }
        });
    }

    public void onActivityCreated() {
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
        toolbarOper.setText(R.string.message_oper);
        title.setText(R.string.message_title);
    }

    @Override
    public void onActivityResumed() {
        onRefresh(0);
    }

    @Override
    public int getToastResource() {
        return R.string.comment_get_all;
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new MessageRequest(AccountManager.getInstance().getUserId(), curPage), new SimpleRequestCallBack<BaseListEntity<List<MessageLetter>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<MessageLetter>> listEntity) {
                isLastPage = listEntity.isLastPage();
                if (curPage == 1) {
                    if (listEntity.getData().size() == 0) {
                        findViewById(R.id.no_message).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.no_message).setVisibility(View.GONE);
                    }
                } else {
                    CustomToast.getInstance().showToast(curPage + "/" + listEntity.getTotalPage(), Toast.LENGTH_SHORT);
                }
                onNetDataReturnSuccess(listEntity.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
