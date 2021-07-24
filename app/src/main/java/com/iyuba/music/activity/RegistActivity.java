package com.iyuba.music.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.AddRippleEffect;
import com.buaa.ct.core.view.CustomToast;
import com.buaa.ct.core.view.image.CircleImageView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.CheckPhoneRegisted;
import com.iyuba.music.request.account.RegistByPhoneRequest;
import com.iyuba.music.request.account.RegistRequest;
import com.iyuba.music.util.Utils;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.textview.CustomUrlSpan;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

/**
 * Created by 10202 on 2015/11/24.
 */
public class RegistActivity extends BaseActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private MaterialEditText phone, messageCode, userName, userPwd, userPwd2, email;
    private RoundTextView regist, getMessageCode;
    private CheckBox protocol;
    private View registByPhone, registByEmail;
    private CircleImageView photo;
    private IyubaDialog waittingDialog;
    TextView.OnEditorActionListener editor = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                if (v.getId() == R.id.regist_msg_code) {
                    registByPhone();
                } else if (v.getId() == R.id.regist_repwd) {
                    if (!email.isShown()) {
                        registByPhoneContinue();
                    }
                } else if (v.getId() == R.id.regist_email) {
                    registByEmail();
                }
                return true;
            }
            return false;
        }
    };
    private boolean smsReady;

    @Override
    public int getLayoutId() {
        return R.layout.regist;
    }

    @Override
    public void afterSetLayout() {
        super.afterSetLayout();
        initSMSService();
    }

    @Override
    public void initWidget() {
        super.initWidget();
        photo = findViewById(R.id.regist_photo);
        regist = findViewById(R.id.regist);
        AddRippleEffect.addRippleEffect(regist);
        protocol = findViewById(R.id.regist_protocol_checkbox);
        registByPhone = findViewById(R.id.regist_by_phone);
        registByEmail = findViewById(R.id.regist_by_email);
        getMessageCode = findViewById(R.id.get_msg_code);
        AddRippleEffect.addRippleEffect(getMessageCode);
        phone = findViewById(R.id.regist_phone);
        messageCode = findViewById(R.id.regist_msg_code);
        userName = findViewById(R.id.regist_username);
        userPwd = findViewById(R.id.regist_pwd);
        userPwd2 = findViewById(R.id.regist_repwd);
        email = findViewById(R.id.regist_email);
        waittingDialog = WaitingDialog.create(context, context.getString(R.string.regist_on_way));
    }

    @Override
    public void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (toolbarOper.getText().equals(context.getString(R.string.regist_by_phone))) {
                    enableToolbarOper(context.getString(R.string.regist_by_email));
                    photo.setVisibility(View.VISIBLE);
                    registByPhone.setVisibility(View.VISIBLE);
                    registByEmail.setVisibility(View.GONE);
                } else {
                    photo.setVisibility(View.GONE);
                    enableToolbarOper(context.getString(R.string.regist_by_phone));
                    registByPhone.setVisibility(View.GONE);
                    registByEmail.setVisibility(View.VISIBLE);
                }
            }
        });
        getMessageCode.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (!phone.isCharactersCountValid() || !regexPhone()) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(phone);
                } else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    }
                    RequestClient.requestAsync(new CheckPhoneRegisted(phone.getText().toString()), new SimpleRequestCallBack<BaseApiEntity<Integer>>() {
                        @Override
                        public void onSuccess(BaseApiEntity<Integer> result) {
                            if (BaseApiEntity.isSuccess(result)) {
                                SMSSDK.getVerificationCode("86", phone.getText().toString());
                                handler.obtainMessage(1, 60, 0).sendToTarget();
                            } else {
                                YoYo.with(Techniques.Shake).duration(500).playOn(phone);
                                phone.setError(context.getString(R.string.regist_phone_registed, phone.getText()));
                            }
                        }

                        @Override
                        public void onError(ErrorInfoWrapper errorInfoWrapper) {
                            CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                        }
                    });
                }
            }
        });
        regist.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                if (registByEmail.isShown()) {
                    if (email.isShown()) {
                        registByEmail();
                    } else {
                        registByPhoneContinue();
                    }
                } else if (registByPhone.getVisibility() == View.VISIBLE) {
                    registByPhone();
                }
            }
        });
        userPwd2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                regexUserPwd();
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                regexPhone();
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                regexEmail();
            }
        });
        toolbarOperSub.setOnClickListener(new INoDoubleClick() {
            @Override
            public void activeClick(View view) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", "http://m.iyuba.cn/m_login/inputPhone.jsp");
                intent.putExtra("title", context.getString(R.string.regist_title));
                startActivity(intent);
            }
        });
        userPwd2.setOnEditorActionListener(editor);
        email.setOnEditorActionListener(editor);
        messageCode.setOnEditorActionListener(editor);
    }

    @Override
    public void onActivityCreated() {
        super.onActivityCreated();
        interceptHyperLink(protocol);
        registByPhone.setVisibility(View.VISIBLE);
        registByEmail.setVisibility(View.GONE);
        title.setText(R.string.regist_title);
    }

    public void onActivityResumed() {
        if (registByEmail.getVisibility() == View.VISIBLE) {
            photo.setVisibility(View.GONE);
            if (email.getVisibility() == View.VISIBLE) {
                regist.setText(context.getString(R.string.regist_title));
                enableToolbarOper(context.getString(R.string.regist_by_phone));
                userPwd2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                title.setText(R.string.regist_title);
            } else {
                regist.setText(context.getString(R.string.regist_phone_part_two));
                enableToolbarOper(context.getString(R.string.regist_by_email));
                userPwd2.setImeOptions(EditorInfo.IME_ACTION_SEND);
                title.setText(R.string.regist_phone_part_two);
            }
        } else if (registByPhone.getVisibility() == View.VISIBLE) {
            enableToolbarOper(context.getString(R.string.regist_by_email));
            regist.setText(context.getString(R.string.regist_title));
            photo.setVisibility(View.VISIBLE);
            title.setText(R.string.regist_title);
        }
        enableToolbarOperSub(R.string.regist_oper_sub);
    }

    @Override
    public void onBackPressed() {
        if (registByEmail.getVisibility() == View.VISIBLE) {
            enableToolbarOper(context.getString(R.string.regist_by_email));
            registByPhone.setVisibility(View.VISIBLE);
            registByEmail.setVisibility(View.GONE);
            photo.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (smsReady) {
            SMSSDK.unregisterAllEventHandler();
        }
    }

    private void interceptHyperLink(TextView tv) {
        //        tv.setAutoLinkMask(Linkify.WEB_URLS);
        tv.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence text = tv.getText();
        if (text instanceof Spannable) {
            int end = text.length();
            Spannable spannable = (Spannable) tv.getText();
            URLSpan[] urlSpans = spannable.getSpans(0, end, URLSpan.class);
            if (urlSpans.length == 0) {
                return;
            }

            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(text);
            // 循环遍历并拦截 所有http://开头的链接
            for (URLSpan uri : urlSpans) {
                String url = uri.getURL();
                if (url.indexOf("http://") == 0 || url.indexOf("https://") == 0) {
                    CustomUrlSpan customUrlSpan = new CustomUrlSpan(this, url);
                    spannableStringBuilder.setSpan(customUrlSpan, spannable.getSpanStart(uri),
                            spannable.getSpanEnd(uri), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                    spannableStringBuilder.removeSpan(uri);
                }
            }
            tv.setText(spannableStringBuilder);
        }
    }

    private void initSMSService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int readPhone = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
            int receiveSms = checkSelfPermission(Manifest.permission.RECEIVE_SMS);
            int readSms = checkSelfPermission(Manifest.permission.READ_SMS);
            int readContacts = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            int readSdcard = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);

            int requestCode = 0;
            List<String> permissions = new ArrayList<>();
            if (readPhone != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 0;
                permissions.add(Manifest.permission.READ_PHONE_STATE);
            }
            if (receiveSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 1;
                permissions.add(Manifest.permission.RECEIVE_SMS);
            }
            if (readSms != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 2;
                permissions.add(Manifest.permission.READ_SMS);
            }
            if (readContacts != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 3;
                permissions.add(Manifest.permission.READ_CONTACTS);
            }
            if (readSdcard != PackageManager.PERMISSION_GRANTED) {
                requestCode |= 1 << 4;
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if (requestCode > 0) {
                String[] permission = new String[permissions.size()];
                this.requestPermissions(permissions.toArray(permission), requestCode);
                return;
            }
        }
        registerSDK();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        registerSDK();
    }

    private void registerSDK() {
        SMSSDK.setAskPermisionOnReadContact(true);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                handler.obtainMessage(0, event, result, data).sendToTarget();
            }
        };
        SMSSDK.registerEventHandler(eh);
        smsReady = true;
    }

    @Deprecated
    private void getSmsFromPhone() {//读取验证码 因权限问题取消
        context.getContentResolver().registerContentObserver(
                Uri.parse("content://sms/"), true, new ContentObserver(handler) {
                    @Override
                    public void onChange(boolean selfChange) {
                        super.onChange(selfChange);
                        getSmsFromPhone();
                    }
                });
        ContentResolver cr = getContentResolver();
        String[] projection = new String[]{"address", "body", "read"};
        String where = "  read=0  ";
        Cursor cur = cr.query(Uri.parse("content://sms/inbox"), projection, where, null, "date desc");
        if (null == cur)
            return;
        if (cur.moveToNext()) {
            String smsbody = cur.getString(cur.getColumnIndex("body"));
            if (smsbody.contains(ConstantManager.appName)) {
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(smsbody);
                handler.obtainMessage(2, m.replaceAll("").trim()).sendToTarget();
            }
        }
        cur.close();
    }

    private boolean regexPhone() {
        String regex = "[1][356789]\\d{9}";
        if (phone.getEditableText().toString().matches(regex)) {
            return true;
        } else {
            phone.setError(context.getString(R.string.matches_phone));
            return false;
        }
    }

    private boolean regexEmail() {
        String regex = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
        if (email.getText().toString().matches(regex)) {
            return true;
        } else {
            email.setError(context.getString(R.string.matches_email));
            return false;
        }
    }

    private int regexUserName(String userName) {
        String regex = "^([a-z0-9A-Z]+[-|.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
        if (userName.matches(regex)) {
            return -1;
        } else if (userName.contains("@")) {
            return -2;
        }
        return 1;
    }

    private boolean regexUserPwd() {
        if (userPwd2.getEditableText().toString().equals(userPwd.getEditableText().toString())) {
            return true;
        } else {
            userPwd2.setError(context.getString(R.string.matches_pwd));
            return false;
        }
    }

    private void registByEmail() {
        if (!userName.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userName);
        } else if (regexUserName(userName.getEditableText().toString()) == -1) {
            userName.setError(context.getString(R.string.regist_username_error1));
            YoYo.with(Techniques.Shake).duration(500).playOn(userName);
        } else if (regexUserName(userName.getEditableText().toString()) == -2) {
            userName.setError(context.getString(R.string.regist_username_error2));
            YoYo.with(Techniques.Shake).duration(500).playOn(userName);
        } else if (!regexUserPwd()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userPwd2);
        } else if (!userPwd.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userPwd);
        } else if (!regexEmail()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(email);
        } else if (!protocol.isChecked()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(protocol);
        } else {
            waittingDialog.show();
            RequestClient.requestAsync(new RegistRequest(new String[]{userName.getText().toString(),
                    userPwd.getText().toString(), email.getText().toString()}), new SimpleRequestCallBack<BaseApiEntity<Integer>>() {
                @Override
                public void onSuccess(BaseApiEntity<Integer> baseApiEntity) {
                    waittingDialog.dismiss();
                    if (BaseApiEntity.isSuccess(baseApiEntity)) {
                        CustomToast.getInstance().showToast(R.string.regist_success);
                        Intent intent = new Intent();
                        intent.putExtra("username", userName.getText().toString());
                        intent.putExtra("userpwd", userPwd.getText().toString());
                        setResult(1, intent);
                        RegistActivity.this.finish();
                    } else if (BaseApiEntity.isFail(baseApiEntity)) {
                        CustomToast.getInstance().showToast(baseApiEntity.getMessage());
                        userName.setError(baseApiEntity.getMessage());
                    } else {
                        CustomToast.getInstance().showToast(context.getString(R.string.regist_fail) + " " + baseApiEntity.getMessage());
                        email.setError(context.getString(R.string.regist_fail));
                    }
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    waittingDialog.dismiss();
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            });
        }
    }

    private void registByPhoneContinue() {
        if (!userName.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userName);
        } else if (regexUserName(userName.getEditableText().toString()) == -1) {
            userName.setError(context.getString(R.string.regist_username_error1));
            YoYo.with(Techniques.Shake).duration(500).playOn(userName);
        } else if (regexUserName(userName.getEditableText().toString()) == -2) {
            userName.setError(context.getString(R.string.regist_username_error2));
            YoYo.with(Techniques.Shake).duration(500).playOn(userName);
        } else if (!regexUserPwd()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userPwd2);
        } else if (!userPwd.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userPwd);
        } else if (!protocol.isChecked()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(protocol);
        } else {
            waittingDialog.show();
            RequestClient.requestAsync(new RegistByPhoneRequest(new String[]{userName.getText()
                    .toString(), userPwd.getText().toString(), phone.getText().toString()}), new SimpleRequestCallBack<BaseApiEntity<Integer>>() {
                @Override
                public void onSuccess(BaseApiEntity<Integer> result) {
                    waittingDialog.dismiss();
                    if (BaseApiEntity.isSuccess(result)) {
                        Intent intent = new Intent();
                        intent.putExtra("username", userName.getText().toString());
                        intent.putExtra("userpwd", userPwd.getText().toString());
                        setResult(1, intent);
                        RegistActivity.this.finish();
                    } else if (BaseApiEntity.isFail(result)) {
                        CustomToast.getInstance().showToast(R.string.regist_userid_same);
                        userName.setError(context.getString(R.string.regist_userid_same));
                    } else {
                        CustomToast.getInstance().showToast(R.string.regist_fail);
                    }
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {
                    waittingDialog.dismiss();
                    CustomToast.getInstance().showToast(Utils.getRequestErrorMeg(errorInfoWrapper));
                }
            });
        }
    }

    private void registByPhone() {
        if (phone.isCharactersCountValid() && messageCode.isCharactersCountValid()) {
            handler.removeMessages(1);
            SMSSDK.submitVerificationCode("86", phone.getText().toString(),
                    messageCode.getText().toString());
        } else {
            YoYo.with(Techniques.Shake).duration(500).playOn(messageCode);
            messageCode.setError(context.getString(R.string.matches_msg_code_empty));
        }
    }

    private static class HandlerMessageByRef implements WeakReferenceHandler.IHandlerMessageByRef<RegistActivity> {
        @Override
        public void handleMessageByRef(final RegistActivity activity, Message msg) {
            switch (msg.what) {
                case 0:
                    int event = msg.arg1;
                    int result = msg.arg2;
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {// 提交验证码成功
                            activity.registByPhone.setVisibility(View.GONE);
                            activity.registByEmail.setVisibility(View.VISIBLE);
                            activity.userName.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
                            activity.userName.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
                            activity.userPwd2.setFloatingLabel(MaterialEditText.FLOATING_LABEL_HIGHLIGHT);
                            activity.email.setVisibility(View.GONE);
                            activity.onActivityResumed();
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            CustomToast.getInstance().showToast(R.string.regist_code_on_way);
                        }
                    } else {
                        CustomToast.getInstance().showToast("验证码获取失败，建议采用邮箱注册方式进行注册，我们会尽快修复~");
                    }
                    break;
                case 1:
                    activity.getMessageCode.setEnabled(false);
                    Message message = new Message();
                    message.what = 1;
                    activity.getMessageCode.setText(activity.getString(R.string.regist_refresh_code, msg.arg1));
                    message.arg1 = msg.arg1 - 1;
                    if (message.arg1 == -1) {
                        activity.getMessageCode.setEnabled(true);
                        activity.getMessageCode.setText(R.string.regist_get_code);
                    } else {
                        activity.handler.sendMessageDelayed(message, 1000);
                    }
                    break;
                case 2:
                    activity.getMessageCode.setText(R.string.regist_get_code);
                    activity.handler.removeMessages(1);
                    activity.messageCode.setText(msg.obj.toString());
                    break;
            }
        }
    }
}
