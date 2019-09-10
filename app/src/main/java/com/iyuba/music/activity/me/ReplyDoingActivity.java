package com.iyuba.music.activity.me;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.ct.comment.CommentView;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.me.DoingCommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.DoingCommentRequest;
import com.iyuba.music.request.merequest.SendDoingCommentRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.imageview.VipPhoto;

import java.util.Date;
import java.util.List;

/**
 * Created by 10202 on 2016/2/13.
 */
public class ReplyDoingActivity extends BaseListActivity<DoingComment> {
    public static final String VIP_FLG = "vip_flg";
    private Doing doing;
    private VipPhoto doingPhoto;
    private TextView doingUserName, doingMessage, doingTime, doingReplyCounts;
    private CommentView commentView;
    private View noComment;
    private DoingComment selectComment;
    private boolean isVip;

    @Override
    public int getLayoutId() {
        return R.layout.reply_doings;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        isVip = getIntent().getBooleanExtra(VIP_FLG, false);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        noComment = findViewById(R.id.no_comment);
        doingPhoto = findViewById(R.id.doings_photo);
        doingUserName = findViewById(R.id.doings_username);
        doingMessage = findViewById(R.id.doings_message);
        doingTime = findViewById(R.id.doings_time);
        doingReplyCounts = findViewById(R.id.doings_reply_count);
        owner = findViewById(R.id.doings_reply_list);
        ownerAdapter = new DoingCommentAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectComment = getData().get(position);
                commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                        selectComment.getUsername()));
                commentView.getmEtText().setSelection(commentView.getmEtText().length());
            }
        });
        assembleRecyclerView();
        commentView = findViewById(R.id.comment_view);
    }

    @Override
    public void setListener() {
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
    public void onActivityCreated() {
        super.onActivityCreated();
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
    public void onDestroy() {
        super.onDestroy();
        SocialManager.getInstance().popDoing();
    }

    @Override
    public void onBackPressed() {
        if (commentView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public int getToastResource() {
        return R.string.comment_get_all;
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new DoingCommentRequest(doing.getDoid(), curPage), new SimpleRequestCallBack<BaseListEntity<List<DoingComment>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<DoingComment>> listEntity) {
                isLastPage = listEntity.isLastPage();
                swipeRefreshLayout.setRefreshing(false);
                if (isLastPage) {
                    if (curPage == 1) {
                        noComment.setVisibility(View.VISIBLE);
                    } else {
                        CustomToast.getInstance().showToast(R.string.person_doings_load_all);
                    }
                } else {
                    noComment.setVisibility(View.GONE);
                    ownerAdapter.addDatas(listEntity.getData());
                    if (curPage == 1) {

                    } else {
                        CustomToast.getInstance().showToast(curPage + "/" + (doing.getReplynum() / 20 + (doing.getReplynum() % 20 == 0 ? 0 : 1)), Toast.LENGTH_SHORT);
                    }
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
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
        RequestClient.requestAsync(new SendDoingCommentRequest(selectComment, doing, fromUid, fromMessage), new SimpleRequestCallBack<String>() {
            @Override
            public void onSuccess(String resultCode) {
                if (resultCode.equals("361")) {
                    ownerAdapter.addData(0, selectComment);
                    if (noComment.isShown()) {
                        noComment.setVisibility(View.GONE);
                    }
                    commentView.clearText();
                } else {
                    CustomToast.getInstance().showToast(R.string.message_send_fail);
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }
}
