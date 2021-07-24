package com.iyuba.music.activity.eggshell.meizhi;

/**
 * Created by 10202 on 2016/4/21.
 */

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.imageselector.photo.PhotoView;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;

import java.io.File;

public class LocalPhotoActivity extends BaseActivity {
    private static String EXTRA_IAMGE_URL = "url";
    private PhotoView photoView;

    @Override
    public int getLayoutId() {
        return R.layout.local_photo;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        photoView = findViewById(R.id.iv_girl);
    }

    @Override
    public void setListener() {
        super.setListener();
        photoView.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                finish();
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                Intent intent = new Intent();
                setResult(1, intent);
                LocalPhotoActivity.this.finish();
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        enableToolbarOper(R.string.file_delete);
        File target = new File(getIntent().getStringExtra(LocalPhotoActivity.EXTRA_IAMGE_URL));
        photoView.setImageURI(Uri.fromFile(target));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(0, intent);
        super.onBackPressed();
    }
}
