package com.iyuba.music.activity.study;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.comment.CommentView;
import com.buaa.ct.comment.ContextManager;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.study.CommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.CommentDeleteRequest;
import com.iyuba.music.request.newsrequest.CommentExpressRequest;
import com.iyuba.music.request.newsrequest.CommentRequest;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by 10202 on 2016/2/13.
 */
public class CommentActivity extends BaseListActivity<Comment> implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private RecyclerView commentRecycleView;
    private Article curArticle;
    private ImageView img;
    private TextView articleTitle, singer, announcer, count;
    private CommentAdapter commentAdapter;
    private CommentView commentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.comment);
        initWidget();
        setListener();
        changeUIByPara();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        img = findViewById(R.id.article_img);
        articleTitle = findViewById(R.id.article_title);
        announcer = findViewById(R.id.article_announcer);
        singer = findViewById(R.id.article_singer);
        count = findViewById(R.id.article_comment_count);
        commentRecycleView = findViewById(R.id.comment_recyclerview);
        setRecyclerViewProperty(commentRecycleView);
        ((SimpleItemAnimator) commentRecycleView.getItemAnimator()).setSupportsChangeAnimations(false);
        commentAdapter = new CommentAdapter(context, true);
        commentAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    if (AccountManager.getInstance().getUserId().equals(datas.get(position).getUserid())) {//是自己，删除
                        delDialog(position);
                    } else {//不是自己  回复
                        commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                datas.get(position).getUserName()));
                        commentView.getmEtText().setSelection(commentView.getmEtText().length());
                    }
                } else {
                    final int pos = position;
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            if (AccountManager.getInstance().getUserId().equals(datas.get(pos).getUserid())) {//是自己，删除
                                delDialog(pos);
                            } else {//不是自己  回复
                                commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                        datas.get(pos).getUserName()));
                                commentView.getmEtText().setSelection(commentView.getmEtText().length());
                            }
                        }
                    });
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        commentRecycleView.setAdapter(commentAdapter);
        commentView = findViewById(R.id.comment_view);
    }

    @Override
    protected void setListener() {
        super.setListener();
        commentView.setOperationDelegate(new CommentView.OnComposeOperationDelegate() {
            @Override
            public void onSendText(String s) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    sendComment(s);
                } else {
                    final String string = s;
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            sendComment(string);
                        }
                    });
                }
                commentView.hideEmojiOptAndKeyboard();
            }

            @Override
            public void onSendVoice(String s, int i) {
                if (i == 0) {
                    CustomToast.getInstance().showToast(R.string.comment_sound_short);
                } else {
                    if (AccountManager.getInstance().checkUserLogin()) {
                        handler.obtainMessage(1, s).sendToTarget();
                    } else {
                        final String string = s;
                        CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                            @Override
                            public void finish() {
                                handler.obtainMessage(1, string).sendToTarget();
                            }
                        });
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

    private void sendComment(String s) {
        CommentExpressRequest.exeRequest(CommentExpressRequest.generateUrl(
                String.valueOf(curArticle.getId()), AccountManager.getInstance().getUserId(),
                AccountManager.getInstance().getUserInfo().getUsername(), s), new IProtocolResponse<String>() {
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
                if (resultCode.equals("501")) {
                    commentView.clearText();
                    handler.sendEmptyMessage(2);
                } else {
                    CustomToast.getInstance().showToast(R.string.comment_send_fail);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.comment_title);
        curArticle = StudyManager.getInstance().getCurArticle();
        ImageUtil.loadImage("http://static.iyuba.cn/images/song/" + curArticle.getPicUrl(), img, R.drawable.default_music);
        articleTitle.setText(curArticle.getTitle());
        announcer.setText(context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
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

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(CommentActivity.this, new String[]{Manifest.permission.RECORD_AUDIO},
                            100);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
        if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(CommentActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            101);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }

    @Override
    protected int getToastResource() {
        return R.string.comment_get_all;
    }

    private void delDialog(final int position) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.comment_title);
        materialDialog.setMessage(R.string.comment_del_msg);
        materialDialog.setPositiveButton(R.string.comment_del, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDeleteRequest.exeRequest(CommentDeleteRequest.generateUrl(datas.get(position).getId()), new IProtocolResponse<String>() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(String resultCode) {
                        if (resultCode.equals("1")) {
                            commentAdapter.removeData(position);
                        } else {
                            CustomToast.getInstance().showToast(R.string.comment_del_fail);
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

    @Override
    protected void getNetData() {
        CommentRequest.exeRequest(CommentRequest.generateUrl(curArticle.getId(), curPage), new IProtocolResponse<BaseListEntity<ArrayList<Comment>>>() {
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
            public void response(BaseListEntity<ArrayList<Comment>> listEntity) {
                swipeRefreshLayout.setRefreshing(false);
                isLastPage = listEntity.isLastPage();
                if (listEntity.getTotalCount() == 0) {
                    findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
                } else {
                    datas.addAll(listEntity.getData());
                    findViewById(R.id.no_comment).setVisibility(View.GONE);
                    handler.obtainMessage(0, listEntity.getTotalCount()).sendToTarget();
                    if (listEntity.getCurPage() == 1) {

                    } else {
                        CustomToast.getInstance().showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), 800);
                    }
                }
            }
        });
    }

    private void startUploadVoice(final String url) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String sb = "http://daxue.iyuba.cn/appApi/UnicomApi?protocol=60003&platform=android&appName=music&format=json" + "&userid=" +
                        AccountManager.getInstance().getUserId() +
                        "&shuoshuotype=" + 1 +
                        "&voaid=" + curArticle.getId();
                final File file = new File(url);
                UploadFile.postSound(sb, file, new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        handler.sendEmptyMessage(2);
                        file.delete();
                    }

                    @Override
                    public void fail(Object object) {
                        handler.sendEmptyMessage(3);
                    }
                });
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<CommentActivity> {
        @Override
        public void handleMessageByRef(final CommentActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    activity.commentAdapter.setDataSet(activity.datas);
                    activity.count.setText(activity.getString(R.string.article_commentcount, msg.obj.toString()));
                    break;
                case 1:
                    activity.startUploadVoice(msg.obj.toString());
                    break;
                case 2:
                    activity.onRefresh(0);
                    activity.commentRecycleView.scrollToPosition(0);
                    break;
                case 3:
                    CustomToast.getInstance().showToast(R.string.comment_send_fail);
                    break;
            }
        }
    }
}
