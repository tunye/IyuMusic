package com.iyuba.music.activity.study;

import android.widget.CompoundButton;

import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.widget.view.SwitchButton;

/**
 * Created by chentong1 on 2017/4/24.
 */

public class MediaButtonControlActivity extends BaseActivity {
    SwitchButton switchButton;

    @Override
    public int getLayoutId() {
        return R.layout.mediabutton_control;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        switchButton = findViewById(R.id.mediabutton_control);
    }

    @Override
    public void setListener() {
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
    public void onActivityCreated() {
        super.onActivityCreated();
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
