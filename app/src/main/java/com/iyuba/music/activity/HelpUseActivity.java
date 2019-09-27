package com.iyuba.music.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Window;
import android.view.WindowManager;

import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.GetAppColor;
import com.iyuba.music.R;
import com.iyuba.music.fragmentAdapter.HelpFragmentAdapter;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.apprequest.VisitorIdRequest;
import com.iyuba.music.widget.imageview.PageIndicator;

/**
 * 使用说明Activity
 *
 * @author chentong
 */
public class HelpUseActivity extends BaseActivity {
    public PageIndicator pi;
    private ViewPager viewPager;
    private float lastChange = 0;
    private boolean lastFlag;
    private boolean usePullDown;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(GetAppColor.getInstance().getAppColor());
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        usePullDown = getIntent().getBooleanExtra("UsePullDown", false);
    }

    @Override
    public int getLayoutId() {
        return R.layout.help_use;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        viewPager = findViewById(R.id.viewpager);
        viewPager.setAdapter(new HelpFragmentAdapter(getSupportFragmentManager(), usePullDown));
        pi = findViewById(R.id.pageindicator);
        pi.setFillColor(0xffededed);
        pi.setStrokeColor(0xffededed);
        pi.setCircleCount(viewPager.getAdapter().getCount());
    }

    @Override
    public void setListener() {
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                pi.setDirection(PageIndicator.NONE);
                pi.setCurrentItem(viewPager.getCurrentItem());
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                if (lastChange != 0 && arg1 != 0) {
                    if (lastChange > arg1) {//左滑
                        pi.setDirection(PageIndicator.LEFT);
                        pi.setMovePercent(arg0 + 1, arg1);
                    } else {
                        pi.setDirection(PageIndicator.RIGHT);
                        pi.setMovePercent(arg0, arg1);
                    }
                }
                lastChange = arg1;
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                switch (arg0) {
                    case 0:// 停止变更
                        if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1 && !lastFlag) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                            if (!usePullDown) {
                                startActivity(new Intent(HelpUseActivity.this, MainActivity.class));
                            }
                            finish();
                        }
                        lastFlag = true;
                        break;
                    case 1:
                        lastFlag = false;
                        break;
                    case 2: // 加载完毕
                        lastFlag = true;
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResumed() {
        super.onActivityResumed();
        if (AccountManager.getInstance().needGetVisitorID()) {
            RequestClient.requestAsync(new VisitorIdRequest(), new SimpleRequestCallBack<String>() {
                @Override
                public void onSuccess(String s) {

                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (usePullDown) {
            super.onBackPressed();
        }
    }
}