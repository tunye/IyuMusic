package com.iyuba.music.fragment;

import android.support.v4.app.Fragment;

import com.umeng.analytics.MobclickAgent;

/**
 * Created by 10202 on 2016/1/1.
 */
public class BaseFragment extends Fragment {

    public boolean onBackPressed() {
        return false;
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getName());
    }

    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getName());
    }
}
