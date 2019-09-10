package com.iyuba.music.activity.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragment.MusicFragment;

/**
 * Created by 10202 on 2016/3/7.
 */
public class MusicActivity extends BaseActivity {

    @Override
    public int getLayoutId() {
        return R.layout.music_activity;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        Fragment fragment = new MusicFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.music_fragment, fragment).commitAllowingStateLoss();
    }

    @Override
    public void setListener() {
        super.setListener();
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.article_song);
    }
}
