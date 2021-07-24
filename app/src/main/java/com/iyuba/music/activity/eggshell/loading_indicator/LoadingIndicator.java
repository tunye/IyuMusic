package com.iyuba.music.activity.eggshell.loading_indicator;

import android.widget.TextView;

import com.buaa.ct.core.util.GetAppColor;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by 10202 on 2016/12/19.
 */

public class LoadingIndicator extends BaseActivity {
    AVLoadingIndicatorView loadingIndicatorView;
    TextView loadingIndicatorName;

    @Override
    public int getLayoutId() {
        return R.layout.loading_indicator;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        loadingIndicatorView = findViewById(R.id.loading_indicator);
        loadingIndicatorName = findViewById(R.id.loading_indicator_name);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText("加载小动画");
        loadingIndicatorName.setText(getIntent().getStringExtra("indicator"));
        loadingIndicatorView.setIndicator(getIntent().getStringExtra("indicator"));
        loadingIndicatorView.setIndicatorColor(GetAppColor.getInstance().getAppColor());
    }
}
