package com.iyuba.music.activity.study;

import android.content.Intent;
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
import com.iyuba.music.widget.recycleview.GridDividerItemDecoration;
import com.iyuba.music.widget.recycleview.MyLinearLayoutManager;

import java.util.Arrays;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/5.
 */
public class StudySetActivity extends BaseActivity implements View.OnClickListener {
    private RoundRelativeLayout playMode, nextMode, autoRound, downLoad, headplugPlay, headplugPause, originalSize;
    private CheckBox currAutoRound, currHeadplugPlay, currHeadplugPause;
    private TextView currPlayMode, currNextMode, currDownLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.study_setting);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        playMode = (RoundRelativeLayout) findViewById(R.id.study_set_playmode);
        nextMode = (RoundRelativeLayout) findViewById(R.id.study_set_nextmode);
        autoRound = (RoundRelativeLayout) findViewById(R.id.study_set_auto_round);
        downLoad = (RoundRelativeLayout) findViewById(R.id.study_set_download);
        headplugPlay = (RoundRelativeLayout) findViewById(R.id.study_set_headplug_play);
        headplugPause = (RoundRelativeLayout) findViewById(R.id.study_set_headplug_pause);
        originalSize = (RoundRelativeLayout) findViewById(R.id.study_set_original_size);
        currPlayMode = (TextView) findViewById(R.id.study_set_playmode_current);
        currNextMode = (TextView) findViewById(R.id.study_set_nextmode_current);
        currDownLoad = (TextView) findViewById(R.id.study_set_download_current);
        currAutoRound = (CheckBox) findViewById(R.id.study_set_auto_round_current);
        currHeadplugPlay = (CheckBox) findViewById(R.id.study_set_headplug_play_current);
        currHeadplugPause = (CheckBox) findViewById(R.id.study_set_headplug_pause_current);
    }

    @Override
    protected void setListener() {
        super.setListener();
        playMode.setOnClickListener(this);
        nextMode.setOnClickListener(this);
        autoRound.setOnClickListener(this);
        downLoad.setOnClickListener(this);
        headplugPlay.setOnClickListener(this);
        headplugPause.setOnClickListener(this);
        originalSize.setOnClickListener(this);
        currAutoRound.setOnClickListener(this);
        currHeadplugPlay.setOnClickListener(this);
        currHeadplugPause.setOnClickListener(this);
        currPlayMode.setOnClickListener(this);
        currNextMode.setOnClickListener(this);
        currDownLoad.setOnClickListener(this);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        currPlayMode.setText(getPlayMode(SettingConfigManager.instance.getStudyMode()));
        currNextMode.setText(getNextMode(SettingConfigManager.instance.getStudyPlayMode()));
        currDownLoad.setText(getDownload(SettingConfigManager.instance.getDownloadMode()));
        currAutoRound.setChecked(SettingConfigManager.instance.isAutoRound());
        currHeadplugPlay.setChecked(SettingConfigManager.instance.isAutoPlay());
        currHeadplugPause.setChecked(SettingConfigManager.instance.isAutoStop());
        title.setText(R.string.setting_study_set);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.study_set_playmode:
                popPlayModeDialog();
                break;
            case R.id.study_set_playmode_current:
                popPlayModeDialog();
                break;
            case R.id.study_set_nextmode:
                popNextModeDialog();
                break;
            case R.id.study_set_nextmode_current:
                popNextModeDialog();
                break;
            case R.id.study_set_download:
                popDownloadDialog();
                break;
            case R.id.study_set_download_current:
                popDownloadDialog();
                break;
            case R.id.study_set_auto_round:
                currAutoRound.setChecked(!currAutoRound.isChecked());
                SettingConfigManager.instance.setAutoRound(currAutoRound.isChecked());
                break;
            case R.id.study_set_headplug_play:
                currHeadplugPlay.setChecked(!currHeadplugPlay.isChecked());
                SettingConfigManager.instance.setAutoPlay(currHeadplugPlay.isChecked());
                break;
            case R.id.study_set_headplug_pause:
                currHeadplugPause.setChecked(!currHeadplugPause.isChecked());
                SettingConfigManager.instance.setAutoStop(currHeadplugPause.isChecked());
                break;
            case R.id.study_set_auto_round_current:
                SettingConfigManager.instance.setAutoRound(currAutoRound.isChecked());
                break;
            case R.id.study_set_headplug_play_current:
                SettingConfigManager.instance.setAutoPlay(currHeadplugPlay.isChecked());
                break;
            case R.id.study_set_headplug_pause_current:
                SettingConfigManager.instance.setAutoStop(currHeadplugPause.isChecked());
                break;
            case R.id.study_set_original_size:
                startActivity(new Intent(context, OriginalSizeActivity.class));
                break;
        }
    }

    private void popPlayModeDialog() {
        final MaterialDialog groupDialog = new MaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_playmode);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.type)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SettingConfigManager.instance.setStudyMode(position);
                currPlayMode.setText(getPlayMode(position));
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(SettingConfigManager.instance.getStudyMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new GridDividerItemDecoration());
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

    private void popNextModeDialog() {
        final MaterialDialog groupDialog = new MaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_nextmode);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.mode)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SettingConfigManager.instance.setStudyPlayMode(position);
                currNextMode.setText(getNextMode(position));
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(SettingConfigManager.instance.getStudyPlayMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new GridDividerItemDecoration());
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


    private void popDownloadDialog() {
        final MaterialDialog groupDialog = new MaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_download);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.download)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                SettingConfigManager.instance.setDownloadMode(position);
                currDownLoad.setText(getDownload(position));
                groupDialog.dismiss();
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
        adapter.setSelected(SettingConfigManager.instance.getDownloadMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new GridDividerItemDecoration());
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

    private String getPlayMode(int order) {
        String[] playModeGroup = context.getResources().getStringArray(R.array.type);
        return playModeGroup[order];
    }

    private String getNextMode(int order) {
        String[] nextModeGroup = context.getResources().getStringArray(R.array.mode);
        return nextModeGroup[order];
    }

    private String getDownload(int order) {
        String[] downloadGroup = context.getResources().getStringArray(R.array.download);
        return downloadGroup[order];
    }
}
