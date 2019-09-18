package com.iyuba.music.activity.me;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.network.NetWorkState;
import com.buaa.ct.core.util.AddRippleEffect;
import com.buaa.ct.core.util.PermissionPool;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.image.CircleImageView;
import com.buaa.ct.imageselector.view.ImageCropActivity;
import com.buaa.ct.imageselector.view.ImageSelectorActivity;
import com.buaa.ct.imageselector.view.OnlyPreviewActivity;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IOperationResultInt;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.util.AppImageUtil;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomSnackBar;
import com.iyuba.music.widget.dialog.ContextMenu;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.roundview.RoundTextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2016/2/18.
 */
public class ChangePhotoActivity extends BaseActivity {
    private String imgPath;
    private CircleImageView photo;
    private RoundTextView change;
    private ContextMenu menu;
    View.OnClickListener ocl = new INoDoubleClick() {
        @Override
        public void activeClick(View view) {
            menu.show();
        }
    };
    private View root;

    @Override
    public int getLayoutId() {
        return R.layout.change_photo;
    }

    @Override
    public void afterSetLayout() {
        super.afterSetLayout();
        permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    @Override
    public void initWidget() {
        super.initWidget();
        root = findViewById(R.id.root);
        photo = findViewById(R.id.photo);
        change = findViewById(R.id.change);
        AddRippleEffect.addRippleEffect(change);
        menu = new ContextMenu(context);
        initContextMunu();
    }

    @Override
    public void setListener() {
        super.setListener();
        photo.setOnClickListener(ocl);
        change.setOnClickListener(ocl);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        title.setText(R.string.changephoto_title);
        AppImageUtil.loadAvatar(AccountManager.getInstance().getUserId(), photo);
    }

    private void initContextMunu() {
        List<String> list = new ArrayList<>();
        list.add(context.getString(R.string.changephoto_camera));
        list.add(context.getString(R.string.changephoto_gallery));
        list.add(context.getString(R.string.changephoto_see));
        menu.setInfo(list, new IOperationResultInt() {
            @Override
            public void performance(int index) {
                if (index == 0) {
                    permissionDispose(PermissionPool.CAMERA, Manifest.permission.CAMERA);
                } else if (index == 1) {
                    ImageSelectorActivity.start(ChangePhotoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, false, true, true);
                } else if (index == 2) {
                    OnlyPreviewActivity.startPreview(context, "http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=" + AccountManager.getInstance().getUserId());
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
    public void onAccreditSucceed(int requestCode) {
        super.onAccreditSucceed(requestCode);
        if (requestCode == PermissionPool.CAMERA) {
            imgPath = ImageSelectorActivity.startCameraDirect(context);
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
                if (requestCode == PermissionPool.CAMERA) {
                    permissionDispose(PermissionPool.CAMERA, Manifest.permission.CAMERA);
                } else {
                    permissionDispose(PermissionPool.WRITE_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ImageSelectorActivity.REQUEST_CAMERA:
                    ImageCropActivity.startCrop(this, imgPath);
                    break;
                case ImageSelectorActivity.REQUEST_IMAGE:
                    ArrayList<String> images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
                    imgPath = images.get(0);
                    photo.setImageBitmap(getImage());
                    startUploadThread();
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
        if (!NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            CustomToast.getInstance().showToast(R.string.net_no_net);
            return;
        }
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                UploadFile.postImg("http://api.iyuba.com.cn/v2/avatar?uid="
                        + AccountManager.getInstance().getUserId(), new File(imgPath), new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        CustomToast.getInstance().showToast(R.string.changephoto_success);
                        CustomSnackBar.make(root, getString(R.string.changephoto_intro)).info(getString(R.string.credit_check), new INoDoubleClick() {
                            @Override
                            public void activeClick(View view) {
                                startActivity(new Intent(context, CreditActivity.class));
                            }
                        });
                        ConfigManager.getInstance().setUserPhotoTimeStamp();
                    }

                    @Override
                    public void fail(Object object) {
                        CustomToast.getInstance().showToast(R.string.changephoto_fail);
                    }
                });
            }
        });
    }
}
