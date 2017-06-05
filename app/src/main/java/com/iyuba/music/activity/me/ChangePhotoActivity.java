package com.iyuba.music.activity.me;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.buaa.ct.imageselector.view.ImageCropActivity;
import com.buaa.ct.imageselector.view.ImageSelectorActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.meizhi.MeizhiPhotoActivity;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.ImageUtil;
import com.iyuba.music.util.ThreadPoolUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2016/2/18.
 */
public class ChangePhotoActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private String imgPath;
    private CircleImageView photo;
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        root = findViewById(R.id.root);
        photo = (CircleImageView) findViewById(R.id.photo);
        change = (RoundTextView) findViewById(R.id.change);
        AddRippleEffect.addRippleEffect(change);
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
        ImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), photo);
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
                    if (ContextCompat.checkSelfPermission(ChangePhotoActivity.this, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(ChangePhotoActivity.this, new String[]{Manifest.permission.CAMERA}, 101);
                    } else {
                        imgPath = ImageSelectorActivity.startCameraDirect(context);
                    }
                } else if (index == 1) {
                    ImageSelectorActivity.start(ChangePhotoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false, true, true);
                } else if (index == 2) {
                    Intent intent = new Intent(context, MeizhiPhotoActivity.class);
                    intent.putExtra("url", "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=" + AccountManager.getInstance().getUserId());
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
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(ChangePhotoActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            100);
                    materialDialog.dismiss();
                }
            });
        } else if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.storage_permission);
            materialDialog.setMessage(R.string.storage_permission_content);
            materialDialog.setPositiveButton(R.string.app_sure, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityCompat.requestPermissions(ChangePhotoActivity.this, new String[]{Manifest.permission.CAMERA},
                            101);
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }else if (requestCode == 101 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            imgPath = ImageSelectorActivity.startCameraDirect(context);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageSelectorActivity.REQUEST_CAMERA:
                    ImageCropActivity.startCrop(this, imgPath);
                    break;
                case ImageSelectorActivity.REQUEST_IMAGE:
                    if (resultCode == RESULT_OK) {
                        ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                        imgPath = images.get(0);
                        photo.setImageBitmap(getImage());
                        startUploadThread();
                    }
                    break;
                case ImageCropActivity.REQUEST_CROP:
                    imgPath = data.getStringExtra(ImageCropActivity.OUTPUT_PATH);
                    photo.setImageBitmap(getImage());
                    startUploadThread();
                    break;
            }
        } else if (resultCode == RESULT_CANCELED) {
            if (requestCode == ImageSelectorActivity.REQUEST_CAMERA) {
                CustomToast.getInstance().showToast(R.string.changephoto_camera_cancel);
            }
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

    private void startUploadThread() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                UploadFile.postImg("http://api.iyuba.com.cn/v2/avatar?uid="
                        + AccountManager.getInstance().getUserId(), new File(imgPath), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        handler.sendEmptyMessage(0);
                    }

                    @Override
                    public void fail(Object object) {
                        handler.sendEmptyMessage(1);
                    }
                });
            }
        });
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<ChangePhotoActivity> {
        @Override
        public void handleMessageByRef(final ChangePhotoActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    CustomToast.getInstance().showToast(R.string.changephoto_success);
                    CustomSnackBar.make(activity.root, activity.getString(R.string.changephoto_intro)).info(activity.getString(R.string.credit_check), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            activity.startActivity(new Intent(activity, CreditActivity.class));
                        }
                    });
                    ConfigManager.getInstance().setUserPhotoTimeStamp();
                    break;
                case 1:
                    CustomToast.getInstance().showToast(R.string.changephoto_fail);
                    break;
            }
        }
    }
}
