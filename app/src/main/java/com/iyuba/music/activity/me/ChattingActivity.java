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
import com.buaa.ct.comment.ContextManager;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.me.ChattingAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetterContent;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.ChattingRequest;
import com.iyuba.music.request.merequest.SendMessageRequest;
import com.iyuba.music.widget.CustomToast;

import java.util.ArrayList;
import java.util.Collections;


public class ChattingActivity extends BaseActivity {
    private ChattingAdapter adapter;
    private ArrayList<MessageLetterContent> list;
    private ListView chatContent;
    private boolean needPop;
    private int chattingPage;
    private boolean isLastPage = false;
    private ClipboardManager clipboard;
    private CommentView chatView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.chatting);
        clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        needPop = getIntent().getBooleanExtra("needpop", false);
        isLastPage = false;
        chattingPage = 1;
        list = new ArrayList<>();
        initWidget();
        setListener();
        changeUIByPara();
        initMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
        chattingPage = 1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needPop) {
            SocialManager.getInstance().popFriendId();
            SocialManager.getInstance().popFriendName();
        }
        ContextManager.destory();
    }

    @Override
    public void onBackPressed() {
        if (chatView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        chatContent = findViewById(R.id.chatting_history);
        chatView = findViewById(R.id.chat_view);
        chatContent.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
    }

    @Override
    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SocialManager.getInstance().pushFriendId(SocialManager.getInstance().getFriendId());
                Intent intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                startActivity(intent);
            }
        });
        chatView.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(final String s) {
                SendMessageRequest.exeRequest(SendMessageRequest.generateUrl(
                        AccountManager.getInstance().getUserId(), SocialManager.getInstance().getFriendName(), s), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        CustomToast.getInstance().showToast(R.string.message_send_fail);
                    }

                    @Override
                    public void onServerError(String msg) {
                        CustomToast.getInstance().showToast(R.string.message_send_fail);
                    }

                    @Override
                    public void response(Object object) {
                        if (object.toString().equals("611")) {
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
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(SocialManager.getInstance().getFriendName());
        toolbarOper.setText(R.string.message_home);
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
        ChattingRequest.exeRequest(ChattingRequest.generateUrl(AccountManager.getInstance().getUserId(),
                SocialManager.getInstance().getFriendId(), chattingPage),
                new IProtocolResponse<BaseListEntity<ArrayList<MessageLetterContent>>>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(BaseListEntity<ArrayList<MessageLetterContent>> baseListEntity) {
                if (baseListEntity.isLastPage()) {
                    isLastPage = true;
                    CustomToast.getInstance().showToast(R.string.message_load_all);
                } else {
                    chattingPage++;
                    ArrayList<MessageLetterContent> contents = baseListEntity.getData();
                    Collections.reverse(contents);
                    list.addAll(0, contents);
                    adapter.setList(list);
                    final int pos = contents.size() - 1;
                    chatContent.postDelayed(new Runnable() {
                        public void run() {
                            chatContent.setSelection(pos);
                        }
                    }, 300);
                }
            }
        });
    }
}
