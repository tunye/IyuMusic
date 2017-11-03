package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadTopFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private CommentAdapter readAdapter;
    private ArrayList<Comment> readList;
    private int readPage;
    private boolean isLastPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        readAdapter = new CommentAdapter(context, false);
        readAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    if (AccountManager.getInstance().getUserId()
                            .equals(readList.get(position).getUserid())) {//是自己，删除
                        delDialog(position);
                    }
                } else {
                    final int pos = position;
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            if (AccountManager.getInstance().getUserId()
                                    .equals(readList.get(pos).getUserid())) {//是自己，删除
                                delDialog(pos);
                            }
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
    }

    /**
     * 下拉刷新
     *
     * @param index 当前分页索引
     */
    @Override
    public void onRefresh(int index) {
        readPage = 1;
        readList = new ArrayList<>();
        isLastPage = false;
        getReadData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (readList.size() == 0) {

        } else if (!isLastPage) {
            readPage++;
            getReadData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.read_get_all);
        }
    }

    @Override
    public void onClick(View view, Object message) {
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        readAdapter.onDestroy();
    }

    private void getReadData() {
        ReadRequest.exeRequest(ReadRequest.generateUrl(StudyManager.getInstance().getCurArticle().getId(), readPage, "agree"), new IProtocolResponse() {
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
            public void response(Object object) {
                swipeRefreshLayout.setRefreshing(false);
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (listEntity.getTotalCount() == 0) {
                    noData.setVisibility(View.VISIBLE);
                    ((TextView) noData.findViewById(R.id.no_data_content)).setText(R.string.no_read);
                } else {
                    readList.addAll((ArrayList<Comment>) listEntity.getData());
                    noData.setVisibility(View.GONE);
                    readAdapter.setDataSet(readList);
                    if (listEntity.getCurPage() != 1) {
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
                CommentDeleteRequest.exeRequest(CommentDeleteRequest.generateUrl(readList.get(position).getId()), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {

                    }

                    @Override
                    public void onServerError(String msg) {

                    }

                    @Override
                    public void response(Object object) {
                        if (object.toString().equals("1")) {
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
