package com.iyuba.music.activity.discover;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.flyco.roundview.RoundRelativeLayout;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.adapter.MaterialDialogAdapter;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.widget.recycleview.DividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;

import java.util.Arrays;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/5.
 */
public class WordSetActivity extends BaseActivity implements View.OnClickListener {
    private RoundRelativeLayout group, showDef, autoAudio, autoAdd;
    private CheckBox currShowDef, currAutoAudio, currAutoAdd;
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
        currGroup = (TextView) findViewById(R.id.word_set_group_current);
        showDef = (RoundRelativeLayout) findViewById(R.id.word_set_show_def);
        currShowDef = (CheckBox) findViewById(R.id.word_set_show_def_current);
        autoAudio = (RoundRelativeLayout) findViewById(R.id.word_set_play);
        currAutoAudio = (CheckBox) findViewById(R.id.word_set_play_current);
        autoAdd = (RoundRelativeLayout) findViewById(R.id.word_set_auto_add);
        currAutoAdd = (CheckBox) findViewById(R.id.word_set_auto_add_current);
    }

    @Override
    protected void setListener() {
        super.setListener();
        group.setOnClickListener(this);
        showDef.setOnClickListener(this);
        autoAudio.setOnClickListener(this);
        autoAdd.setOnClickListener(this);

        currAutoAdd.setOnClickListener(this);
        currAutoAudio.setOnClickListener(this);
        currShowDef.setOnClickListener(this);
        currGroup.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        currGroup.setText(getWordOrder(SettingConfigManager.instance.getWordOrder()));
        currAutoAudio.setChecked(SettingConfigManager.instance.isWordAutoPlay());
        currAutoAdd.setChecked(SettingConfigManager.instance.isWordAutoAdd());
        currShowDef.setChecked(SettingConfigManager.instance.isWordDefShow());
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
                SettingConfigManager.instance.setWordDefShow(currShowDef.isChecked());
                break;
            case R.id.word_set_auto_add:
                currAutoAdd.setChecked(!currAutoAdd.isChecked());
                SettingConfigManager.instance.setWordAutoAdd(currAutoAdd.isChecked());
                break;
            case R.id.word_set_play:
                currAutoAudio.setChecked(!currAutoAudio.isChecked());
                SettingConfigManager.instance.setWordAutoPlay(currAutoAudio.isChecked());
                break;
            case R.id.word_set_auto_add_current:
                SettingConfigManager.instance.setWordAutoAdd(currAutoAdd.isChecked());
                break;
            case R.id.word_set_play_current:
                SettingConfigManager.instance.setWordAutoPlay(currAutoAudio.isChecked());
                break;
            case R.id.word_set_show_def_current:
                SettingConfigManager.instance.setWordDefShow(currShowDef.isChecked());
                break;
        }
    }

    private void popGroupDialog() {
        final MaterialDialog groupDialog = new MaterialDialog(context);
        groupDialog.setTitle(R.string.word_set_group);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.word_group)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SettingConfigManager.instance.setWordOrder(position);
                currGroup.setText(getWordOrder(position));
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(SettingConfigManager.instance.getWordOrder());
        languageList.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL_LIST));
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.cancel, new View.OnClickListener() {
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
