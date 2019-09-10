package com.iyuba.music.fragmentAdapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.fragment.FriendFragment;
import com.iyuba.music.request.merequest.FriendRequest;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/9.
 */
public class FriendFragmentAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    public ArrayList<BaseFragment> list;

    public FriendFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.list = new ArrayList<>(3);
        FriendFragment follow = new FriendFragment();
        follow.setFriendProtocol(FriendRequest.FOLLOW_REQUEST_CODE);
        FriendFragment fan = new FriendFragment();
        fan.setFriendProtocol(FriendRequest.FAN_REQUEST_CODE);
        FriendFragment recommend = new FriendFragment();
        recommend.setFriendProtocol(FriendRequest.RECOMMEND_REQUEST_CODE);
        list.add(follow);
        list.add(fan);
        list.add(recommend);
    }

    @Override
    public BaseFragment getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public BaseFragment instantiateItem(@NonNull ViewGroup container, int position) {
        BaseFragment fragment = (BaseFragment) super.instantiateItem(container, position);
        fm.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        BaseFragment fragment = list.get(position);
        fm.beginTransaction().hide(fragment).commitAllowingStateLoss();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
