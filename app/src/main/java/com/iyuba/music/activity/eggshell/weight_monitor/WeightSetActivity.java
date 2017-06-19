package com.iyuba.music.activity.eggshell.weight_monitor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.BaseInputActivity;
import com.iyuba.music.activity.study.MediaButtonControlActivity;
import com.iyuba.music.activity.study.OriginalSizeActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.iyuba.music.widget.roundview.RoundRelativeLayout;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.Arrays;

/**
 * Created by 10202 on 2015/12/5.
 */
public class WeightSetActivity extends BaseInputActivity implements View.OnClickListener {
    private RoundRelativeLayout initWeight, targetWeight,showTargetLy;
    private SwitchButton showTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weight_setting);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        initWeight = (RoundRelativeLayout) findViewById(R.id.weight_set_init_ly);
        AddRippleEffect.addRippleEffect(initWeight);
        targetWeight = (RoundRelativeLayout) findViewById(R.id.weight_set_target_ly);
        AddRippleEffect.addRippleEffect(targetWeight);
        showTargetLy = (RoundRelativeLayout) findViewById(R.id.weight_set_show_target_ly);
        AddRippleEffect.addRippleEffect(showTargetLy);
        showTarget = (SwitchButton) findViewById(R.id.weight_set_show_target);
    }

    @Override
    protected void setListener() {
        super.setListener();
        initWeight.setOnClickListener(this);
        targetWeight.setOnClickListener(this);
        showTargetLy.setOnClickListener(this);

        showTarget.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setAutoRound(isChecked);
                if (isChecked) {
                    showTarget.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    showTarget.setBackColorRes(R.color.background_light);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        showTarget.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isAutoRound());
        if (showTarget.isChecked()) {
            showTarget.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            showTarget.setBackColorRes(R.color.background_light);
        }
        title.setText("体重管家设置");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weight_set_init_ly:
//                popPlayModeDialog();
                break;
            case R.id.weight_set_target_ly:
//                popPlayModeDialog();
                break;
            case R.id.weight_set_show_target_ly:
                showTarget.setChecked(!showTarget.isChecked());
//                ConfigManager.getInstance().setAutoRound(showTarget.isChecked());
                break;
        }
    }
}
