package com.iyuba.music.activity.me;

import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.skin.BaseSkinActivity;
import com.chaowen.commentlibrary.CommentView;
import com.chaowen.commentlibrary.ContextManager;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.adapter.me.DoingCommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.DoingCommentRequest;
import com.iyuba.music.request.merequest.SendDoingCommentRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2016/2/13.
 */
public class ReplyDoingActivity extends BaseSkinActivity implements MySwipeRefreshLayout.OnRefreshListener {
    protected Context context;
    protected MaterialRippleLayout back;
    protected MaterialMenu backIcon;
    protected RelativeLayout toolBarLayout;
    protected TextView title;

    private Doing doing;
    private CircleImageView doingPhoto;
    private TextView doingUserName, doingMessage, doingTime, doingReplyCounts;
    private ArrayList<DoingComment> comments;
    private DoingCommentAdapter commentAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private CommentView commentView;
    private View noComment;
    private int commentPage;
    private boolean isLastPage = false;
    private DoingComment selectComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        ContextManager.setInstance(this);//评论模块初始化
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(GetAppColor.instance.getAppColor(this));
            getWindow().setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        }
        setContentView(R.layout.reply_doings);
        context = this;
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        isLastPage = false;
        initWidget();
        setListener();
        changeUIByPara();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    protected void initWidget() {
        back = (MaterialRippleLayout) findViewById(R.id.back);
        backIcon = (MaterialMenu) findViewById(R.id.back_material);
        title = (TextView) findViewById(R.id.toolbar_title);
        toolBarLayout = (RelativeLayout) findViewById(R.id.toolbar_title_layout);

        noComment = findViewById(R.id.no_comment);
        doingPhoto = (CircleImageView) findViewById(R.id.doings_photo);
        doingUserName = (TextView) findViewById(R.id.doings_username);
        doingMessage = (TextView) findViewById(R.id.doings_message);
        doingTime = (TextView) findViewById(R.id.doings_time);
        doingReplyCounts = (TextView) findViewById(R.id.doings_reply_count);
        RecyclerView doingRecycleView = (RecyclerView) findViewById(R.id.doings_reply_list);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        doingRecycleView.setLayoutManager(new LinearLayoutManager(context));
        commentAdapter = new DoingCommentAdapter(context);
        commentAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectComment = comments.get(position);
                commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                        selectComment.getUsername()));
                commentView.getmEtText().setSelection(commentView.getmEtText().length());
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        doingRecycleView.setAdapter(commentAdapter);
        doingRecycleView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
        commentView = (CommentView) findViewById(R.id.comment_view);
    }

    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        commentView.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(String s) {
                if (s.startsWith(getResources().getString(R.string.comment_reply, selectComment.getUsername()))) {
                } else {
                    selectComment = new DoingComment();
                }
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

    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
        title.setText(R.string.doing_title);
        doing = SocialManager.instance.getDoing();
        ImageUtil.loadAvatar(doing.getUid(), doingPhoto);
        doingUserName.setText(doing.getUsername());
        doingMessage.setText(doing.getMessage());
        doingReplyCounts.setText(context.getString(R.string.article_commentcount, doing.getReplynum()));
        doingTime.setText(DateFormat.showTime(context, new Date(Long.parseLong(doing.getDateline()) * 1000)));
        selectComment = new DoingComment();
        onRefresh(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocialManager.instance.popDoing();
        ContextManager.destory();
        ((MusicApplication) getApplication()).popActivity(this);
    }

    @Override
    public void onBackPressed() {
        if (commentView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        commentPage = 1;
        comments = new ArrayList<>();
        isLastPage = false;
        getCommentData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (comments.size() == 0) {

        } else if (!isLastPage) {
            commentPage++;
            getCommentData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.comment_get_all);
        }
    }


    private void getCommentData() {
        DoingCommentRequest.getInstance().exeRequest(DoingCommentRequest.getInstance().generateUrl(doing.getDoid(), commentPage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    if (commentPage == 1) {
                        noComment.setVisibility(View.VISIBLE);
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.person_doings_load_all);
                    }
                } else {
                    noComment.setVisibility(View.GONE);
                    comments.addAll((ArrayList<DoingComment>) listEntity.getData());
                    commentAdapter.setDoingList(comments);
                    if (commentPage == 1) {

                    } else {
                        CustomToast.INSTANCE.showToast(commentPage + "/" + (Integer.parseInt(doing.getReplynum()) / 20 + (Integer.parseInt(doing.getReplynum()) % 20 == 0 ? 0 : 1)), 800);
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
        selectComment.setUid(AccountManager.instance.getUserId());
        selectComment.setUsername(AccountManager.instance.getUserName());
        if (TextUtils.isEmpty(selectComment.getId())) {
            selectComment.setUpid("0");
        } else {
            selectComment.setUpid(selectComment.getId());
        }
        if (TextUtils.isEmpty(selectComment.getGrade())) {
            selectComment.setGrade("1");
        }
        SendDoingCommentRequest.getInstance().exeRequest(SendDoingCommentRequest.getInstance().generateUrl(selectComment, doing, fromUid, fromMessage), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void response(Object object) {
                if (object.toString().equals("361")) {
                    commentAdapter.addData(0, selectComment);
                    if (noComment.isShown()) {
                        noComment.setVisibility(View.GONE);
                    }
                    commentView.clearText();
                } else {
                    CustomToast.INSTANCE.showToast(R.string.message_send_fail);
                }
            }
        });
    }
}
