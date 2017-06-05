package com.iyuba.music.activity.study;

import android.os.Bundle;
import android.widget.CompoundButton;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.CustomToast;
import com.kyleduo.switchbutton.SwitchButton;

/**
 * Created by chentong1 on 2017/4/24.
 */

public class MediaButtonControlActivity extends BaseActivity {
    SwitchButton switchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mediabutton_control);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        switchButton = (SwitchButton) findViewById(R.id.mediabutton_control);
    }

    @Override
    protected void setListener() {
        super.setListener();
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setMediaButton(isChecked);
                if (isChecked) {
                    switchButton.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                    CustomToast.getInstance().showToast(R.string.setting_mediabutton_on);
                } else {
                    switchButton.setBackColorRes(R.color.background_light);
                    CustomToast.getInstance().showToast(R.string.setting_mediabutton_off);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        if (ConfigManager.getInstance().isMediaButton()) {
            switchButton.setCheckedImmediatelyNoEvent(true);
            switchButton.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            switchButton.setCheckedImmediatelyNoEvent(false);
            switchButton.setBackColorRes(R.color.background_light);
        }
        title.setText(R.string.setting_mediabutton);
    }
}
