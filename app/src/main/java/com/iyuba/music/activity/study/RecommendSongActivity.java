package com.iyuba.music.activity.study;

import android.animation.Animator;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

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
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.apprequest.RecommendSongRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.animator.SimpleAnimatorListener;
import com.iyuba.music.widget.dialog.CustomDialog;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by 10202 on 2015/11/20.
 */
public class RecommendSongActivity extends BaseActivity {
    private MaterialEditText recommendTitle, recommendSinger;
    private View mainContent;

    @Override
    public int getLayoutId() {
        return R.layout.recommend_song;
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        mainContent = findViewById(R.id.recommend_main);
        recommendTitle = findViewById(R.id.recommend_title);
        recommendSinger = findViewById(R.id.recommend_singer);
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (AccountManager.getInstance().checkUserLogin()) {
                    submit();
                } else {
                    CustomDialog.showLoginDialog(context, true, new IOperationFinish() {
                        @Override
                        public void finish() {
                            submit();
                        }
                    });
                }
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        enableToolbarOper(R.string.app_submit);
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
            CustomToast.getInstance().showToast(R.string.study_recommend_on_way);
            String uid = AccountManager.getInstance().getUserId();
            RequestClient.requestAsync(new RecommendSongRequest(uid, recommendTitle.getEditableText().toString(),
                    recommendSinger.getEditableText().toString()), new SimpleRequestCallBack<BaseApiEntity<String>>() {
                @Override
                public void onSuccess(BaseApiEntity<String> s) {
                    YoYo.with(Techniques.ZoomOutUp).interpolate(new AccelerateDecelerateInterpolator()).duration(1200).withListener(new SimpleAnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            CustomToast.getInstance().showToast(R.string.study_recommend_success);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            ThreadUtils.postOnUiThreadDelay(new Runnable() {
                                @Override
                                public void run() {
                                    RecommendSongActivity.this.finish();
                                }
                            }, 300);
                        }
                    }).playOn(mainContent);
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            });
        }
    }
}