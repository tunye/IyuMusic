package com.iyuba.music.activity.eggshell.weight_monitor;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;

import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseInputActivity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.GetAppColor;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.roundview.RoundRelativeLayout;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.kyleduo.switchbutton.SwitchButton;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Created by 10202 on 2017/6/19.
 */
public class WeightSetActivity extends BaseInputActivity implements View.OnClickListener {
    private RoundRelativeLayout initWeight, targetWeight, showTargetLy;
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
                ConfigManager.getInstance().setShowTarget(isChecked);
                if (isChecked) {
                    showTarget.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    showTarget.setBackColorRes(R.color.background_light);
                }
            }
        });
        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<WeightMonitorEntity> datas = new ArrayList<>();
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
                try {
                    WeightMonitorEntity item = new WeightMonitorEntity(format.parse("20170606"), 118.35, -2.0);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170607"), 116.85, -1.5);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170608"), 113.6, -3.25);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170609"), 114.5, 0.9);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170610"), 113.85, -0.65);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170611"), 113.1, -0.75);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170612"), 113.4, 0.3);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170613"), 112.85, -0.55);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170614"), 112.45, -0.45);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170615"), 112.65, 0.2);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170616"), 112.2, -0.45);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170617"), 111.75, -0.45);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170618"), 111.3, -0.45);
                    datas.add(item);
                    item = new WeightMonitorEntity(format.parse("20170619"), 110.85, -0.45);
                    datas.add(item);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Collections.reverse(datas);
                ConfigManager.getInstance().putString("weight_monitor", new Gson().toJson(datas));
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        showTarget.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isShowTarget());
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
                popInitDialog();
                break;
            case R.id.weight_set_target_ly:
                popTargetDialog();
                break;
            case R.id.weight_set_show_target_ly:
                showTarget.setChecked(!showTarget.isChecked());
                ConfigManager.getInstance().setShowTarget(showTarget.isChecked());
                break;
        }
    }

    private void popInitDialog() {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle("体重管家");
        final View contentView = LayoutInflater.from(context).inflate(R.layout.file_create, null);
        dialog.setContentView(contentView);
        final MaterialEditText fileName = (MaterialEditText) contentView.findViewById(R.id.file_name);
        fileName.setHint("请输入初始体重，以公斤计");
        dialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
                ConfigManager.getInstance().setInitWeight(Float.valueOf(fileName.getText().toString()));
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void popTargetDialog() {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle("体重管家");
        final View contentView = LayoutInflater.from(context).inflate(R.layout.file_create, null);
        dialog.setContentView(contentView);
        final MaterialEditText fileName = (MaterialEditText) contentView.findViewById(R.id.file_name);
        fileName.setHint("请输入目标体重，以公斤计");
        dialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
                ConfigManager.getInstance().setTargetWeight(Float.valueOf(fileName.getText().toString()));
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(fileName.getApplicationWindowToken(), 0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
