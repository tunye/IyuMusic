package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.adapter.study.CommentAdapter;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.fragment.BaseRecyclerViewFragment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.CommentDeleteRequest;
import com.iyuba.music.request.newsrequest.ReadRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.util.List;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadNewFragment extends BaseRecyclerViewFragment<Comment> {
    private String dataType;

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        ownerAdapter = new CommentAdapter(context, false);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    delReadItem(position);
                } else {
                    final int pos = position;
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            delReadItem(pos);
                        }
                    });
                }
            }
        });
        assembleRecyclerView();
        listRequestAllState.setEmptyShowContent(R.string.no_read);
        setUserVisibleHint(true);
        return view;
    }

    private void delReadItem(int position) {
        if (position == -1) {
            position = 0;
        }
        if (AccountManager.getInstance().getUserId().equals(getData().get(position).getUserid())) {//是自己，删除
            delDialog(position);
        }
    }

    @Override
    public int getToastResource() {
        return R.string.read_get_all;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((CommentAdapter) ownerAdapter).onDestroy();
    }

    @Override
    public void getNetData() {
        RequestClient.requestAsync(new ReadRequest(StudyManager.getInstance().getCurArticle().getId(),
                curPage, dataType), new SimpleRequestCallBack<BaseListEntity<List<Comment>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Comment>> listEntity) {
                isLastPage = listEntity.isLastPage();
                if (!isLastPage && listEntity.getCurPage() != 1) {
                    CustomToast.getInstance().showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), Toast.LENGTH_SHORT);
                }
                onNetDataReturnSuccess(listEntity.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void delDialog(final int position) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.read_title);
        materialDialog.setMessage(R.string.read_del_msg);
        materialDialog.setPositiveButton(R.string.comment_del, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                RequestClient.requestAsync(new CommentDeleteRequest(getData().get(position).getId()), new SimpleRequestCallBack<BaseApiEntity<String>>() {
                    @Override
                    public void onSuccess(BaseApiEntity<String> resultCode) {
                        if (BaseApiEntity.isSuccess(resultCode)) {
                            ownerAdapter.removeData(position);
                        } else {
                            CustomToast.getInstance().showToast(R.string.read_del_fail);
                        }
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
}
