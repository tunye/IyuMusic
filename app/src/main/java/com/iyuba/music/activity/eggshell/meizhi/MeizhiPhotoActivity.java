package com.iyuba.music.activity.eggshell.meizhi;

/**
 * Created by 10202 on 2016/4/21.
 */

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.umeng.analytics.MobclickAgent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MeizhiPhotoActivity extends AppCompatActivity {
    private static String EXTRA_IAMGE_URL = "url";
    protected Context context;
    private PhotoView photoView;
    private ContextMenu menu;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.meizhi_photo);
        context = this;
        initWidget();
        setListener();
        Intent intent = getIntent();
        url = intent.getStringExtra(MeizhiPhotoActivity.EXTRA_IAMGE_URL);
        Glide.with(this).load(url).crossFade().into(photoView);
        ((MusicApplication) getApplication()).pushActivity(this);
    }

    protected void initWidget() {
        photoView = (PhotoView) findViewById(R.id.iv_girl);
        menu = new ContextMenu(context);
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.photo_download));
        menu.setInfo(list, new IOperationResultInt() {
            @Override
            public void performance(int index) {
                if (index == 0) {
                    Glide.with(context).load(url).asBitmap().into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                            try {
                                saveFile(resource, Calendar.getInstance().getTimeInMillis() + ".jpg");
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }
        });
    }

    protected void setListener() {
        photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                finish();
            }
        });
        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                menu.show();
                return false;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ((MusicApplication) getApplication()).popActivity(this);
    }

    private void saveFile(Bitmap bm, String fileName) throws IOException {
        File foder = new File(ConstantManager.instance.getImgFile());
        if (!foder.exists()) {
            foder.mkdirs();
        }
        File myCaptureFile = new File(ConstantManager.instance.getImgFile(), fileName);
        if (!myCaptureFile.exists()) {
            myCaptureFile.createNewFile();

        }
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        bos.flush();
        bos.close();
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(myCaptureFile);
        intent.setData(uri);
        context.sendBroadcast(intent);
        CustomToast.INSTANCE.showToast(R.string.photo_downloaded);
    }

    @Override
    public void onBackPressed() {
        if (menu.isShown()) {
            menu.dismiss();
        } else {
            super.onBackPressed();
        }
    }
}
