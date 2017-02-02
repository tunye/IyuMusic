package com.iyuba.music.activity;

import android.annotation.TargetApi;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.util.GetAppColor;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;


/**
 * Created by 10202 on 2015/10/23.
 */
public abstract class BaseInputActivity extends BaseSkinActivity {
    protected Context context;
    protected MaterialRippleLayout back;
    protected MaterialMenu backIcon;
    protected RelativeLayout toolBarLayout;
    protected TextView title, toolbarOper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(GetAppColor.instance.getAppColor(this));
            window.setNavigationBarColor(GetAppColor.instance.getAppColor(this));
        }
        PushAgent.getInstance(context).onAppStart();
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    protected void initWidget() {
        back = (MaterialRippleLayout) findViewById(R.id.back);
        backIcon = (MaterialMenu) findViewById(R.id.back_material);
        toolBarLayout = (RelativeLayout) findViewById(R.id.toolbar_title_layout);
        title = (TextView) findViewById(R.id.toolbar_title);
    }

    protected void setListener() {
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
}
