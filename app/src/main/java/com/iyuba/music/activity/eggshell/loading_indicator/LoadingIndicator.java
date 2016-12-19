package com.iyuba.music.activity.eggshell.loading_indicator;

import android.os.Bundle;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.util.GetAppColor;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by 10202 on 2016/12/19.
 */

public class LoadingIndicator extends BaseActivity {
    AVLoadingIndicatorView loadingIndicatorView;
    TextView loadingIndicatorName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_indicator);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        loadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        loadingIndicatorName = (TextView) findViewById(R.id.loading_indicator_name);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText("加载小动画");
        loadingIndicatorName.setText(getIntent().getStringExtra("indicator"));
        loadingIndicatorView.setIndicator(getIntent().getStringExtra("indicator"));
        loadingIndicatorView.setIndicatorColor(GetAppColor.instance.getAppColor(context));
    }
}
