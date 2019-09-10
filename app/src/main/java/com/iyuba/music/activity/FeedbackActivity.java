package com.iyuba.music.activity;

import android.animation.Animator;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.apprequest.FeedbackRequest;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;
import com.iyuba.music.widget.animator.SimpleAnimatorListener;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by 10202 on 2015/11/20.
 */
public class FeedbackActivity extends BaseActivity {
    private MaterialEditText contact, content;
    private boolean regex;
    private View mainContent;
    private IyubaDialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        context = this;
        initWidget();
        setListener();
        onActivityCreated();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        toolbarOper = findViewById(R.id.toolbar_oper);
        mainContent = findViewById(R.id.feedback_main);
        content = findViewById(R.id.feedback_content);
        contact = findViewById(R.id.feedback_contact);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.feedback_on_way));
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void onClick(View view) {
                super.onClick(view);
                submit();
            }
        });
        contact.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                regex = regexContact(s.toString());
            }
        });
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        if (AccountManager.getInstance().checkUserLogin()) {
            UserInfo userInfo = AccountManager.getInstance().getUserInfo();
            if (!TextUtils.isEmpty(userInfo.getUserEmail())) {
                contact.setText(userInfo.getUserEmail());
            }
        }
        toolbarOper.setText(R.string.app_submit);
        title.setText(R.string.feedback_title);
        regex = regexContact(contact.getEditableText().toString());
    }

    private boolean regexContact(String content) {
        if (TextUtils.isEmpty(content)) {
            contact.setError(context.getString(R.string.feedback_contact_empty));
            return false;
        }
        if (content.matches("[0-9]*")) {
            if (content.length() > 11) {
                contact.setError(context.getString(R.string.matches_qq));
                return false;
            } else {
                return true;
            }
        } else {
            String regexPattern = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
            if (content.matches(regexPattern)) {
                return true;
            } else {
                contact.setError(context.getString(R.string.matches_email));
                return false;
            }
        }
    }

    private void submit() {
        String contentString = content.getEditableText().toString();
        if (!regex) {
            YoYo.with(Techniques.Shake).interpolate(new AccelerateDecelerateInterpolator()).duration(500).playOn(contact);
        } else if (TextUtils.isEmpty(contentString)) {
            YoYo.with(Techniques.Shake).interpolate(new AccelerateDecelerateInterpolator()).duration(500).playOn(content);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
            }
            waitingDialog.show();
            String uid = AccountManager.getInstance().getUserId();
            try {
                contentString = contentString + "\nappversion:["
                        + context.getPackageManager().getPackageInfo(context.getPackageName(),
                        PackageManager.GET_CONFIGURATIONS).versionName
                        + "]\nphone:[" + android.os.Build.BRAND
                        + android.os.Build.DEVICE + "]sysversion:["
                        + android.os.Build.VERSION.RELEASE + "]";
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            RequestClient.requestAsync(new FeedbackRequest(uid, ParameterUrl.encode(contentString),
                    contact.getEditableText().toString()), new SimpleRequestCallBack<String>() {
                @Override
                public void onSuccess(String result) {
                    waitingDialog.dismiss();
                    if ("OK".equals(result)) {
                        YoYo.with(Techniques.ZoomOutUp).interpolate(new AccelerateDecelerateInterpolator()).duration(1200).withListener(new SimpleAnimatorListener() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                CustomToast.getInstance().showToast(R.string.feedback_success);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                ThreadUtils.postOnUiThreadDelay(new Runnable() {
                                    @Override
                                    public void run() {
                                        FeedbackActivity.this.finish();
                                    }
                                }, 300);
                            }
                        }).playOn(mainContent);
                    } else {
                        CustomToast.getInstance().showToast(R.string.feedback_fail);
                    }
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    waitingDialog.dismiss();
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            });
        }
    }
}
