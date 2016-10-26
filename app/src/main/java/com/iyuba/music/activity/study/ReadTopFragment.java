package com.iyuba.music.activity.study;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.adapter.study.CommentAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.CommentDeleteRequest;
import com.iyuba.music.request.newsrequest.ReadRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.CustomSwipeToRefresh;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadTopFragment extends BaseFragment implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private RecyclerView recyclerView;
    private CustomSwipeToRefresh swipeRefreshLayout;
    private CommentAdapter readAdapter;
    private ArrayList<Comment> readList;
    private Context context;
    private int readPage;
    private boolean isLastPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.recycleview_read, null);
        getActivity().findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        swipeRefreshLayout = (CustomSwipeToRefresh) view.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.listview);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        readAdapter = new CommentAdapter(context);
        readAdapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (AccountManager.instance.checkUserLogin()) {
                    if (AccountManager.instance.getUserId()
                            .equals(readList.get(position).getUserid())) {//是自己，删除
                        delDialog(position);
                    }
                } else {
                    CustomDialog.showLoginDialog(context);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        recyclerView.setAdapter(readAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        swipeRefreshLayout.setRefreshing(true);
        onRefresh(0);
        return view;
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
            CustomToast.INSTANCE.showToast(R.string.read_get_all);
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
        ReadRequest.getInstance().exeRequest(ReadRequest.getInstance().generateUrl(
                StudyManager.instance.getCurArticle().getId(), readPage, "agree"), new IProtocolResponse() {
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
                    getActivity().findViewById(R.id.no_read).setVisibility(View.VISIBLE);
                } else {
                    readList.addAll((ArrayList<Comment>) listEntity.getData());
                    getActivity().findViewById(R.id.no_read).setVisibility(View.GONE);
                    readAdapter.setDataSet(readList);
                    if (listEntity.getCurPage() == 1) {

                    } else {
                        CustomToast.INSTANCE.showToast(listEntity.getCurPage() + "/" + listEntity.getTotalPage(), 800);
                    }
                }
            }
        });
    }

    private void delDialog(final int position) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(R.string.read_title);
        materialDialog.setMessage(R.string.read_del_msg);
        materialDialog.setPositiveButton(R.string.comment_del, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommentDeleteRequest.getInstance().exeRequest(CommentDeleteRequest.getInstance().generateUrl(readList.get(position).getId()), new IProtocolResponse() {
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
                            CustomToast.INSTANCE.showToast(R.string.read_del_fail);
                        }
                    }
                });
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }
}
