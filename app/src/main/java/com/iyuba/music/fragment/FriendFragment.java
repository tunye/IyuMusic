package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.swiperefresh.MySwipeRefreshLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.me.ChattingActivity;
import com.iyuba.music.activity.me.FriendCenter;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.adapter.me.FriendAdapter;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.Friend;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.request.merequest.FriendRequest;
import com.iyuba.music.util.Utils;

import java.util.List;


/**
 * Created by 10202 on 2015/11/6.
 */
public class FriendFragment extends BaseRecyclerViewFragment implements MySwipeRefreshLayout.OnRefreshListener {
    private FriendAdapter friendAdapter;
    private int curPage;
    private boolean isLastPage = false;
    private String friendProtocol;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    public void setFriendProtocol(String friendProtocol) {
        this.friendProtocol = friendProtocol;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        friendAdapter = new FriendAdapter(context);
        friendAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.getInstance().pushFriendId(friendAdapter.getDatas().get(position).getUid());
                if ("chat".equals(((FriendCenter) getActivity()).getIntentMessage())) {
                    SocialManager.getInstance().pushFriendName(friendAdapter.getDatas().get(position).getUsername());
                    Intent intent = new Intent(context, ChattingActivity.class);
                    intent.putExtra("needpop", true);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(context, PersonalHomeActivity.class);
                    intent.putExtra("needpop", true);
                    startActivity(intent);
                }
            }
        });
        recyclerView.setAdapter(friendAdapter);
        setUserVisibleHint(true);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
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
        if (friendAdapter.getDatas().size() == 0) {

        } else if (!isLastPage) {
            curPage++;
            getFriendData();
        } else {
            swipeRefreshLayout.setRefreshing(false);
            CustomToast.getInstance().showToast(R.string.friend_load_all);
        }
    }

    private void getFriendData() {
        RequestClient.requestAsync(new FriendRequest(SocialManager.getInstance().getFriendId(), friendProtocol, null, curPage), new SimpleRequestCallBack<BaseListEntity<List<Friend>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Friend>> listEntity) {
                isLastPage = listEntity.isLastPage();
                if (isLastPage) {
                    if (curPage == 1) {
                        if (friendProtocol.equals(FriendRequest.RECOMMEND_REQUEST_CODE)) {
                            CustomToast.getInstance().showToast(R.string.friend_no_data);
                        } else {
                            CustomToast.getInstance().showToast(R.string.no_friend);
                        }
                    } else {
                        CustomToast.getInstance().showToast(R.string.friend_load_all);
                    }
                } else {
                    friendAdapter.addDatas(listEntity.getData());
                    if (curPage != 1 && !friendProtocol.equals(FriendRequest.RECOMMEND_REQUEST_CODE)) {
                        CustomToast.getInstance().showToast(curPage + "/" + listEntity.getTotalPage(), 800);
                    }
                }
                swipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
}
