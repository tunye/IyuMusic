package com.iyuba.music.activity;

import com.iyuba.music.R;

/**
 * Created by 10202 on 2016/4/1.
 */
public class TestActivity extends BaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.test;
    }

    @Override
    public void initWidget() {
        super.initWidget();
    }

    @Override
    public void setListener() {
        super.setListener();
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.app_name);
    }
}
