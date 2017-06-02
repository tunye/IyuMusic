package com.iyuba.music.activity;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.listener.NoDoubleClickListener;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.ImmersiveManager;
import com.iyuba.music.util.Mathematics;
import com.umeng.analytics.MobclickAgent;


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
        ImmersiveManager.getInstance().updateImmersiveStatus(this);
        ((MusicApplication) getApplication()).pushActivity(this);
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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
                Rect outRect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                Mathematics.setMargins(toolBarLayout, 0, RuntimeManager.getWindowHeight() - outRect.height(), 0, 0);
            }
        }
    }
}
