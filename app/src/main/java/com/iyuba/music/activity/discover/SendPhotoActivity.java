package com.iyuba.music.activity.discover;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.buaa.ct.comment.EmojiView;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.util.ThreadUtils;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.imageselector.view.ImageSelectorActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.meizhi.LocalPhotoActivity;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.merequest.WriteStateRequest;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.animator.SimpleAnimatorListener;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.MyMaterialDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by 10202 on 2015/11/20.
 */
public class SendPhotoActivity extends BaseActivity {
    private MaterialEditText content;
    private IyubaDialog waittingDialog;
    private List<String> images;
    private EmojiView emojiView;
    private ImageView photo;
    private View photoContent;

    @Override
    public int getLayoutId() {
        return R.layout.circle_photo;
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        images = new ArrayList<>();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        photoContent = findViewById(R.id.photo_content);
        content = findViewById(R.id.feedback_content);
        photo = findViewById(R.id.state_image);
        emojiView = findViewById(R.id.emoji);
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.photo_on_way));
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                submit();
            }
        });
        photo.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (images.size() == 0) {
                    ImageSelectorActivity.start(SendPhotoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, false);
                } else {
                    Intent intent = new Intent(context, LocalPhotoActivity.class);
                    intent.putExtra("url", ConstantManager.envir + "/temp.jpg");
                    startActivityForResult(intent, 101);
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        enableToolbarOper(R.string.state_send);
        title.setText(R.string.photo_title);
        emojiView.setmEtText(content);
        photo.setImageResource(R.drawable.circle_photo_add);
    }

    private void submit() {
        String contentString = content.getEditableText().toString();
        if (TextUtils.isEmpty(contentString)) {
            YoYo.with(Techniques.Shake).duration(500).playOn(content);
        } else if (!content.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(content);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
            waittingDialog.show();
            if (images.size() == 0) {
                RequestClient.requestAsync(new WriteStateRequest(AccountManager.getInstance().getUserId(), AccountManager.getInstance().getUserInfo().getUsername(),
                        content.getEditableText().toString()), new SimpleRequestCallBack<BaseApiEntity<String>>() {
                    @Override
                    public void onSuccess(BaseApiEntity<String> result) {
                        waittingDialog.dismiss();
                        if (BaseApiEntity.isSuccess(result)) {
                            sendFriendCircleSuccess();
                        } else {
                            CustomToast.getInstance().showToast(R.string.photo_fail);
                        }
                    }

                    @Override
                    public void onError(ErrorInfoWrapper errorInfoWrapper) {
                        waittingDialog.dismiss();
                        CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                    }
                });
            } else {
                ThreadPoolUtil.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        UploadFile.postImg("http://api.iyuba.com.cn/v2/avatar/photo?uid="
                                        + AccountManager.getInstance().getUserId() + "&iyu_describe=" + ParameterUrl.encode(ParameterUrl.encode(content.getEditableText().toString())),
                                new File(images.get(0)), new IOperationResult() {
                                    @Override
                                    public void success(Object object) {
                                        waittingDialog.dismiss();
                                        sendFriendCircleSuccess();
                                    }

                                    @Override
                                    public void fail(Object object) {
                                        waittingDialog.dismiss();
                                        CustomToast.getInstance().showToast(R.string.photo_fail);
                                    }
                                });
                    }
                });
            }
        }
    }

    private Bitmap saveImage(String path, String bitmapPath) {
        File picture = new File(path);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(picture);
            BitmapFactory.Options op = new BitmapFactory.Options();
            op.inSampleSize = 2;
            op.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap temp = BitmapFactory.decodeFile(bitmapPath, op);
            temp.compress(Bitmap.CompressFormat.JPEG, 60, out);
            out.flush();
            out.close();
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            photo.setImageBitmap(saveImage(ConstantManager.envir + "/temp.jpg", images.get(0)));
        } else if (resultCode == -2) {
            CustomToast.getInstance().showToast(R.string.storage_permission_cancel);
        } else if (requestCode == 101 && resultCode == 1) {//删除
            images = new ArrayList<>();
            photo.setImageResource(R.drawable.circle_photo_add);
            new File(ConstantManager.envir + "/temp.jpg").delete();
        }
    }

    @Override
    public void onBackPressed() {
        if (emojiView.onBackPressed()) {
            final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
            materialDialog.setTitle(R.string.photo_title);
            materialDialog.setMessage(R.string.photo_exit);
            materialDialog.setPositiveButton(R.string.photo_exit_sure, new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    materialDialog.dismiss();
                    SendPhotoActivity.this.finish();
                }
            });
            materialDialog.setNegativeButton(R.string.app_cancel, new INoDoubleClick() {
                @Override
                public void activeClick(View view) {
                    materialDialog.dismiss();
                }
            });
            materialDialog.show();
        }
    }

    private void sendFriendCircleSuccess() {
        YoYo.with(Techniques.ZoomOutUp).interpolate(new AccelerateDecelerateInterpolator()).duration(1200).withListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                CustomToast.getInstance().showToast(R.string.photo_success);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                ThreadUtils.postOnUiThreadDelay(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        SendPhotoActivity.this.setResult(1, intent);
                        SendPhotoActivity.this.finish();
                    }
                }, 300);
            }
        }).playOn(photoContent);
    }
}
