package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.adapter.study.CommentAdapter;
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
    private CommentAdapter readAdapter;
    private String dataType;

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        readAdapter = new CommentAdapter(context, false);
        readAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
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
        recyclerView.setAdapter(readAdapter);
        setUserVisibleHint(true);
        return view;
    }

    private void delReadItem(int position) {
        if (position == -1) {
            position = 0;
        }
        if (AccountManager.getInstance().getUserId()
                .equals(datas.get(position).getUserid())) {//是自己，删除
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
        readAdapter.onDestroy();
    }

    @Override
    protected void getNetData() {
        RequestClient.requestAsync(new ReadRequest(StudyManager.getInstance().getCurArticle().getId(),
                curPage, dataType), new SimpleRequestCallBack<BaseListEntity<List<Comment>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Comment>> listEntity) {
                swipeRefreshLayout.setRefreshing(false);
                isLastPage = listEntity.isLastPage();
                if (listEntity.getTotalCount() == 0) {
                    noData.setVisibility(View.VISIBLE);
                    ((TextView) noData.findViewById(R.id.no_data_content)).setText(R.string.no_read);
                } else {
                    datas.addAll(listEntity.getData());
                    noData.setVisibility(View.GONE);
                    readAdapter.setDataSet(datas);
                    if (listEntity.getCurPage() == 1) {

                    } else {
                        CustomToast.getInstance().showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), Toast.LENGTH_SHORT);
                    }
                }
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
            public void onClick(View view) {
                super.onClick(view);
                RequestClient.requestAsync(new CommentDeleteRequest(datas.get(position).getId()), new SimpleRequestCallBack<String>() {
                    @Override
                    public void onSuccess(String resultCode) {
                        if (resultCode.equals("1")) {
                            readAdapter.removeData(position);
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
            public void onClick(View view) {
                super.onClick(view);
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }
}
