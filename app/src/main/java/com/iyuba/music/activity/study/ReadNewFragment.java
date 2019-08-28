package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.adapter.study.CommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.fragment.BaseRecyclerViewFragment;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.CommentDeleteRequest;
import com.iyuba.music.request.newsrequest.ReadRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadNewFragment extends BaseRecyclerViewFragment<Comment> {
    private CommentAdapter readAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        readAdapter = new CommentAdapter(context, false);
        readAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
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

            @Override
            public void onItemLongClick(View view, int position) {

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
    protected int getToastResource() {
        return R.string.read_get_all;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        readAdapter.onDestroy();
    }

    @Override
    protected void getNetData() {
        ReadRequest.exeRequest(ReadRequest.generateUrl(StudyManager.getInstance().getCurArticle().getId(),
                curPage, "no"), new IProtocolResponse<BaseListEntity<ArrayList<Comment>>>() {
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
                    noData.setVisibility(View.VISIBLE);
                    ((TextView) noData.findViewById(R.id.no_data_content)).setText(R.string.no_read);
                } else {
                    datas.addAll(listEntity.getData());
                    noData.setVisibility(View.GONE);
                    readAdapter.setDataSet(datas);
                    if (listEntity.getCurPage() == 1) {

                    } else {
                        CustomToast.getInstance().showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), 800);
                    }
                }
            }
        });
    }

    private void delDialog(final int position) {
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.read_title);
        materialDialog.setMessage(R.string.read_del_msg);
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
                            readAdapter.removeData(position);
                        } else {
                            CustomToast.getInstance().showToast(R.string.read_del_fail);
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
}
