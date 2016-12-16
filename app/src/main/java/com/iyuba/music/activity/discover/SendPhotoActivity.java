package com.iyuba.music.activity.discover;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chaowen.commentlibrary.ContextManager;
import com.chaowen.commentlibrary.EmojiView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.activity.eggshell.meizhi.LocalPhotoActivity;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.UploadFile;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.nineoldandroids.animation.Animator;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.yongchun.library.view.ImageSelectorActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;


/**
 * Created by 10202 on 2015/11/20.
 */
public class SendPhotoActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private MaterialEditText content;
    private Dialog waittingDialog;
    private ArrayList<String> images;
    private EmojiView emojiView;
    private ImageView photo;
    private View photoContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.circle_photo);
        context = this;
        images = new ArrayList<>();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        photoContent = findViewById(R.id.photo_content);
        content = (MaterialEditText) findViewById(R.id.feedback_content);
        photo = (ImageView) findViewById(R.id.state_image);
        emojiView = (EmojiView) findViewById(R.id.emoji);
        waittingDialog = new WaitingDialog.Builder(context).setMessage(context.getString(R.string.photo_on_way)).create();
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (images.size() == 0) {
                    ImageSelectorActivity.start(SendPhotoActivity.this, 1, ImageSelectorActivity.MODE_SINGLE, true, true, false);
                } else {
                    Intent intent = new Intent(context, LocalPhotoActivity.class);
                    intent.putExtra("url", ConstantManager.instance.getEnvir() + "/temp.jpg");
                    startActivityForResult(intent, 101);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.state_send);
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
        } else if (images.size() == 0) {
            YoYo.with(Techniques.Shake).duration(500).playOn(photo);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(content.getWindowToken(), 0);
            waittingDialog.show();
            new UploadThread().start();
        }
    }

    private Bitmap saveImage(String path, String bitmapPath) {
        File picture = new File(path);
        try {
            FileOutputStream out = new FileOutputStream(picture);
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
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == ImageSelectorActivity.REQUEST_IMAGE) {
            images = (ArrayList<String>) data.getSerializableExtra(ImageSelectorActivity.REQUEST_OUTPUT);
            photo.setImageBitmap(saveImage(ConstantManager.instance.getEnvir() + "/temp.jpg", images.get(0)));
        } else if (requestCode == 101 && resultCode == 1) {//删除
            images = new ArrayList<>();
            photo.setImageResource(R.drawable.circle_photo_add);
            new File(ConstantManager.instance.getEnvir() + "/temp.jpg").delete();
        } else if (requestCode == 101 && resultCode == 0) {//未删除
        }
    }

    @Override
    public void onBackPressed() {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(R.string.photo_title);
        materialDialog.setMessage(R.string.photo_exit);
        materialDialog.setPositiveButton(R.string.photo_exit_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
                SendPhotoActivity.this.finish();
            }
        });
        materialDialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ContextManager.destory();
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<SendPhotoActivity> {
        @Override
        public void handleMessageByRef(final SendPhotoActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    YoYo.with(Techniques.ZoomOutUp).duration(1200).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            CustomToast.INSTANCE.showToast(R.string.photo_success);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            activity.handler.sendEmptyMessageDelayed(2, 300);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(activity.photoContent);
                    break;
                case 1:
                    activity.waittingDialog.dismiss();
                    break;
                case 2:
                    Intent intent = new Intent();
                    activity.setResult(1, intent);
                    activity.finish();
                    break;
            }
        }
    }

    class UploadThread extends Thread {

        @Override
        public void run() {
            super.run();
            UploadFile.postImg("http://api.iyuba.com.cn/v2/avatar/photo?uid="
                            + AccountManager.instance.getUserId() + "&iyu_describe=" + ParameterUrl.encode(ParameterUrl.encode(content.getEditableText().toString())),
                    new File(images.get(0)), new IOperationResult() {
                        @Override
                        public void success(Object object) {
                            handler.sendEmptyMessage(0);
                            handler.sendEmptyMessage(1);
                        }

                        @Override
                        public void fail(Object object) {
                            handler.sendEmptyMessage(1);
                            CustomToast.INSTANCE.showToast(R.string.photo_fail);
                        }
                    });
        }
    }
}
