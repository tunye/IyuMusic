package com.iyuba.music.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
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
public class FriendFragment extends BaseRecyclerViewFragment {
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
        ownerAdapter = new FriendAdapter(context);
        ownerAdapter.setOnItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SocialManager.getInstance().pushFriendId(getData().get(position).getUid());
                if ("chat".equals(((FriendCenter) getActivity()).getIntentMessage())) {
                    SocialManager.getInstance().pushFriendName(getData().get(position).getUsername());
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
        owner.setAdapter(ownerAdapter);
        setUserVisibleHint(true);
        return view;
    }

    public List<Friend> getData() {
        return ((FriendAdapter) ownerAdapter).getDatas();
    }

    @Override
    protected int getToastResource() {
        return R.string.friend_load_all;
    }

    @Override
    public void getNetData() {
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
                    ((FriendAdapter) ownerAdapter).addDatas(listEntity.getData());
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
