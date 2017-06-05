package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenu;
import com.balysv.materialmenu.MaterialMenuDrawable;
import com.balysv.materialripple.MaterialRippleLayout;
import com.buaa.ct.skin.BaseSkinActivity;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.listener.NoDoubleClickListener;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.util.ImmersiveManager;
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
        ImmersiveManager.getInstance().updateImmersiveStatus(this);
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
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ((MusicApplication) getApplication()).popActivity(this);
    }
}
