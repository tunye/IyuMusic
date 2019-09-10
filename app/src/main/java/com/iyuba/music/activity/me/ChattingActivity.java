package com.iyuba.music.activity.me;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.buaa.ct.comment.CommentView;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.me.ChattingAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetterContent;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.ChattingRequest;
import com.iyuba.music.request.merequest.SendMessageRequest;
import com.iyuba.music.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ChattingActivity extends BaseActivity {
    private ChattingAdapter adapter;
    private List<MessageLetterContent> list;
    private ListView chatContent;
    private boolean needPop;
    private int chattingPage;
    private boolean isLastPage = false;
    private ClipboardManager clipboard;
    private CommentView chatView;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        needPop = getIntent().getBooleanExtra("needpop", false);
        chattingPage = 1;
        list = new ArrayList<>();
    }

    @Override
    public int getLayoutId() {
        return R.layout.chatting;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needPop) {
            SocialManager.getInstance().popFriendId();
            SocialManager.getInstance().popFriendName();
        }
    }

    @Override
    public void onBackPressed() {
        if (chatView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void initWidget() {
        super.initWidget();
        chatContent = findViewById(R.id.chatting_history);
        chatView = findViewById(R.id.chat_view);
        chatContent.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                SocialManager.getInstance().pushFriendId(SocialManager.getInstance().getFriendId());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
            }
        });
        chatView.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(final String s) {
                RequestClient.requestAsync(new SendMessageRequest(AccountManager.getInstance().getUserId(), SocialManager.getInstance().getFriendName(), s), new SimpleRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        if (result.equals("611")) {
                            chatView.clearText();
                            MessageLetterContent letterContent = new MessageLetterContent();
                            letterContent.setContent(s);
                            letterContent.setDirection(1);
                            letterContent.setAuthorid(AccountManager.getInstance().getUserId());
                            letterContent.setDate(String.valueOf(System.currentTimeMillis() / 1000));
                            list.add(letterContent);
                            adapter.setList(list);
                        } else {
                            CustomToast.getInstance().showToast(R.string.message_send_fail);
                        }
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {
                        CustomToast.getInstance().showToast(R.string.message_send_fail);
                    }
                });
            }

            @Override
            public void onSendVoice(String s, int i) {

            }

            @Override
            public void onSendImageClicked(View view) {

            }

            @Override
            public void onSendLocationClicked(View view) {

            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(SocialManager.getInstance().getFriendName());
        toolbarOper.setText(R.string.message_home);
        initMessages();
    }

    // 设置adapter
    private void setAdapterForThis() {
        adapter = new ChattingAdapter(this, AccountManager.getInstance().getUserId());
        chatContent.setAdapter(adapter);
        chatContent.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                ClipData clip = ClipData.newPlainText("chat message",
                        adapter.getItem(arg2).getContent());
                clipboard.setPrimaryClip(clip);
                CustomToast.getInstance().showToast(R.string.message_clip_board);
            }
        });
        chatContent.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

                switch (scrollState) {
                    case OnScrollListener.SCROLL_STATE_IDLE: // 当不滚动时
                        // 判断滚动到顶部
                        if (view.getFirstVisiblePosition() == 0) {
                            if (!isLastPage) {
                                getChattingContent();
                            } else {
                                CustomToast.getInstance().showToast(R.string.message_load_all);
                            }
                        }
                        break;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });
    }

    // 为listView添加数据
    private void initMessages() {
        setAdapterForThis();
        getChattingContent();
    }

    private void getChattingContent() {
        RequestClient.requestAsync(new ChattingRequest(AccountManager.getInstance().getUserId(),
                SocialManager.getInstance().getFriendId(), chattingPage), new SimpleRequestCallBack<BaseListEntity<List<MessageLetterContent>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<MessageLetterContent>> baseListEntity) {
                if (baseListEntity.isLastPage()) {
                    isLastPage = true;
                    CustomToast.getInstance().showToast(R.string.message_load_all);
                } else {
                    chattingPage++;
                    List<MessageLetterContent> contents = baseListEntity.getData();
                    Collections.reverse(contents);
                    list.addAll(0, contents);
                    adapter.setList(list);
                    final int pos = contents.size() - 1;
                    chatContent.postDelayed(new Runnable() {
                        public void run() {
                            chatContent.setSelection(pos);
                        }
                    }, 200);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }
}
