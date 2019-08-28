package com.iyuba.music.activity.me;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.buaa.ct.comment.CommentView;
import com.buaa.ct.comment.ContextManager;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.me.DoingCommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.DoingCommentRequest;
import com.iyuba.music.request.merequest.SendDoingCommentRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.Mathematics;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.imageview.VipPhoto;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by 10202 on 2016/2/13.
 */
public class ReplyDoingActivity extends BaseListActivity<DoingComment> {
    public static final String VIP_FLG = "vip_flg";
    private Doing doing;
    private VipPhoto doingPhoto;
    private TextView doingUserName, doingMessage, doingTime, doingReplyCounts;
    private DoingCommentAdapter commentAdapter;
    private CommentView commentView;
    private View noComment;
    private DoingComment selectComment;
    private boolean isVip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.reply_doings);
        isVip = getIntent().getBooleanExtra(VIP_FLG, false);
        initWidget();
        setListener();
        changeUIByPara();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        noComment = findViewById(R.id.no_comment);
        doingPhoto = findViewById(R.id.doings_photo);
        doingUserName = findViewById(R.id.doings_username);
        doingMessage = findViewById(R.id.doings_message);
        doingTime = findViewById(R.id.doings_time);
        doingReplyCounts = findViewById(R.id.doings_reply_count);
        RecyclerView doingRecycleView = findViewById(R.id.doings_reply_list);
        setRecyclerViewProperty(doingRecycleView);
        commentAdapter = new DoingCommentAdapter(context);
        commentAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectComment = datas.get(position);
                commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                        selectComment.getUsername()));
                commentView.getmEtText().setSelection(commentView.getmEtText().length());
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        doingRecycleView.setAdapter(commentAdapter);
        commentView = findViewById(R.id.comment_view);
    }

    @Override
    protected void setListener() {
        super.setListener();
        commentView.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(String s) {
                if (s.startsWith(getResources().getString(R.string.comment_reply, selectComment.getUsername()))) {
                } else {
                    selectComment = new DoingComment();
                }
                commentView.hideEmojiOptAndKeyboard();
                sendDoingComment(s);
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
        title.setText(R.string.doing_title);
        doing = SocialManager.getInstance().getDoing();
        doingPhoto.setVipStateVisible(doing.getUid(), isVip);
        doingUserName.setText(doing.getUsername());
        doingMessage.setText(doing.getMessage());
        doingReplyCounts.setText(context.getString(R.string.article_commentcount, doing.getReplynum()));
        doingTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(doing.getDateline()) * 1000)));
        selectComment = new DoingComment();
        onRefresh(0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
                Rect outRect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                Mathematics.setMargins(toolBarLayout, 0, RuntimeManager.getWindowHeight() - outRect.height(), 0, 0);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocialManager.getInstance().popDoing();
        ContextManager.destory();
    }

    @Override
    public void onBackPressed() {
        if (commentView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    protected int getToastResource() {
        return R.string.comment_get_all;
    }

    @Override
    protected void getNetData() {
        DoingCommentRequest.exeRequest(DoingCommentRequest.generateUrl(doing.getDoid(), curPage), new IProtocolResponse<BaseListEntity<ArrayList<DoingComment>>>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(BaseListEntity<ArrayList<DoingComment>> listEntity) {
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    if (curPage == 1) {
                        noComment.setVisibility(View.VISIBLE);
                    } else {
                        CustomToast.getInstance().showToast(R.string.person_doings_load_all);
                    }
                } else {
                    noComment.setVisibility(View.GONE);
                    datas.addAll(listEntity.getData());
                    commentAdapter.setDoingList(datas);
                    if (curPage == 1) {

                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + (Integer.parseInt(doing.getReplynum()) / 20 + (Integer.parseInt(doing.getReplynum()) % 20 == 0 ? 0 : 1)), 800);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void sendDoingComment(final String s) {
        String fromUid = selectComment.getUid();
        String fromMessage = selectComment.getMessage();
        selectComment.setDateline(String.valueOf(System.currentTimeMillis() / 1000));
        selectComment.setMessage(s);
        selectComment.setUid(AccountManager.getInstance().getUserId());
        selectComment.setUsername(AccountManager.getInstance().getUserInfo().getUsername());
        if (TextUtils.isEmpty(selectComment.getId())) {
            selectComment.setUpid("0");
        } else {
            selectComment.setUpid(selectComment.getId());
        }
        if (TextUtils.isEmpty(selectComment.getGrade())) {
            selectComment.setGrade("1");
        }
        SendDoingCommentRequest.exeRequest(SendDoingCommentRequest.generateUrl(selectComment, doing, fromUid, fromMessage), new IProtocolResponse<String>() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(String resultCode) {
                if (resultCode.equals("361")) {
                    commentAdapter.addData(0, selectComment);
                    if (noComment.isShown()) {
                        noComment.setVisibility(View.GONE);
                    }
                    commentView.clearText();
                } else {
                    CustomToast.getInstance().showToast(R.string.message_send_fail);
                }
            }
        });
    }
}
