package com.iyuba.music.fragmentAdapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.fragment.AnnouncerFragment;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.fragment.ClassifyFragment;
import com.iyuba.music.fragment.MusicFragment;
import com.iyuba.music.fragment.NewsFragment;
import com.iyuba.music.fragment.SongCategoryFragment;
import com.iyuba.music.network.NetWorkState;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/9.
 */
public class MainFragmentAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    public ArrayList<BaseFragment> list;

    public MainFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.list = new ArrayList<>(4);
        list.add(new NewsFragment());
        list.add(new ClassifyFragment());
        list.add(new AnnouncerFragment());
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            list.add(new SongCategoryFragment());
        } else {
            list.add(new MusicFragment());
        }
    }

    @Override
    public BaseFragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public BaseFragment instantiateItem(ViewGroup container, int position) {
        BaseFragment fragment = (BaseFragment) super.instantiateItem(container, position);
        fm.beginTransaction().show(fragment).commitAllowingStateLoss();
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        BaseFragment fragment = list.get(position);
        fm.beginTransaction().hide(fragment).commitAllowingStateLoss();
    }

    @Override
    public int getCount() {
        return 4;
    }

}
