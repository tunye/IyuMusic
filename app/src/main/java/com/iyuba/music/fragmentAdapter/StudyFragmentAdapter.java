package com.iyuba.music.fragmentAdapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.activity.study.OriginalFragment;
import com.iyuba.music.activity.study.OriginalSynFragment;
import com.iyuba.music.activity.study.StudyInfoFragment;
import com.iyuba.music.fragment.BaseFragment;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/17.s
 */
public class StudyFragmentAdapter extends FragmentPagerAdapter {
    public FragmentManager fm;
    public ArrayList<BaseFragment> list;

    public StudyFragmentAdapter(FragmentManager fm) {
        super(fm);
        this.fm = fm;
        this.list = new ArrayList<>(3);
        list.add(new StudyInfoFragment());
        list.add(new OriginalSynFragment());
        list.add(new OriginalFragment());
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

    public void destroy() {
        list.clear();
        list = null;
    }

    public void refresh() {
        StudyInfoFragment infoFragment = (StudyInfoFragment) list.get(0);
        infoFragment.refresh();
        OriginalSynFragment synFragment = (OriginalSynFragment) list.get(1);
        synFragment.refresh();
        OriginalFragment originalFragment = (OriginalFragment) list.get(2);
        originalFragment.refresh();
    }

    public void changeLanguage() {
        OriginalSynFragment synFragment = (OriginalSynFragment) list.get(1);
        synFragment.changeLanguage();
        OriginalFragment originalFragment = (OriginalFragment) list.get(2);
        originalFragment.changeLanguage();
    }

    @Override
    public int getCount() {
        return 3;
    }
}

