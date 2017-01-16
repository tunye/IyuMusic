package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iyuba.music.R;
import com.iyuba.music.activity.me.ChattingActivity;
import com.iyuba.music.activity.me.FriendCenter;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.adapter.me.FriendAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.Follows;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.FollowRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.SwipeRefreshLayout.MySwipeRefreshLayout;

import java.util.ArrayList;


/**
 * Created by 10202 on 2015/11/6.
 */
public class FollowFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private ArrayList<Follows> followsArrayList;
    private FriendAdapter friendAdapter;
    private int curPage;
    private boolean isLastPage = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        followsArrayList = new ArrayList<>();
        friendAdapter = new FriendAdapter(context);
        friendAdapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.instance.pushFriendId(followsArrayList.get(position).getUid());
                if ("chat".equals(((FriendCenter) getActivity()).getIntentMessage())) {
                    SocialManager.instance.pushFriendName(followsArrayList.get(position).getUsername());
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
        curPage = 1;
        followsArrayList = new ArrayList<>();
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
        if (followsArrayList.size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getFriendData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.INSTANCE.showToast(R.string.friend_load_all);
        }
    }

    private void getFriendData() {
        FollowRequest.exeRequest(FollowRequest.generateUrl(SocialManager.instance.getFriendId(), curPage), new IProtocolResponse() {
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
                    followsArrayList.addAll((ArrayList<Follows>) listEntity.getData());
                    friendAdapter.setFriendList(followsArrayList);
                    if (curPage != 1) {
                        CustomToast.INSTANCE.showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
