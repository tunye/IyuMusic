package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;


public class Dialog extends AppCompatDialog {

    private Context context;
    private View contentView;
    private View view;
    AnimationListener animationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            view.post(new Runnable() {
                @Override
                public void run() {
                    Dialog.super.dismiss();
                }
            });
        }
    };
    private LinearLayout backView;
    private boolean cancelOutSide;
    private OnDismissListener listener;
    private int marginDimension;

    public Dialog(Context context, View v) {
        super(context, R.style.MyDialogTheme);
        this.context = context;// init Context
        this.contentView = v;
        cancelOutSide = true;
        marginDimension = 10;
    }

    public Dialog(Context context, View v, boolean cancle) {
        super(context, R.style.MyDialogTheme);
        this.context = context;// init Context
        this.contentView = v;
        this.cancelOutSide = cancle;
        marginDimension = 10;
    }

    public Dialog(Context context, View v, boolean cancle, int marginDimension) {
        super(context, R.style.MyDialogTheme);
        this.context = context;// init Context
        this.contentView = v;
        this.cancelOutSide = cancle;
        this.marginDimension = marginDimension;
    }


    public Dialog(Context context, View v, boolean cancle, int marginDimension, OnDismissListener dismissListener) {
        super(context, R.style.MyDialogTheme);
        this.context = context;// init Context
        this.contentView = v;
        this.cancelOutSide = cancle;
        this.listener = dismissListener;
        this.marginDimension = marginDimension;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.dialog);
        view = findViewById(R.id.contentDialog);
        backView = (LinearLayout) findViewById(R.id.dialog_rootView);
        if (cancelOutSide) {
            backView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getX() < view.getLeft()
                            || event.getX() > view.getRight()
                            || event.getY() > view.getBottom()
                            || event.getY() < view.getTop()) {
                        dismiss();
                    }
                    return false;
                }
            });
        }
        backView.removeAllViewsInLayout();
        backView.removeView(contentView);
        backView.addView(contentView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        view = contentView;
        backView.setPadding(RuntimeManager.dip2px(marginDimension), RuntimeManager.dip2px(marginDimension), RuntimeManager.dip2px(marginDimension), RuntimeManager.dip2px(marginDimension));
        if (listener != null) {
            this.setOnDismissListener(listener);
        }
    }

    @Override
    public void show() {
        super.show();
        // set dialog enter animations
        view.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
        backView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_root_show_amination));
    }

    public void show(int animStyle) {
        super.show();
        backView.startAnimation(AnimationUtils.loadAnimation(context, animStyle));
    }

    @Override
    public void dismiss() {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
        anim.setAnimationListener(animationListener);
        Animation backAnim = AnimationUtils.loadAnimation(context, R.anim.dialog_root_hide_amination);
        view.startAnimation(anim);
        backView.startAnimation(backAnim);
    }

    public void dismiss(int animStyle) {
        Animation anim = AnimationUtils.loadAnimation(context, animStyle);
        anim.setAnimationListener(animationListener);
        backView.startAnimation(anim);
    }
}
