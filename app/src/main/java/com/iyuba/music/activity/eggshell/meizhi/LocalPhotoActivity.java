package com.iyuba.music.activity.eggshell.meizhi;

/**
 * Created by 10202 on 2016/4/21.
 */

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class LocalPhotoActivity extends BaseActivity {
    private static String EXTRA_IAMGE_URL = "url";
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.local_photo);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
        photoView.setImageURI(Uri.fromFile(new File(getIntent().getStringExtra(LocalPhotoActivity.EXTRA_IAMGE_URL))));
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        photoView = (PhotoView) findViewById(R.id.iv_girl);
    }

    @Override
    protected void setListener() {
        super.setListener();
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                setResult(1, intent);
                LocalPhotoActivity.this.finish();
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.file_delete);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(0, intent);
        super.onBackPressed();
    }
}
