package com.iyuba.music.activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.apprequest.FeedbackRequest;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.nineoldandroids.animation.Animator;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by 10202 on 2015/11/20.
 */
public class FeedbackActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private MaterialEditText contact, content;
    private boolean regex;
    private View mainContent;
    private Dialog waitingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        mainContent = findViewById(R.id.feedback_main);
        content = (MaterialEditText) findViewById(R.id.feedback_content);
        contact = (MaterialEditText) findViewById(R.id.feedback_contact);
        waitingDialog = new WaitingDialog.Builder(context).setMessage(context.getString(R.string.feedback_on_way)).create();
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
    protected void changeUIByPara() {
        super.changeUIByPara();
        if (AccountManager.instance.checkUserLogin()) {
            UserInfo userInfo = AccountManager.instance.getUserInfo();
            if (!TextUtils.isEmpty(userInfo.getUserEmail())) {
                contact.setText(userInfo.getUserEmail());
            }
        }
        toolbarOper.setText(R.string.submit);
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
            YoYo.with(Techniques.Shake).duration(500).playOn(contact);
        } else if (TextUtils.isEmpty(contentString)) {
            YoYo.with(Techniques.Shake).duration(500).playOn(content);
        } else {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            waitingDialog.show();
            String uid;
            if (AccountManager.instance.checkUserLogin()) {
                uid = AccountManager.instance.getUserId();
            } else {
                uid = "0";
            }
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
            FeedbackRequest.getInstance().exeRequest(FeedbackRequest.getInstance().generateUrl(uid,
                    ParameterUrl.encode(contentString), contact.getEditableText().toString()), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void onServerError(String msg) {
                    CustomToast.INSTANCE.showToast(msg);
                    handler.sendEmptyMessage(1);
                }

                @Override
                public void response(Object object) {
                    handler.sendEmptyMessage(1);
                    String result = (String) object;
                    if ("OK".equals(result)) {
                        handler.sendEmptyMessage(0);
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.feedback_fail);
                    }
                }
            });
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<FeedbackActivity> {
        @Override
        public void handleMessageByRef(final FeedbackActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    YoYo.with(Techniques.ZoomOutUp).duration(1200).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            CustomToast.INSTANCE.showToast(R.string.feedback_success);
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
                case 1:
                    activity.waitingDialog.dismiss();
                    break;
                case 2:
                    activity.finish();
                    break;
            }
        }
    }
}
