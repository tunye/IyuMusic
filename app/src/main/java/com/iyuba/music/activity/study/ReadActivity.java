package com.iyuba.music.activity.study;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.util.PermissionPool;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragmentAdapter.ReadFragmentAdapter;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.imageview.TabIndicator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by 10202 on 2016/3/21.
 */
public class ReadActivity extends BaseActivity {
    private boolean needRestart;

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        if (((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
            needRestart = true;
            sendBroadcast(new Intent("iyumusic.pause"));
        } else {
            needRestart = false;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.read;
    }

    @Override
    public void afterSetLayout() {
        super.afterSetLayout();
        requestMultiPermission(new int[]{PermissionPool.RECORD_AUDIO, PermissionPool.WRITE_EXTERNAL_STORAGE}, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void initWidget() {
        super.initWidget();
        List<String> tabTitle = new ArrayList<>(Arrays.asList(context.getResources().getStringArray(R.array.read_tab_title)));
        ViewPager viewPager = findViewById(R.id.read_main);
        TabIndicator viewPagerIndicator = findViewById(R.id.tab_indicator);
        viewPager.setAdapter(new ReadFragmentAdapter(getSupportFragmentManager()));
        viewPagerIndicator.setTabItemTitles(tabTitle);
        viewPagerIndicator.setViewPager(viewPager, 0);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(StudyManager.getInstance().getCurArticle().getTitle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needRestart) {
            sendBroadcast(new Intent("iyumusic.pause"));
        }
    }

    @Override
    public void onAccreditFailure(final int requestCode) {
        super.onAccreditFailure(requestCode);
        final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
        materialDialog.setTitle(R.string.storage_permission);
        materialDialog.setMessage(R.string.storage_permission_content);
        materialDialog.setPositiveButton(R.string.app_sure, new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                permissionDispose(requestCode, requestCode == PermissionPool.RECORD_AUDIO ? Manifest.permission.RECORD_AUDIO : Manifest.permission.WRITE_EXTERNAL_STORAGE);
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }
}
