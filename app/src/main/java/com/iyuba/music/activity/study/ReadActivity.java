package com.iyuba.music.activity.study;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.fragmentAdapter.ReadFragmentAdapter;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.widget.imageview.TabIndicator;

import java.util.ArrayList;
import java.util.Arrays;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2016/3/21.
 */
public class ReadActivity extends BaseActivity {
    private boolean needRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.read);
        context = this;
        if (((MusicApplication) getApplication()).getPlayerService().getPlayer().isPlaying()) {
            needRestart = true;
            sendBroadcast(new Intent("iyumusic.pause"));
        } else {
            needRestart = false;
        }
        initWidget();
        setListener();
        changeUIByPara();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        ArrayList<String> tabTitle = new ArrayList<>();
        tabTitle.addAll(Arrays.asList(context.getResources().getStringArray(R.array.read_tab_title)));
        ViewPager viewPager = (ViewPager) findViewById(R.id.read_main);
        TabIndicator viewPagerIndicator = (TabIndicator) findViewById(R.id.tab_indicator);
        viewPager.setAdapter(new ReadFragmentAdapter(getSupportFragmentManager()));
        viewPagerIndicator.setTabItemTitles(tabTitle);
        viewPagerIndicator.setViewPager(viewPager, 0);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(StudyManager.instance.getCurArticle().getTitle());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (needRestart) {
            sendBroadcast(new Intent("iyumusic.pause"));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == permissions.length
                && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

        } else {
            final MaterialDialog materialDialog = new MaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(ReadActivity.this, new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            100);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }
}
