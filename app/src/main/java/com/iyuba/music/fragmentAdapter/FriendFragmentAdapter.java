package com.iyuba.music.fragmentAdapter;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.fragment.FanFragment;
import com.iyuba.music.fragment.FollowFragment;
import com.iyuba.music.fragment.RecommendFragment;

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
        this.list = new ArrayList<>();
        list.add(new FollowFragment());
        list.add(new FanFragment());
        list.add(new RecommendFragment());
    }

    @Override
    public BaseFragment getItem(int position) {
        BaseFragment fragment = list.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("id", String.valueOf(position));
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public BaseFragment instantiateItem(ViewGroup container, int position) {
        BaseFragment fragment = (BaseFragment) super.instantiateItem(container, position);
        fm.beginTransaction().show(fragment).commit();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        BaseFragment fragment = list.get(position);
        fm.beginTransaction().hide(fragment).commit();
    }

    @Override
    public int getCount() {
        return 3;
    }
}
