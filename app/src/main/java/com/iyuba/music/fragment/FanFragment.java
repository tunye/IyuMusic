package com.iyuba.music.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.activity.me.ChattingActivity;
import com.iyuba.music.activity.me.FriendCenter;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.adapter.me.FriendAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.Fans;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.listener.IOnDoubleClick;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.FanRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;

import java.util.ArrayList;


/**
 * Created by 10202 on 2015/11/6.
 */
public class FanFragment extends BaseFragment implements MySwipeRefreshLayout.OnRefreshListener, IOnClickListener {
    private Context context;
    private RecyclerView recyclerView;
    private ArrayList<Fans> fansArrayList;
    private FriendAdapter friendAdapter;
    private MySwipeRefreshLayout swipeRefreshLayout;
    private int curPage;
    private boolean isLastPage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.friend_recycle, null);
        recyclerView = (RecyclerView) view.findViewById(R.id.friendlist);
        swipeRefreshLayout = (MySwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(0xff259CF7, 0xff2ABB51, 0xffE10000, 0xfffaaa3c);
        swipeRefreshLayout.setFirstIndex(0);
        swipeRefreshLayout.setOnRefreshListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        fansArrayList = new ArrayList<>();
        friendAdapter = new FriendAdapter(context);
        friendAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.instance.pushFriendId(fansArrayList.get(position).getUid());
                if ("chat".equals(((FriendCenter) getActivity()).getIntentMessage())) {
                    SocialManager.instance.pushFriendName(fansArrayList.get(position).getUsername());
                    Intent intent = new Intent(context, ChattingActivity.class);
                    intent.putExtra("needpop", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, PersonalHomeActivity.class);
                    intent.putExtra("needpop", true);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
            }
        });
        recyclerView.setAdapter(friendAdapter);
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
        curPage = 1;
        fansArrayList = new ArrayList<>();
        isLastPage = false;
        getFriendData();
    }

    /**
     * 加载更多
     *
     * @param index 当前分页索引
     */
    @Override
    public void onLoad(int index) {
        if (fansArrayList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getFriendData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.friend_load_all);
        }
    }

    private void getFriendData() {
        FanRequest.getInstance().exeRequest(FanRequest.getInstance().generateUrl(SocialManager.instance.getFriendId(), curPage), new IProtocolResponse() {
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
                BaseListEntity listEntity = (BaseListEntity) object;
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    if (curPage == 1) {
                        CustomToast.INSTANCE.showToast(R.string.no_friend);
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.friend_load_all);
                    }
                } else {
                    fansArrayList.addAll((ArrayList<Fans>) listEntity.getData());
                    friendAdapter.setFriendList(fansArrayList);
                    if (curPage != 1) {
                        CustomToast.INSTANCE.showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onClick(View view, Object message) {
        recyclerView.scrollToPosition(0);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getActivity().findViewById(R.id.toolbar).setOnTouchListener(new IOnDoubleClick(this, context.getString(R.string.list_double)));
        }
    }
}
