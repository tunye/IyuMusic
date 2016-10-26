package com.iyuba.music.activity.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.chaowen.commentlibrary.ContextManager;
import com.chaowen.commentlibrary.EmojiView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.iyuba.music.R;
import com.iyuba.music.activity.BaseActivity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.merequest.WriteStateRequest;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.dialog.Dialog;
import com.iyuba.music.widget.dialog.WaitingDialog;
import com.nineoldandroids.animation.Animator;
import com.rengwuxian.materialedittext.MaterialEditText;


/**
 * Created by 10202 on 2015/11/20.
 */
public class WriteStateActivity extends BaseActivity {
    private MaterialEditText content;
    private Dialog waitingDialog;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    YoYo.with(Techniques.ZoomOutUp).duration(1200).withListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            CustomToast.INSTANCE.showToast(R.string.state_modify_success);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            handler.sendEmptyMessageDelayed(2, 300);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    }).playOn(content);
                    break;
                case 1:
                    waitingDialog.dismiss();
                    break;
                case 2:
                    Intent intent = new Intent();
                    setResult(1, intent);
                    WriteStateActivity.this.finish();
                    break;
            }
            return false;
        }
    });
    private EmojiView emojiView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ContextManager.setInstance(this);//评论模块初始化
        setContentView(R.layout.write_state);
        context = this;
        initWidget();
        setListener();
        changeUIByPara();
    }

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    protected void initWidget() {
        super.initWidget();
        toolbarOper = (TextView) findViewById(R.id.toolbar_oper);
        content = (MaterialEditText) findViewById(R.id.feedback_content);
        emojiView = (EmojiView) findViewById(R.id.emoji);
        waitingDialog = new WaitingDialog.Builder(context).
                setMessage(context.getString(R.string.state_on_way)).create();
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
    }

    @Override
    protected void changeUIByPara() {
        super.changeUIByPara();
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
            String uid = AccountManager.instance.getUserId();
            WriteStateRequest.getInstance().exeRequest(WriteStateRequest.getInstance().generateUrl(uid, AccountManager.instance.getUserName(),
                    content.getEditableText().toString()), new IProtocolResponse() {
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
                    if ("351".equals(result)) {
                        handler.sendEmptyMessage(0);
                    } else {
                        CustomToast.INSTANCE.showToast(R.string.state_modify_fail);
                    }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ContextManager.destory();
    }
}
