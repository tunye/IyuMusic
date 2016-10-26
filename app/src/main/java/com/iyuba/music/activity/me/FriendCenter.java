package com.iyuba.music.activity.me;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragmentAdapter.FriendFragmentAdapter;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.widget.imageview.TabIndicator;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by 10202 on 2016/3/1.
 */
public class FriendCenter extends BaseActivity {
    private ViewPager viewPager;
    private String startType, intentType;
    private boolean needPop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.friend_center);
        context = this;
        startType = getIntent().getStringExtra("type");
        intentType = getIntent().getStringExtra("intenttype");
        needPop = getIntent().getBooleanExtra("needpop", false);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        ArrayList<String> tabTitle = new ArrayList<>();
        tabTitle.addAll(Arrays.asList(context.getResources().getStringArray(R.array.friend_tab_title)));
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        TabIndicator viewPagerIndicator = (TabIndicator) findViewById(R.id.tab_indicator);
        viewPager.setAdapter(new FriendFragmentAdapter(getSupportFragmentManager()));
        viewPagerIndicator.setTabItemTitles(tabTitle);
        viewPagerIndicator.setViewPager(viewPager, 0);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, FindFriendActivity.class));
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.friend_search);
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
            SocialManager.instance.popFriendId();
        }
    }
}
