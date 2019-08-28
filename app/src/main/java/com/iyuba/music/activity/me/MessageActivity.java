package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.me.MessageAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.MessageRequest;
import com.iyuba.music.request.merequest.SetMessageReadRequest;
import com.iyuba.music.widget.CustomToast;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/1/2.
 */
public class MessageActivity extends BaseListActivity<MessageLetter> {
    private MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        RecyclerView messageRecycleView = findViewById(R.id.message_recyclerview);
        setRecyclerViewProperty(messageRecycleView);
        ((SimpleItemAnimator) messageRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        messageAdapter = new MessageAdapter(context);
        messageAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MessageLetter messageLetter = datas.get(position);
                SocialManager.getInstance().pushFriendId(messageLetter.getFriendid());
                SocialManager.getInstance().pushFriendName(messageLetter.getFriendName());
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
                SetMessageReadRequest.exeRequest(SetMessageReadRequest.generateUrl(AccountManager.getInstance().getUserId(), messageLetter.getMessageid()));
                messageAdapter.setReaded(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        messageRecycleView.setAdapter(messageAdapter);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().pushFriendId(AccountManager.getInstance().getUserId());
                Intent intent = new Intent(context, FriendCenter.class);
                intent.putExtra("type", "0");
                intent.putExtra("intenttype", "chat");
                startActivity(intent);
            }
        });
    }

    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
        toolbarOper.setText(R.string.message_oper);
        title.setText(R.string.message_title);
    }

    protected void changeUIResumeByPara() {
        onRefresh(0);
    }

    @Override
    protected int getToastResource() {
        return R.string.comment_get_all;
    }

    @Override
    protected void getNetData() {
        MessageRequest.exeRequest(MessageRequest.generateUrl(AccountManager.getInstance().getUserId(), curPage), new IProtocolResponse<BaseListEntity<ArrayList<MessageLetter>>>() {
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
            public void response(BaseListEntity<ArrayList<MessageLetter>> listEntity) {
                isLastPage = listEntity.isLastPage();
                datas.addAll(listEntity.getData());
                swipeRefreshLayout.setRefreshing(false);
                messageAdapter.setMessageList(datas);
                if (curPage == 1) {
                    if (datas.size() == 0) {
                        findViewById(R.id.no_message).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.no_message).setVisibility(View.GONE);
                    }
                } else {
                    CustomToast.getInstance().showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                }
            }
        });
    }
}
