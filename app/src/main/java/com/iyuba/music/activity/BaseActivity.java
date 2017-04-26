package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.GetAppColor;
import com.umeng.analytics.MobclickAgent;


/**
 * Created by 10202 on 2015/10/23.
 */
public abstract class BaseActivity extends BaseSkinActivity {
    protected Context context;
    protected MaterialRippleLayout back;
    protected MaterialMenu backIcon;
    protected RelativeLayout toolBarLayout;
    protected TextView title, toolbarOper;

    protected boolean changeProperty;
    protected boolean mipush;

    //@TargetApi(19)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(GetAppColor.getInstance().getAppColor(this));
            window.setNavigationBarColor(GetAppColor.getInstance().getAppColor(this));
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        changeProperty = getIntent().getBooleanExtra(ChangePropertyBroadcast.RESULT_FLAG, false);
        mipush = getIntent().getBooleanExtra("pushIntent", false);
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        changeProperty = intent.getBooleanExtra(ChangePropertyBroadcast.RESULT_FLAG, false);
        mipush = intent.getBooleanExtra("pushIntent", false);
    }

    protected void initWidget() {
        back = (MaterialRippleLayout) findViewById(R.id.back);
        backIcon = (MaterialMenu) findViewById(R.id.back_material);
        toolBarLayout = (RelativeLayout) findViewById(R.id.toolbar_title_layout);
        title = (TextView) findViewById(R.id.toolbar_title);
    }

    protected void setListener() {
        back.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                onBackPressed();
            }
        });
    }

    protected void changeUIByPara() {
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
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
    public void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
    }

    public abstract static class NoDoubleClickListener implements View.OnClickListener {

        private static final int MIN_CLICK_DELAY_TIME = 500;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            }
        }

        public abstract void onNoDoubleClick(View v);
    }
}
