package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragmentAdapter.FriendFragmentAdapter;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.widget.imageview.TabIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 10202 on 2016/3/1.
 */
public class FriendCenter extends BaseActivity {
    private ViewPager viewPager;
    private String startType, intentType;
    private boolean needPop;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        startType = getIntent().getStringExtra("type");
        intentType = getIntent().getStringExtra("intenttype");
        needPop = getIntent().getBooleanExtra("needpop", false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.friend_center;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        List<String> tabTitle = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.friend_tab_title)));
        viewPager = findViewById(R.id.viewpager);
        TabIndicator viewPagerIndicator = findViewById(R.id.tab_indicator);
        viewPager.setAdapter(new FriendFragmentAdapter(getSupportFragmentManager()));
        viewPagerIndicator.setTabItemTitles(tabTitle);
        viewPagerIndicator.setViewPager(viewPager, 0);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                startActivity(new Intent(context, SearchFriendActivity.class));
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        enableToolbarOper(R.string.friend_search);
        title.setText(R.string.friend_title);
        if (TextUtils.isEmpty(startType)) {
            viewPager.setCurrentItem(0);
        } else {
            viewPager.setCurrentItem(Integer.parseInt(startType));
        }
    }

    public String getIntentMessage() {
        return intentType;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needPop) {
            SocialManager.getInstance().popFriendId();
        }
    }
}
