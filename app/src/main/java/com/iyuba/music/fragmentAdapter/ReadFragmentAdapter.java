package com.iyuba.music.fragmentAdapter;

import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.activity.study.ReadFragment;
import com.iyuba.music.activity.study.ReadNewFragment;
import com.iyuba.music.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.
 */
public class ReadFragmentAdapter extends FragmentStatePagerAdapter {
    public FragmentManager fm;
    public ArrayList<BaseFragment> list;

    public ReadFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.list = new ArrayList<>(3);
        list.add(new ReadFragment());
        ReadNewFragment topFragment = new ReadNewFragment();
        topFragment.setDataType("agree");
        list.add(topFragment);
        ReadNewFragment newFragment = new ReadNewFragment();
        newFragment.setDataType("no");
        list.add(newFragment);
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

