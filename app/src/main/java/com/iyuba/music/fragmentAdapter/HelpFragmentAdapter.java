package com.iyuba.music.fragmentAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iyuba.music.fragment.HelpFragment;

public class HelpFragmentAdapter extends FragmentPagerAdapter {
    private static final int[] CONTENT = new int[]{1, 2, 3, 4, 5};
    private boolean usePullDown;

    public HelpFragmentAdapter(FragmentManager fm,boolean usePullDown) {
        super(fm);
        this.usePullDown=usePullDown;
    }

    @Override
    public Fragment getItem(int position) {
        return HelpFragment.newInstance(CONTENT[position],usePullDown);
    }

    @Override
    public int getCount() {
        return CONTENT.length;
    }
}
