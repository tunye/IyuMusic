package com.iyuba.music.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.iyuba.music.R;
import com.iyuba.music.fragmentAdapter.HelpFragmentAdapter;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.imageview.PageIndicator;
import com.umeng.analytics.MobclickAgent;

/**
 * 使用说明Activity
 *
 * @author chentong
 */
public class HelpUseActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private float lastChange = 0;
    private PageIndicator pi;
    private boolean lastFlag;
    private LocalBroadcastManager localBroadcastManager;
    private HelpFinishBroadcast helpFinishBroadcast;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(GetAppColor.getInstance().getAppColor(this));
            window.setNavigationBarColor(GetAppColor.getInstance().getAppColor(this));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.help_use);
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        helpFinishBroadcast = new HelpFinishBroadcast();
        IntentFilter intentFilter = new IntentFilter("pulldoor.finish");
        localBroadcastManager.registerReceiver(helpFinishBroadcast, intentFilter);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        pi = (PageIndicator) findViewById(R.id.pageindicator);
        pi.setFillColor(0xffededed);
        pi.setStrokeColor(0xffededed);
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
                        if (viewPager.getCurrentItem() == viewPager.getAdapter()
                                .getCount() - 1 && !lastFlag) {
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
        viewPager.setAdapter(new HelpFragmentAdapter(getSupportFragmentManager()));
        pi.setCircleCount(viewPager.getAdapter().getCount());
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        localBroadcastManager.unregisterReceiver(helpFinishBroadcast);
    }

    class HelpFinishBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            pi.setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            localBroadcastManager.unregisterReceiver(helpFinishBroadcast);
            HelpUseActivity.this.finish();
        }
    }
}