package com.iyuba.music.activity.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

import com.flyco.roundview.RoundTextView;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.yongchun.library.utils.FileUtils;
import com.yongchun.library.view.ImageCropActivity;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2016/2/18.
 */
public class ChangePhotoActivity extends BaseActivity {
    private String imgPath;
    private CircleImageView photo;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    ImageUtil.clearImageAllCache(context);
                    CustomToast.INSTANCE.showToast(R.string.changephoto_success);
                    handler.sendEmptyMessageDelayed(1, 1000);
                    break;
                case 1:
                    ImageUtil.loadAvatar(AccountManager.instance.getUserId(), photo);
                    break;
            }
            return false;
        }
    });
    private RoundTextView change;
    private ContextMenu menu;
    View.OnClickListener ocl = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            menu.show();
        }
    };
    private View root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_photo);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        root = findViewById(R.id.root);
        photo = (CircleImageView) findViewById(R.id.photo);
        change = (RoundTextView) findViewById(R.id.change);
        menu = new ContextMenu(context);
        initContextMunu();
    }

    @Override
    protected void setListener() {
        super.setListener();
        photo.setOnClickListener(ocl);
        change.setOnClickListener(ocl);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        title.setText(R.string.changephoto_title);
        ImageUtil.loadAvatar(AccountManager.instance.getUserId(), photo);
    }

    private void initContextMunu() {
        ArrayList<String> list = new ArrayList<>();
        list.add(context.getString(R.string.changephoto_camera));
        list.add(context.getString(R.string.changephoto_gallery));
        list.add(context.getString(R.string.changephoto_see));
        menu.setInfo(list, new IOperationResultInt() {
            @Override
            public void performance(int index) {
                if (index == 0) {
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        File cameraFile = FileUtils.createCameraFile(context);
                        imgPath = cameraFile.getAbsolutePath();
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(cameraFile));
                        startActivityForResult(cameraIntent, ImageSelectorActivity.REQUEST_CAMERA);
                    }
                } else if (index == 1) {
                    ImageSelectorActivity.start(ChangePhotoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false, true, true);
                } else if (index == 2) {
                    Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                    intent.putExtra("url", "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=" + AccountManager.instance.getUserId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (menu.isShown()) {
            menu.dismiss();
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ImageSelectorActivity.REQUEST_CAMERA:
                ImageCropActivity.startCrop(this, imgPath);
                break;
            case ImageSelectorActivity.REQUEST_IMAGE:
                if (resultCode == RESULT_OK) {
                    ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                    imgPath = images.get(0);
                    photo.setImageBitmap(getImage());
                    new UploadThread().start();
                }
                break;
            case ImageCropActivity.REQUEST_CROP:
                imgPath = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                photo.setImageBitmap(getImage());
                new UploadThread().start();
                break;
        }
    }


    private Bitmap getImage() {
        try {
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            return BitmapFactory.decodeFile(imgPath, op);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void showSnackBar() {
        Snackbar snackbar = Snackbar.make(root, R.string.changephoto_intro, Snackbar.LENGTH_LONG).setAction(R.string.credit_check, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreditActivity.class);
                startActivity(intent);
            }
        });
        ((TextView) snackbar.getView().findViewById(R.id.snackbar_text)).setTextColor(Color.WHITE);
        snackbar.show();
    }

    class UploadThread extends Thread {
        @Override
        public void run() {

            super.run();
            UploadFile.postImg("http://api.iyuba.com.cn/v2/avatar?uid="
                    + AccountManager.instance.getUserId(), new File(imgPath), new IOperationResult() {
                @Override
                public void success(Object object) {
                    handler.sendEmptyMessage(0);
                    showSnackBar();
                }

                @Override
                public void fail(Object object) {
                    CustomToast.INSTANCE.showToast(R.string.changephoto_fail);
                }
            });
        }
    }
}
