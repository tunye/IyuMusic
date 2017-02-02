package com.iyuba.music.activity.study;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.apprequest.RecommendSongRequest;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.nineoldandroids.animation.Animator;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by 10202 on 2015/11/20.
 */
public class RecommendSongActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private MaterialEditText recommendTitle, recommendSinger;
    private View mainContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recommend_song);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        mainContent = findViewById(R.id.recommend_main);
        recommendTitle = (MaterialEditText) findViewById(R.id.recommend_title);
        recommendSinger = (MaterialEditText) findViewById(R.id.recommend_singer);
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AccountManager.INSTANCE.checkUserLogin()) {
                    submit();
                } else {
                    CustomDialog.showLoginDialog(context);
                }
            }
        });
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.app_submit);
        title.setText(R.string.study_recommend_title);
    }

    private void submit() {
        if (TextUtils.isEmpty(recommendTitle.getText().toString())) {
            YoYo.with(Techniques.Shake).duration(500).playOn(recommendTitle);
        } else if (TextUtils.isEmpty(recommendSinger.getText().toString())) {
            YoYo.with(Techniques.Shake).duration(500).playOn(recommendSinger);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
            }
            CustomToast.INSTANCE.showToast(R.string.study_recommend_on_way);
            String uid = AccountManager.INSTANCE.getUserId();
            RecommendSongRequest.exeRequest(RecommendSongRequest.generateUrl(uid, recommendTitle.getEditableText().toString()
                    , recommendSinger.getEditableText().toString()), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void response(Object object) {
                    String result = (String) object;
                    if ("1".equals(result)) {
                        handler.sendEmptyMessage(0);
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.study_recommend_fail);
                    }
                }
            });
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<RecommendSongActivity> {
        @Override
        public void handleMessageByRef(final RecommendSongActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    YoYo.with(Techniques.ZoomOutUp).duration(1200).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            CustomToast.INSTANCE.showToast(R.string.study_recommend_success);
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
                    }).playOn(activity.mainContent);
                    break;
                case 2:
                    activity.finish();
                    break;
            }
        }
    }
}
