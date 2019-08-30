package com.iyuba.music.activity;

import android.os.Bundle;

import com.iyuba.music.R;

/**
 * Created by 10202 on 2016/4/1.
 */
public class TestActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
    }

    @Override
    protected void setListener() {
        super.setListener();
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.app_name);
    }
}
