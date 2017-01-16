package com.iyuba.music.activity.study;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaowen.commentlibrary.CommentView;
import com.chaowen.commentlibrary.ContextManager;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseInputActivity;
import com.iyuba.music.adapter.study.CommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.CommentDeleteRequest;
import com.iyuba.music.request.newsrequest.CommentExpressRequest;
import com.iyuba.music.request.newsrequest.CommentRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.io.File;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2016/2/13.
 */
public class CommentActivity extends BaseInputActivity implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private Article curArticle;
    private ImageView img;
    private TextView articleTitle, singer, announecr, count;
    private RecyclerView commentRecycleView;
    private ArrayList<Comment> comments;
    private CommentAdapter commentAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private CommentView commentView;
    private int commentPage;
    private boolean isLastPage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.comment);
        context = this;
        isLastPage = false;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        img = (ImageView) findViewById(R.id.article_img);
        articleTitle = (TextView) findViewById(R.id.article_title);
        announecr = (TextView) findViewById(R.id.article_announcer);
        singer = (TextView) findViewById(R.id.article_singer);
        count = (TextView) findViewById(R.id.article_comment_count);
        commentRecycleView = (RecyclerView) findViewById(R.id.comment_recyclerview);
        swipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        commentRecycleView.setLayoutManager(new LinearLayoutManager(context));
        commentAdapter = new CommentAdapter(context);
        commentAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (AccountManager.instance.checkUserLogin()) {
                    if (AccountManager.instance.getUserId()
                            .equals(comments.get(position).getUserid())) {//是自己，删除
                        delDialog(position);
                    } else {//不是自己  回复
                        commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                comments.get(position).getUserName()));
                        commentView.getmEtText().setSelection(commentView.getmEtText().length());
                    }
                } else {
                    CustomDialog.showLoginDialog(context);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        commentRecycleView.setAdapter(commentAdapter);
        commentRecycleView.addItemDecoration(new DividerItemDecoration());
        swipeRefreshLayout.setRefreshing(true);
        commentView = (CommentView) findViewById(R.id.comment_view);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolBarLayout.setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        commentView.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(String s) {
                if (AccountManager.instance.checkUserLogin()) {
                    CommentExpressRequest.exeRequest(CommentExpressRequest.generateUrl(
                            String.valueOf(curArticle.getId()), AccountManager.instance.getUserId(),
                            AccountManager.instance.getUserName(), s), new IProtocolResponse() {
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
                            if (object.toString().equals("501")) {
                                commentView.clearText();
                                handler.sendEmptyMessage(2);
                            } else {
                                CustomToast.INSTANCE.showToast(R.string.comment_send_fail);
                            }
                        }
                    });
                } else {
                    CustomDialog.showLoginDialog(context);
                }
            }

            @Override
            public void onSendVoice(String s, int i) {
                if (i == 0) {
                    CustomToast.INSTANCE.showToast(R.string.comment_sound_short);
                } else {
                    if (AccountManager.instance.checkUserLogin()) {
                        handler.obtainMessage(1, s).sendToTarget();
                    } else {
                        CustomDialog.showLoginDialog(context);
                    }
                }
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
        title.setText(R.string.comment_title);
        curArticle = StudyManager.instance.getCurArticle();
        ImageUtil.loadImage("http://static.iyuba.com/images/song/" + curArticle.getPicUrl(), img, R.drawable.default_music);
        articleTitle.setText(curArticle.getTitle());
        announecr.setText(context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
        singer.setText(context.getString(R.string.article_singer, curArticle.getSinger()));
        count.setText(context.getString(R.string.article_commentcount, "0"));
        onRefresh(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        commentAdapter.onDestroy();
        ContextManager.destory();
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

    @Override
    public void onClick(View view, Object message) {
        commentRecycleView.scrollToPosition(0);
    }

    private void delDialog(final int position) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(R.string.comment_title);
        materialDialog.setMessage(R.string.comment_del_msg);
        materialDialog.setPositiveButton(R.string.comment_del, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDeleteRequest.exeRequest(CommentDeleteRequest.generateUrl(comments.get(position).getId()), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {
                        if (object.toString().equals("1")) {
                            commentAdapter.removeData(position);
                        } else {
                            CustomToast.INSTANCE.showToast(R.string.comment_del_fail);
                        }
                    }
                });
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void getCommentData() {
        CommentRequest.exeRequest(CommentRequest.generateUrl(curArticle.getId(), commentPage), new IProtocolResponse() {
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
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (listEntity.getTotalCount() == 0) {
                    findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
                } else {
                    comments.addAll((ArrayList<Comment>) listEntity.getData());
                    findViewById(R.id.no_comment).setVisibility(View.GONE);
                    handler.obtainMessage(0, listEntity.getTotalCount()).sendToTarget();
                    if (listEntity.getCurPage() == 1) {

                    } else {
                        CustomToast.INSTANCE.showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), 800);
                    }
                }
            }
        });
    }

    private void startUploadVoice(String url) {
        new UploadVoice(url).start();
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<CommentActivity> {
        @Override
        public void handleMessageByRef(final CommentActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.commentAdapter.setDataSet(activity.comments);
                    activity.count.setText(activity.getString(R.string.article_commentcount, msg.obj.toString()));
                    break;
                case 1:
                    activity.startUploadVoice(msg.obj.toString());
                    break;
                case 2:
                    activity.onRefresh(0);
                    activity.commentRecycleView.scrollToPosition(0);
                    break;
            }
        }
    }

    public class UploadVoice extends Thread {
        String filePath;

        public UploadVoice(String path) {
            filePath = path;
        }

        @Override
        public void run() {

            super.run();
            StringBuilder sb = new StringBuilder(
                    "http://daxue.iyuba.com/appApi/UnicomApi?protocol=60003&platform=android&appName=music&format=json");
            sb.append("&userid=").append(
                    AccountManager.instance.getUserId());
            sb.append("&shuoshuotype=").append(1);
            sb.append("&voaid=").append(curArticle.getId());
            final File file = new File(filePath);
            UploadFile.postSound(sb.toString(), file, new IOperationResult() {
                @Override
                public void success(Object object) {
                    handler.sendEmptyMessage(2);
                    file.delete();
                }

                @Override
                public void fail(Object object) {
                    CustomToast.INSTANCE.showToast(R.string.comment_send_fail);
                }
            });
        }
    }
}
