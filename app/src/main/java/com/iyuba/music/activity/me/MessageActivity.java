package com.iyuba.music.activity.me;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.adapter.me.MessageAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.MessageRequest;
import com.iyuba.music.request.merequest.SetMessageReadRequest;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/1/2.
 */
public class MessageActivity extends BaseSkinActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    protected Context context;
    protected MaterialRippleLayout back;
    protected MaterialMenu backIcon;
    protected TextView title, toolbarOper;
    private RecyclerView messageRecycleView;
    private ArrayList<MessageLetter> messageLetters;
    private MessageAdapter messageAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int messagePage;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(GetAppColor.instance.getAppColor(this));
            getWindow().setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        }
        ((MusicApplication) getApplication()).pushActivity(this);
        setContentView(R.layout.message);
        context = this;
        isLastPage = false;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        changeUIResumeByPara();
    }

    protected void initWidget() {
        back = (MaterialRippleLayout) findViewById(R.id.back);
        backIcon = (MaterialMenu) findViewById(R.id.back_material);
        title = (TextView) findViewById(R.id.toolbar_title);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        messageRecycleView = (RecyclerView) findViewById(R.id.message_recyclerview);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        messageRecycleView.setLayoutManager(new LinearLayoutManager(context));
        messageAdapter = new MessageAdapter(context);
        messageAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                MessageLetter messageLetter = messageLetters.get(position);
                SocialManager.instance.pushFriendId(messageLetter.getFriendid());
                SocialManager.instance.pushFriendName(messageLetter.getFriendName());
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
                SetMessageReadRequest.getInstance().exeRequest(SetMessageReadRequest.getInstance().
                        generateUrl(AccountManager.instance.getUserId(), messageLetter.getMessageid()));
                messageAdapter.setReaded(position);
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        messageRecycleView.setAdapter(messageAdapter);
        messageRecycleView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
    }

    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.instance.pushFriendId(AccountManager.instance.getUserId());
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

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        messagePage = 1;
        messageLetters = new ArrayList<>();
        isLastPage = false;
        getMessageData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (messageLetters.size() == 0) {

        } else if (!isLastPage) {
            messagePage++;
            getMessageData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.comment_get_all);
        }
    }

    private void getMessageData() {
        MessageRequest.getInstance().exeRequest(MessageRequest.getInstance().generateUrl(AccountManager.instance.getUserId(), messagePage), new IProtocolResponse() {
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
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                messageLetters.addAll((ArrayList<MessageLetter>) listEntity.getData());
                swipeRefreshLayout.setRefreshing(false);
                messageAdapter.setMessageList(messageLetters);
                if (messagePage == 1) {
                    if (messageLetters.size() == 0) {
                        findViewById(R.id.no_message).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.no_message).setVisibility(View.GONE);
                    }
                } else {
                    CustomToast.INSTANCE.showToast(messagePage + "/" + listEntity.getTotalPage(), 800);
                }
            }
        });
    }

    @Override
    public void onClick(View view, Object message) {
        messageRecycleView.scrollToPosition(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
    }
}
