package com.iyuba.music.activity.study;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragmentAdapter.ReadFragmentAdapter;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.imageview.TabIndicator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 10202 on 2016/3/21.
 */
public class ReadActivity extends BaseActivity {
    private boolean needRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);
        context = this;
        if (((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
            needRestart = true;
            sendBroadcast(new Intent("iyumusic.pause"));
        } else {
            needRestart = false;
        }
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ArrayList<String> tabTitle = new ArrayList<>();
        tabTitle.addAll(Arrays.asList(context.getResources().getStringArray(R.array.read_tab_title)));
        ViewPager viewPager = (ViewPager) findViewById(R.id.read_main);
        TabIndicator viewPagerIndicator = (TabIndicator) findViewById(R.id.tab_indicator);
        viewPager.setAdapter(new ReadFragmentAdapter(getSupportFragmentManager()));
        viewPagerIndicator.setTabItemTitles(tabTitle);
        viewPagerIndicator.setViewPager(viewPager, 0);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(StudyManager.instance.getCurArticle().getTitle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needRestart) {
            sendBroadcast(new Intent("iyumusic.pause"));
        }
    }
}
