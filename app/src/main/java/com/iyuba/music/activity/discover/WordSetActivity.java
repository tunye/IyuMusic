package com.iyuba.music.activity.discover;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SettingConfigManager;
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
public class WordSetActivity extends BaseActivity implements View.OnClickListener {
    private RoundRelativeLayout group, showDef, autoAudio, autoAdd;
    private SwitchButton currShowDef, currAutoAudio, currAutoAdd;
    private TextView currGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.word_setting);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        group = (RoundRelativeLayout) findViewById(R.id.word_set_group);
        AddRippleEffect.addRippleEffect(group);
        currGroup = (TextView) findViewById(R.id.word_set_group_current);
        showDef = (RoundRelativeLayout) findViewById(R.id.word_set_show_def);
        AddRippleEffect.addRippleEffect(showDef);
        currShowDef = (SwitchButton) findViewById(R.id.word_set_show_def_current);
        autoAudio = (RoundRelativeLayout) findViewById(R.id.word_set_play);
        AddRippleEffect.addRippleEffect(autoAudio);
        currAutoAudio = (SwitchButton) findViewById(R.id.word_set_play_current);
        autoAdd = (RoundRelativeLayout) findViewById(R.id.word_set_auto_add);
        AddRippleEffect.addRippleEffect(autoAdd);
        currAutoAdd = (SwitchButton) findViewById(R.id.word_set_auto_add_current);
    }

    @Override
    protected void setListener() {
        super.setListener();
        group.setOnClickListener(this);
        showDef.setOnClickListener(this);
        autoAudio.setOnClickListener(this);
        autoAdd.setOnClickListener(this);
        currGroup.setOnClickListener(this);

        currAutoAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingConfigManager.getInstance().setWordAutoAdd(isChecked);
                if (isChecked) {
                    currAutoAdd.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currAutoAdd.setBackColorRes(R.color.background_light);
                }
            }
        });
        currAutoAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingConfigManager.getInstance().setWordAutoPlay(isChecked);
                if (isChecked) {
                    currAutoAudio.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currAutoAudio.setBackColorRes(R.color.background_light);
                }
            }
        });
        currShowDef.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SettingConfigManager.getInstance().setWordDefShow(isChecked);
                if (isChecked) {
                    currShowDef.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currShowDef.setBackColorRes(R.color.background_light);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        currGroup.setText(getWordOrder(SettingConfigManager.getInstance().getWordOrder()));
        currAutoAudio.setCheckedImmediatelyNoEvent(SettingConfigManager.getInstance().isWordAutoPlay());
        currAutoAdd.setCheckedImmediatelyNoEvent(SettingConfigManager.getInstance().isWordAutoAdd());
        currShowDef.setCheckedImmediatelyNoEvent(SettingConfigManager.getInstance().isWordDefShow());
        if (currAutoAudio.isChecked()) {
            currAutoAudio.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currAutoAudio.setBackColorRes(R.color.background_light);
        }
        if (currAutoAdd.isChecked()) {
            currAutoAdd.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currAutoAdd.setBackColorRes(R.color.background_light);
        }
        if (currShowDef.isChecked()) {
            currShowDef.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currShowDef.setBackColorRes(R.color.background_light);
        }
        title.setText(R.string.setting_word_set);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.word_set_group:
            case R.id.word_set_group_current:
                popGroupDialog();
                break;
            case R.id.word_set_show_def:
                currShowDef.setChecked(!currShowDef.isChecked());
                SettingConfigManager.getInstance().setWordDefShow(currShowDef.isChecked());
                break;
            case R.id.word_set_auto_add:
                currAutoAdd.setChecked(!currAutoAdd.isChecked());
                SettingConfigManager.getInstance().setWordAutoAdd(currAutoAdd.isChecked());
                break;
            case R.id.word_set_play:
                currAutoAudio.setChecked(!currAutoAudio.isChecked());
                SettingConfigManager.getInstance().setWordAutoPlay(currAutoAudio.isChecked());
                break;
        }
    }

    private void popGroupDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.word_set_group);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.word_group)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (SettingConfigManager.getInstance().getWordOrder() != position) {
                    SettingConfigManager.getInstance().setWordOrder(position);
                    currGroup.setText(getWordOrder(position));
                }
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(SettingConfigManager.getInstance().getWordOrder());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }

    private String getWordOrder(int order) {
        String[] wordGroup = context.getResources().getStringArray(R.array.word_group);
        return wordGroup[order];
    }
}
