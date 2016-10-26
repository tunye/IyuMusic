package com.iyuba.music.fragmentAdapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.iyuba.music.activity.study.OriginalFragment;
import com.iyuba.music.activity.study.OriginalSynFragment;
import com.iyuba.music.activity.study.StudyInfoFragment;
import com.iyuba.music.fragment.BaseFragment;
import com.iyuba.music.fragment.SimpleFragment;

/**
 * Created by 10202 on 2015/12/17.
 */
public class StudyFragmentAdapter extends FragmentPagerAdapter {
    private BaseFragment currentFragment;

    public StudyFragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        currentFragment = (BaseFragment) object;
        super.setPrimaryItem(container, position, object);
    }

    @Override
    public BaseFragment getItem(int position) {
        switch (position) {
            case 0:
                currentFragment = new StudyInfoFragment();
                break;
            case 1:
                currentFragment = new OriginalSynFragment();
                break;
            case 2:
                currentFragment = new OriginalFragment();
                break;
            default:
                currentFragment = SimpleFragment.newInstance(String.valueOf(position));
                break;
        }
        return currentFragment;
    }

    public BaseFragment getCurrentFragment() {
        return currentFragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}

