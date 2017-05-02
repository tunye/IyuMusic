package com.iyuba.music.fragmentAdapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.iyuba.music.fragment.HelpFragment;

public class HelpFragmentAdapter extends FragmentPagerAdapter {
    private boolean usePullDown;

    public HelpFragmentAdapter(FragmentManager fm, boolean usePullDown) {
        super(fm);
        this.usePullDown = usePullDown;
    }

    @Override
    public Fragment getItem(int position) {
        return HelpFragment.newInstance(position, usePullDown);
    }

    @Override
    public int getCount() {
        return 5;
    }
}
