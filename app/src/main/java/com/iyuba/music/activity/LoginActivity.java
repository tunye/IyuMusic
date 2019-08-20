package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.adapter.me.AutoCompleteAdapter;
import com.iyuba.music.entity.user.HistoryLogin;
import com.iyuba.music.entity.user.HistoryLoginOp;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.OnRecycleViewItemClickListener;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.IyubaDialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.iyuba.music.widget.roundview.RoundTextView;
import com.iyuba.music.widget.view.AddRippleEffect;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/19.
 */
public class LoginActivity extends BaseInputActivity {
    private static IOperationResult result;
    private MaterialAutoCompleteTextView username, userpwd;
    private TextView forgetPwd;
    private CheckBox autoLogin;
    private RoundTextView login;
    private IyubaDialog waitingDialog;
    TextView.OnEditorActionListener editor = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                login();
                return true;
            }
            return false;
        }
    };
    private View photo, loginMsg;

    public static void launch(Context context, IOperationResult result) {
        LoginActivity.result = result;
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        photo = findViewById(R.id.login_photo);
        loginMsg = findViewById(R.id.login_message);
        username = (MaterialAutoCompleteTextView) findViewById(R.id.username);
        userpwd = (MaterialAutoCompleteTextView) findViewById(R.id.userpwd);
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        autoLogin = (CheckBox) findViewById(R.id.auto_login);
        forgetPwd = (TextView) findViewById(R.id.forget_pwd);
        login = (RoundTextView) findViewById(R.id.login);
        AddRippleEffect.addRippleEffect(login);
        waitingDialog = WaitingDialog.create(context, context.getString(R.string.login_on_way));
    }

    @Override
    protected void setListener() {
        super.setListener();
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        login();
                    }
                }
        );
        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userpwd.setText("");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        forgetPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("url", "http://m.iyuba.cn/m_login/inputPhonefp.jsp");
                intent.putExtra("title", forgetPwd.getText());
                startActivity(intent);
            }
        });
        toolbarOper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(context, RegistActivity.class), 101);
            }
        });
        userpwd.setOnEditorActionListener(editor);
        setUserNameAutoLogin();
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
        toolbarOper.setText(R.string.regist_oper);
        title.setText(R.string.login_title);
        photo.setAlpha(0);
        loginMsg.setAlpha(0);
        title.postDelayed(new Runnable() {
            public void run() {
                YoYo.with(Techniques.FadeInLeft).duration(1000).playOn(photo);
                YoYo.with(Techniques.FadeInRight).duration(1000).playOn(loginMsg);
            }
        }, 200);
    }

    private void setUserNameAutoLogin() {
        final HistoryLoginOp historyLoginOp = new HistoryLoginOp();
        final ArrayList<HistoryLogin> historyLogins = historyLoginOp.selectData();
        ArrayList<String> historyLoginNames = new ArrayList<>();
        for (HistoryLogin historyLogin : historyLogins) {
            historyLoginNames.add(historyLogin.getUserName());
        }
        final AutoCompleteAdapter adapter = new AutoCompleteAdapter(context, historyLoginNames);
        adapter.setOnItemClickLitener(new OnRecycleViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String name = adapter.getItem(position).toString();
                username.setText(name);
                for (HistoryLogin historyLogin : historyLogins) {
                    if (historyLogin.getUserName().equals(name)) {
                        userpwd.setText(historyLogin.getUserPwd());
                    }
                }
                username.dismissDropDown();
            }

            @Override
            public void onItemLongClick(View view, int position) {//点击删除按钮
                historyLoginOp.deleteData(adapter.getItem(position).toString());
            }
        });
        username.setAdapter(adapter);
        username.setThreshold(0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == 1) {// 注册的返回结果
            String usernameString = data.getStringExtra("username");
            String userpwdString = data.getStringExtra("userpwd");
            username.setText(usernameString);
            userpwd.setText(userpwdString);
            waitingDialog.show();
            AccountManager.getInstance().login(username.getText().toString(), userpwd.getText().toString(), new IOperationResult() {
                @Override
                public void success(Object object) {
                    waitingDialog.dismiss();
                    ConfigManager.getInstance().setAutoLogin(autoLogin.isChecked());
                    Intent intent = new Intent();
                    setResult(2, intent);
                    if (result != null) {
                        result.success(null);
                    }
                    LoginActivity.this.finish();
                }

                @Override
                public void fail(Object object) {
                    CustomToast.getInstance().showToast(object.toString());
                    if (result != null) {
                        result.fail(null);
                    }
                    waitingDialog.dismiss();
                }
            });
        }
    }

    private void login() {
        if (username.isCharactersCountValid() && userpwd.isCharactersCountValid()) {
            waitingDialog.show();
            AccountManager.getInstance().login(username.getText().toString(), userpwd.getText().toString(), new IOperationResult() {
                @Override
                public void success(Object object) {
                    waitingDialog.dismiss();
                    ConfigManager.getInstance().setAutoLogin(autoLogin.isChecked());
                    Intent intent = new Intent();
                    setResult(1, intent);
                    if (result != null) {
                        result.success(null);
                    }
                    LoginActivity.this.finish();
                }

                @Override
                public void fail(Object object) {
                    waitingDialog.dismiss();
                    if (result != null) {
                        result.fail(null);
                    }
                    CustomToast.getInstance().showToast(object.toString());
                }
            });
        } else if (!username.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(username);
        } else if (!userpwd.isCharactersCountValid()) {
            YoYo.with(Techniques.Shake).duration(500).playOn(userpwd);
        }
    }
}
