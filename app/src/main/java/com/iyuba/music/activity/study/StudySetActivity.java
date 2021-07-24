package com.iyuba.music.activity.study;

import android.content.Intent;
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
public class StudySetActivity extends BaseActivity implements View.OnClickListener {
    private RoundRelativeLayout playMode, nextMode, autoRound, downLoad, headplugPlay, headplugPause, originalSize, mediaButton;
    private SwitchButton currAutoRound, currHeadplugPlay, currHeadplugPause;
    private TextView currPlayMode, currNextMode, currDownLoad;

    @Override
    public int getLayoutId() {
        return R.layout.study_setting;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        playMode = findViewById(R.id.study_set_playmode);
        AddRippleEffect.addRippleEffect(playMode, 200);
        nextMode = findViewById(R.id.study_set_nextmode);
        AddRippleEffect.addRippleEffect(nextMode, 200);
        autoRound = findViewById(R.id.study_set_auto_round);
        AddRippleEffect.addRippleEffect(autoRound, 200);
        downLoad = findViewById(R.id.study_set_download);
        AddRippleEffect.addRippleEffect(downLoad, 200);
        headplugPlay = findViewById(R.id.study_set_headplug_play);
        AddRippleEffect.addRippleEffect(headplugPlay, 200);
        headplugPause = findViewById(R.id.study_set_headplug_pause);
        AddRippleEffect.addRippleEffect(headplugPause, 200);
        originalSize = findViewById(R.id.study_set_original_size);
        AddRippleEffect.addRippleEffect(originalSize, 200);
        mediaButton = findViewById(R.id.study_set_media_button);
        AddRippleEffect.addRippleEffect(mediaButton, 200);
        currPlayMode = findViewById(R.id.study_set_playmode_current);
        currNextMode = findViewById(R.id.study_set_nextmode_current);
        currDownLoad = findViewById(R.id.study_set_download_current);
        currAutoRound = findViewById(R.id.study_set_auto_round_current);
        currHeadplugPlay = findViewById(R.id.study_set_headplug_play_current);
        currHeadplugPause = findViewById(R.id.study_set_headplug_pause_current);
    }

    @Override
    public void setListener() {
        super.setListener();
        playMode.setOnClickListener(this);
        nextMode.setOnClickListener(this);
        autoRound.setOnClickListener(this);
        downLoad.setOnClickListener(this);
        headplugPlay.setOnClickListener(this);
        headplugPause.setOnClickListener(this);
        originalSize.setOnClickListener(this);
        mediaButton.setOnClickListener(this);
        currPlayMode.setOnClickListener(this);
        currNextMode.setOnClickListener(this);
        currDownLoad.setOnClickListener(this);

        currAutoRound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setAutoRound(isChecked);
                if (isChecked) {
                    currAutoRound.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currAutoRound.setBackColorRes(R.color.background_light);
                }
            }
        });
        currHeadplugPlay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setAutoPlay(isChecked);
                if (isChecked) {
                    currHeadplugPlay.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currHeadplugPlay.setBackColorRes(R.color.background_light);
                }
            }
        });
        currHeadplugPause.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ConfigManager.getInstance().setAutoStop(isChecked);
                if (isChecked) {
                    currHeadplugPause.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
                } else {
                    currHeadplugPause.setBackColorRes(R.color.background_light);
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        currPlayMode.setText(getPlayMode(ConfigManager.getInstance().getStudyMode()));
        currNextMode.setText(getNextMode(ConfigManager.getInstance().getStudyPlayMode()));
        currDownLoad.setText(getDownload(ConfigManager.getInstance().getDownloadMode()));
        currAutoRound.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isAutoRound());
        currHeadplugPlay.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isAutoPlay());
        currHeadplugPause.setCheckedImmediatelyNoEvent(ConfigManager.getInstance().isAutoStop());
        if (currAutoRound.isChecked()) {
            currAutoRound.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currAutoRound.setBackColorRes(R.color.background_light);
        }
        if (currHeadplugPlay.isChecked()) {
            currHeadplugPlay.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currHeadplugPlay.setBackColorRes(R.color.background_light);
        }
        if (currHeadplugPause.isChecked()) {
            currHeadplugPause.setBackColorRes(GetAppColor.getInstance().getAppColorRes());
        } else {
            currHeadplugPause.setBackColorRes(R.color.background_light);
        }
        title.setText(R.string.setting_study_set);
    }

    @Override
    public void onClick(View v) {
        if (INoDoubleClick.isFastDoubleClick()) {
            return;
        }
        switch (v.getId()) {
            case R.id.study_set_playmode:
            case R.id.study_set_playmode_current:
                popPlayModeDialog();
                break;
            case R.id.study_set_nextmode:
            case R.id.study_set_nextmode_current:
                popNextModeDialog();
                break;
            case R.id.study_set_download:
            case R.id.study_set_download_current:
                popDownloadDialog();
                break;
            case R.id.study_set_auto_round:
                currAutoRound.setChecked(!currAutoRound.isChecked());
                ConfigManager.getInstance().setAutoRound(currAutoRound.isChecked());
                break;
            case R.id.study_set_headplug_play:
                currHeadplugPlay.setChecked(!currHeadplugPlay.isChecked());
                ConfigManager.getInstance().setAutoPlay(currHeadplugPlay.isChecked());
                break;
            case R.id.study_set_headplug_pause:
                currHeadplugPause.setChecked(!currHeadplugPause.isChecked());
                ConfigManager.getInstance().setAutoStop(currHeadplugPause.isChecked());
                break;
            case R.id.study_set_original_size:
                startActivity(new Intent(context, OriginalSizeActivity.class));
                break;
            case R.id.study_set_media_button:
                startActivity(new Intent(context, MediaButtonControlActivity.class));
                break;
        }
    }

    private void popPlayModeDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_playmode);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.type)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != ConfigManager.getInstance().getStudyMode()) {
                    ConfigManager.getInstance().setStudyMode(position);
                    currPlayMode.setText(getPlayMode(position));
                }
                groupDialog.dismiss();
            }
        });
        adapter.setSelected(ConfigManager.getInstance().getStudyMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }

    private void popNextModeDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_nextmode);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.mode)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (ConfigManager.getInstance().getStudyPlayMode() != position) {
                    ConfigManager.getInstance().setStudyPlayMode(position);
                    currNextMode.setText(getNextMode(position));
                }
                groupDialog.dismiss();
            }
        });
        adapter.setSelected(ConfigManager.getInstance().getStudyPlayMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                groupDialog.dismiss();
            }
        });
        groupDialog.show();
    }


    private void popDownloadDialog() {
        final MyMaterialDialog groupDialog = new MyMaterialDialog(context);
        groupDialog.setTitle(R.string.study_set_download);
        View root = View.inflate(context, R.layout.recycleview, null);
        RecyclerView languageList = (RecyclerView) root.findViewById(R.id.listview);
        MaterialDialogAdapter adapter = new MaterialDialogAdapter(context, Arrays.asList(context.getResources().getStringArray(R.array.download)));
        adapter.setItemClickListener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != ConfigManager.getInstance().getDownloadMode()) {
                    ConfigManager.getInstance().setDownloadMode(position);
                    currDownLoad.setText(getDownload(position));
                }
                groupDialog.dismiss();
            }
        });
        adapter.setSelected(ConfigManager.getInstance().getDownloadMode());
        languageList.setLayoutManager(new MyLinearLayoutManager(context));
        languageList.addItemDecoration(new DividerItemDecoration());
        languageList.setAdapter(adapter);
        groupDialog.setContentView(root);
        groupDialog.setPositiveButton(R.string.app_cancel, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
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
