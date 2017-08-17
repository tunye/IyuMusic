package com.iyuba.music.activity;

import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.widget.TextView;

import com.iyuba.music.R;

/**
 * Created by 10202 on 2015/11/30.
 */
public class DeveloperActivity extends BaseActivity {
    private TextView blog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.developer);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        blog = (TextView) findViewById(R.id.developer_blog);
    }

    @Override
    protected void setListener() {
        super.setListener();
        blog.setAutoLinkMask(Linkify.ALL);
        blog.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.about_developer);
    }
}
