package com.iyuba.music.activity;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.balysv.materialmenu.MaterialMenuDrawable;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.flyco.roundview.RoundTextView;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.account.CheckPhoneRegisted;
import com.iyuba.music.request.account.RegistByPhoneRequest;
import com.iyuba.music.request.account.RegistRequest;
import com.iyuba.music.util.WeakReferenceHandler;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by 10202 on 2015/11/24.
 */
public class RegistActivity extends BaseInputActivity {
    Handler handler = new WeakReferenceHandler<>(this, new HandlerMessageByRef());
    private MaterialEditText phone, messageCode, userName, userPwd, userPwd2, email;
    private TextView protocolText;
    private RoundTextView regist, getMessageCode;
    private CheckBox protocol;
    private View registByPhone, registByEmail;
    private CircleImageView photo;
    private Dialog waittingDialog;
    TextView.OnEditorActionListener editor = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(),
                            0);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regist);
        context = this;
        initSMSService();
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        photo = (CircleImageView) findViewById(R.id.regist_photo);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        regist = (RoundTextView) findViewById(R.id.regist);
        protocol = (CheckBox) findViewById(R.id.regist_protocol_checkbox);
        protocolText = (TextView) findViewById(R.id.regist_protocol);
        registByPhone = findViewById(R.id.regist_by_phone);
        registByEmail = findViewById(R.id.regist_by_email);
        getMessageCode = (RoundTextView) findViewById(R.id.get_msg_code);
        phone = (MaterialEditText) findViewById(R.id.regist_phone);
        messageCode = (MaterialEditText) findViewById(R.id.regist_msg_code);
        userName = (MaterialEditText) findViewById(R.id.regist_username);
        userPwd = (MaterialEditText) findViewById(R.id.regist_pwd);
        userPwd2 = (MaterialEditText) findViewById(R.id.regist_repwd);
        email = (MaterialEditText) findViewById(R.id.regist_email);
        waittingDialog =  WaitingDialog.create(context, context.getString(R.string.regist_on_way));
    }

    @Override
    protected void setListener() {
        super.setListener();
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (toolbarOper.getText().equals(context.getString(R.string.regist_by_phone))) {
                    toolbarOper.setText(context.getString(R.string.regist_by_email));
                    photo.setVisibility(View.VISIBLE);
                    registByPhone.setVisibility(View.VISIBLE);
                    registByEmail.setVisibility(View.GONE);
                } else {
                    photo.setVisibility(View.GONE);
                    toolbarOper.setText(context.getString(R.string.regist_by_phone));
                    registByPhone.setVisibility(View.GONE);
                    registByEmail.setVisibility(View.VISIBLE);
                }
            }
        });
        getMessageCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!phone.isCharactersCountValid() || !regexPhone()) {
                    YoYo.with(Techniques.Shake).duration(500).playOn(phone);
                } else {
                    CheckPhoneRegisted.exeRequest(CheckPhoneRegisted.generateUrl(phone.getText().toString()), new IProtocolResponse() {
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
                            int result = (int) object;
                            if (result == 1) {
                                SMSSDK.getVerificationCode("86", phone.getText().toString());
                                Message message = new Message();
                                message.arg1 = 60;
                                message.what = 1;
                                handler.sendMessage(message);
                            } else {
                                YoYo.with(Techniques.Shake).duration(500).playOn(phone);
                                phone.setError(context.getString(R.string.regist_phone_registed, phone.getText()));
                            }
                        }
                    });

                }
            }
        });
        regist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        protocolText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", "http://app.iyuba.com/ios/protocol.html");
                intent.putExtra("title", context.getString(R.string.regist_protocol));
                startActivity(intent);
            }
        });
        userPwd2.setOnEditorActionListener(editor);
        email.setOnEditorActionListener(editor);
        messageCode.setOnEditorActionListener(editor);
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        protocolText.setText(R.string.regist_protocol);
        backIcon.setState(MaterialMenuDrawable.IconState.ARROW);
        registByPhone.setVisibility(View.VISIBLE);
        registByEmail.setVisibility(View.GONE);
        title.setText(R.string.regist_title);
    }

    protected void changeUIResumeByPara() {
        if (registByEmail.getVisibility() == View.VISIBLE) {
            photo.setVisibility(View.GONE);
            if (email.getVisibility() == View.VISIBLE) {
                regist.setText(context.getString(R.string.regist_title));
                toolbarOper.setText(context.getString(R.string.regist_by_phone));
                userPwd2.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                title.setText(R.string.regist_title);
            } else {
                regist.setText(context.getString(R.string.regist_phone_part_two));
                toolbarOper.setText(context.getString(R.string.regist_by_email));
                userPwd2.setImeOptions(EditorInfo.IME_ACTION_SEND);
                title.setText(R.string.regist_phone_part_two);
            }
        } else if (registByPhone.getVisibility() == View.VISIBLE) {
            toolbarOper.setText(context.getString(R.string.regist_by_email));
            regist.setText(context.getString(R.string.regist_title));
            photo.setVisibility(View.VISIBLE);
            title.setText(R.string.regist_title);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        changeUIResumeByPara();
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        if (registByEmail.getVisibility() == View.VISIBLE) {
            toolbarOper.setText(context.getString(R.string.regist_by_email));
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
    }

    private void initSMSService() {
        SMSSDK.initSDK(this, ConstantManager.SMSAPPID, ConstantManager.SMSAPPSECRET);
        EventHandler eh = new EventHandler() {

            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                msg.what = 0;
                handler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
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
            if (smsbody.contains(ConstantManager.instance.getAppName())) {
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(smsbody);
                handler.obtainMessage(2, m.replaceAll("").trim()).sendToTarget();
            }
        }
    }

    private boolean regexPhone() {
        String regex = "[1][3578]\\d{9}";
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
            RegistRequest.exeRequest(RegistRequest.generateUrl(new String[]{userName.getText().toString(),
                    userPwd.getText().toString(), email.getText().toString()}), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    waittingDialog.dismiss();
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    waittingDialog.dismiss();
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void response(Object object) {
                    waittingDialog.dismiss();
                    BaseApiEntity baseApiEntity = (BaseApiEntity) object;
                    int result = (int) baseApiEntity.getData();
                    if (result == 111) {
                        Intent intent = new Intent();
                        intent.putExtra("username", userName.getText().toString());
                        intent.putExtra("userpwd", userPwd.getText().toString());
                        setResult(1, intent);
                        RegistActivity.this.finish();
                    } else if (result == 112) {
                        CustomToast.INSTANCE.showToast(R.string.regist_userid_same);
                        userName.setError(context.getString(R.string.regist_userid_same));
                    } else if (result == 113) {
                        CustomToast.INSTANCE.showToast(R.string.regist_email_same);
                        email.setError(context.getString(R.string.regist_email_same));
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.regist_fail + " " + baseApiEntity.getMessage());
                    }
                }
            });
        }
    }

    private void registByPhoneContinue() {
        if (!userName.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userName);
        } else if (!regexUserPwd()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userPwd2);
        } else if (!userPwd.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userPwd);
        } else if (!protocol.isChecked()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(protocol);
        } else {
            waittingDialog.show();
            RegistByPhoneRequest.exeRequest(RegistByPhoneRequest.generateUrl(new String[]{userName.getText()
                    .toString(), userPwd.getText().toString(), phone.getText().toString()}), new IProtocolResponse() {
                @Override
                public void onNetError(String msg) {
                    waittingDialog.dismiss();
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void onServerError(String msg) {
                    waittingDialog.dismiss();
                    CustomToast.INSTANCE.showToast(msg);
                }

                @Override
                public void response(Object object) {
                    waittingDialog.dismiss();
                    int result = (int) object;
                    if (result == 111) {
                        Intent intent = new Intent();
                        intent.putExtra("username", userName.getText().toString());
                        intent.putExtra("userpwd", userPwd.getText().toString());
                        setResult(1, intent);
                        RegistActivity.this.finish();
                    } else if (result == 112) {
                        CustomToast.INSTANCE.showToast(R.string.regist_userid_same);
                        userName.setError(context.getString(R.string.regist_userid_same));
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.regist_fail);
                    }
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
                            activity.changeUIResumeByPara();
                        } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            CustomToast.INSTANCE.showToast(R.string.regist_code_on_way);
                        }
                    } else {
                        activity.messageCode.setError(activity.getString(R.string.matches_msg_code));
                        activity.getMessageCode.setText(R.string.regist_get_code);
                        activity.getMessageCode.setEnabled(true);
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
