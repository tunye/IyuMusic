package com.iyuba.music.activity;

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
    public int getLayoutId() {
        return R.layout.developer;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        blog = findViewById(R.id.developer_blog);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.about_developer);
        blog.setAutoLinkMask(Linkify.ALL);
        blog.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
