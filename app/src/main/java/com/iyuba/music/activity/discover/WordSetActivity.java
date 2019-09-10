package com.iyuba.music.activity.discover;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.listener.OnRecycleViewItemClickListener;
import com.buaa.ct.core.util.AddRippleEffect;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.core.view.image.DividerItemDecoration;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;
import com.iyuba.music.widget.roundview.RoundRelativeLayout;
import com.iyuba.music.widget.view.SwitchButton;

import java.util.Arrays;

/**
 * Created by 10202 on 2015/12/5.
 */
public class WordSetActivity extends BaseActivity implements View.OnClickListener {
    private RoundRelativeLayout group, showDef, autoAudio, autoAdd;
    private SwitchButton currShowDef, currAutoAudio, currAutoAdd;
    private TextView currGroup;

    @Override
    public int getLayoutId() {
        return R.layout.word_setting;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        group = findViewById(R.id.word_set_group);
        AddRippleEffect.addRippleEffect(group, 200);
        currGroup = findViewById(R.id.word_set_group_current);
        showDef = findViewById(R.id.word_set_show_def);
        AddRippleEffect.addRippleEffect(showDef, 200);
        currShowDef = findViewById(R.id.word_set_show_def_current);
        autoAudio = findViewById(R.id.word_set_play);
        AddRippleEffect.addRippleEffect(autoAudio, 200);
        currAutoAudio = findViewById(R.id.word_set_play_current);
        autoAdd = findViewById(R.id.word_set_auto_add);
        AddRippleEffect.addRippleEffect(autoAdd, 200);
        currAutoAdd = findViewById(R.id.word_set_auto_add_current);
    }

    @Override
    public void setListener() {
        super.setListener();
        group.setOnClickListener(this);
        showDef.setOnClickListener(this);
        autoAudio.setOnClickListener(this);
        autoAdd.setOnClickListener(this);
        currGroup.setOnClickListener(this);

        currAutoAdd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setWordAutoAdd(isChecked);
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
                ConfigManager.getInstance().setWordAutoPlay(isChecked);
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
                ConfigManager.getInstance().setWordDefShow(isChecked);
                if (isChecked) {
                    currShowDef.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currShowDef.setBackColorRes(R.color.background_light);
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        currGroup.setText(getWordOrder(ConfigManager.getInstance().getWordOrder()));
        currAutoAudio.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isWordAutoPlay());
        currAutoAdd.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isWordAutoAdd());
        currShowDef.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isWordDefShow());
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
                ConfigManager.getInstance().setWordDefShow(currShowDef.isChecked());
                break;
            case R.id.word_set_auto_add:
                currAutoAdd.setChecked(!currAutoAdd.isChecked());
                ConfigManager.getInstance().setWordAutoAdd(currAutoAdd.isChecked());
                break;
            case R.id.word_set_play:
                currAutoAudio.setChecked(!currAutoAudio.isChecked());
                ConfigManager.getInstance().setWordAutoPlay(currAutoAudio.isChecked());
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
                if (ConfigManager.getInstance().getWordOrder() != position) {
                    ConfigManager.getInstance().setWordOrder(position);
                    currGroup.setText(getWordOrder(position));
                }
                groupDialog.dismiss();
            }
        });
        adapter.setSelected(ConfigManager.getInstance().getWordOrder());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
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
