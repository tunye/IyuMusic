package com.iyuba.music.activity.study;

import android.Manifest;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.buaa.ct.comment.CommentView;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.PermissionPool;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseListActivity;
import com.iyuba.music.adapter.study.CommentAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.CommentDeleteRequest;
import com.iyuba.music.request.newsrequest.CommentExpressRequest;
import com.iyuba.music.request.newsrequest.CommentRequest;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.io.File;
import java.util.List;

/**
 * Created by 10202 on 2016/2/13.
 */
public class CommentActivity extends BaseListActivity<Comment> {
    private Article curArticle;
    private ImageView img;
    private TextView articleTitle, singer, announcer, count;
    private CommentView commentView;

    @Override
    public int getLayoutId() {
        return R.layout.comment;
    }

    @Override
    public void afterSetLayout() {
        super.afterSetLayout();
        requestMultiPermission(new int[]{PermissionPool.WRITE_EXTERNAL_STORAGE, PermissionPool.RECORD_AUDIO}, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO});
    }

    @Override
    public void initWidget() {
        super.initWidget();
        img = findViewById(R.id.article_img);
        articleTitle = findViewById(R.id.article_title);
        announcer = findViewById(R.id.article_announcer);
        singer = findViewById(R.id.article_singer);
        count = findViewById(R.id.article_comment_count);
        owner = findViewById(R.id.comment_recyclerview);
        ((SimpleItemAnimator) owner.getItemAnimator()).setSupportsChangeAnimations(false);
        ownerAdapter = new CommentAdapter(context, true);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    if (AccountManager.getInstance().getUserId().equals(getData().get(position).getUserid())) {//是自己，删除
                        delDialog(position);
                    } else {//不是自己  回复
                        commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                getData().get(position).getUserName()));
                        commentView.getmEtText().setSelection(commentView.getmEtText().length());
                    }
                } else {
                    final int pos = position;
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            if (AccountManager.getInstance().getUserId().equals(getData().get(pos).getUserid())) {//是自己，删除
                                delDialog(pos);
                            } else {//不是自己  回复
                                commentView.getmEtText().setText(getResources().getString(R.string.comment_reply,
                                        getData().get(pos).getUserName()));
                                commentView.getmEtText().setSelection(commentView.getmEtText().length());
                            }
                        }
                    });
                }
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
                        startUploadVoice(s);
                    } else {
                        final String string = s;
                        CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                            @Override
                            public void finish() {
                                startUploadVoice(string);
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
        RequestClient.requestAsync(new CommentExpressRequest(String.valueOf(curArticle.getId()), AccountManager.getInstance().getUserId(),
                AccountManager.getInstance().getUserInfo().getUsername(), s), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> resultCode) {
                commentView.clearText();
                onRefresh(0);
                owner.scrollToPosition(0);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.comment_title);
        curArticle = StudyManager.getInstance().getCurArticle();
        AppImageUtil.loadImage("http://static.iyuba.cn/images/song/" + curArticle.getPicUrl(), img, R.drawable.default_music);
        articleTitle.setText(curArticle.getTitle());
        announcer.setText(context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
        singer.setText(context.getString(R.string.article_singer, curArticle.getSinger()));
        count.setText(context.getString(R.string.article_commentcount, 0));
        onRefresh(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((CommentAdapter) ownerAdapter).onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (commentView.onBackPressed()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onAccreditFailure(final int requestCode) {
        super.onAccreditFailure(requestCode);
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(R.string.storage_permission_content);
        materialDialog.setPositiveButton(R.string.app_sure, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (requestCode == PermissionPool.RECORD_AUDIO) {
                    permissionDispose(PermissionPool.RECORD_AUDIO, Manifest.permission.RECORD_AUDIO);
                } else {
                    permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                materialDialog.dismiss();
            }
        });
        materialDialog.show();

    }

    @Override
    public int getToastResource() {
        return R.string.comment_get_all;
    }

    private void delDialog(final int position) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.comment_title);
        materialDialog.setMessage(R.string.comment_del_msg);
        materialDialog.setPositiveButton(R.string.comment_del, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                RequestClient.requestAsync(new CommentDeleteRequest(getData().get(position).getId()), new SimpleRequestCallBack<BaseApiEntity<String>>() {
                    @Override
                    public void onSuccess(BaseApiEntity<String> s) {
                        ownerAdapter.removeData(position);
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {
                        CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                    }
                });
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new CommentRequest(curArticle.getId(), curPage), new SimpleRequestCallBack<BaseListEntity<List<Comment>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Comment>> listEntity) {
                swipeRefreshLayout.setRefreshing(false);
                isLastPage = listEntity.isLastPage();
                if (listEntity.getTotalCount() == 0) {
                    findViewById(R.id.no_comment).setVisibility(View.VISIBLE);
                } else {
                    ownerAdapter.addDatas(listEntity.getData());
                    findViewById(R.id.no_comment).setVisibility(View.GONE);
                    count.setText(getString(R.string.article_commentcount, listEntity.getTotalCount()));
                    ownerAdapter.addDatas(listEntity.getData());
                    if (listEntity.getCurPage() != 1) {
                        CustomToast.getInstance().showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), 800);
                    }
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

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
                        onRefresh(0);
                        owner.scrollToPosition(0);
                        file.delete();
                    }

                    @Override
                    public void fail(Object object) {
                        CustomToast.getInstance().showToast(R.string.comment_send_fail);
                    }
                });
            }
        });
    }
}
