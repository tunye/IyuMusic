package com.iyuba.music.activity.me;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.buaa.ct.comment.EmojiView;
import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.ThreadUtils;
import com.buaa.ct.core.view.CustomToast;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.merequest.WriteStateRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.animator.SimpleAnimatorListener;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by 10202 on 2015/11/20.
 */
public class WriteStateActivity extends BaseActivity {
    private MaterialEditText content;
    private IyubaDialog waitingDialog;
    private EmojiView emojiView;

    @Override
    public int getLayoutId() {
        return R.layout.write_state;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        content = findViewById(R.id.feedback_content);
        emojiView = findViewById(R.id.emoji);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.state_on_way));
    }

    @Override
    public void setListener() {
        back.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                finish();
            }
        });
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                submit();
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        toolbarOper.setText(R.string.state_send);
        title.setText(R.string.state_title);
        emojiView.setmEtText(content);
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
            waitingDialog.show();
            String uid = AccountManager.getInstance().getUserId();
            RequestClient.requestAsync(new WriteStateRequest(uid, AccountManager.getInstance().getUserInfo().getUsername(), content.getEditableText().toString()), new SimpleRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    waitingDialog.dismiss();
                    if ("351".equals(result)) {
                        AccountManager.getInstance().getUserInfo().setText(content.getEditableText().toString());
                        YoYo.with(Techniques.ZoomOutUp).interpolate(new AccelerateDecelerateInterpolator()).duration(1200).withListener(new SimpleAnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                CustomToast.getInstance().showToast(R.string.state_modify_success);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ThreadUtils.postOnUiThreadDelay(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent();
                                        setResult(1, intent);
                                        WriteStateActivity.this.finish();
                                    }
                                }, 300);
                            }
                        }).playOn(content);
                    } else {
                        CustomToast.getInstance().showToast(R.string.state_modify_fail);
                    }
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                    waitingDialog.dismiss();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (emojiView.onBackPressed()) {
            super.onBackPressed();
        }
    }
}
