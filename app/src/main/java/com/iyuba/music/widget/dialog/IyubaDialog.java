package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialog;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.buaa.ct.core.manager.ImmersiveManager;
import com.iyuba.music.R;


public class IyubaDialog extends AppCompatDialog {
    public static int styleId = R.style.MyDialogTheme;
    private Context context;
    private View contentView;
    private FrameLayout backView, containerView;
    private AnimationListener animationListener = new AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            contentView.post(new Runnable() {
                @Override
                public void run() {
                    IyubaDialog.super.dismiss();
                }
            });
        }
    };

    private boolean cancelOutSide;
    private int gravity;
    private OnDismissListener listener;

    public IyubaDialog(Context context, View v) {
        this(context, v, true);
    }

    public IyubaDialog(Context context, View v, boolean cancel) {
        this(context, v, cancel, Gravity.CENTER);
    }

    public IyubaDialog(Context context, View v, boolean cancel, int gravity) {
        this(context, v, cancel, gravity, null);
    }

    public IyubaDialog(Context context, View v, boolean cancel, int gravity, OnDismissListener dismissListener) {
        super(context, styleId);
        this.context = context;// init Context
        this.contentView = v;
        this.cancelOutSide = cancel;
        this.gravity = gravity;
        this.listener = dismissListener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        if (window != null) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        setContentView(R.layout.dialog);
        backView = findViewById(R.id.dialog_rootView);
        containerView = findViewById(R.id.dialog_content);
        if (cancelOutSide) {
            backView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getX() < contentView.getLeft()
                            || event.getX() > contentView.getRight()
                            || event.getY() > contentView.getBottom()
                            || event.getY() < contentView.getTop()) {
                        dismissAnim();
                    }
                    return false;
                }
            });
        }
        containerView.addView(contentView);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) containerView.getLayoutParams();
        layoutParams.gravity = gravity;
        containerView.setLayoutParams(layoutParams);
        if (listener != null) {
            this.setOnDismissListener(listener);
        }
    }

    @Override
    public void show() {
        super.show();
        contentView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.dialog_main_show_amination));
        ImmersiveManager.getInstance().updateImmersiveStatus(this);
    }

    void showAnim(int animStyle) {
        super.show();
        backView.startAnimation(AnimationUtils.loadAnimation(context, animStyle));
        ImmersiveManager.getInstance().updateImmersiveStatus(this);
    }

    void dismissAnim() {
        Animation anim = AnimationUtils.loadAnimation(context, R.anim.dialog_main_hide_amination);
        anim.setAnimationListener(animationListener);
        contentView.startAnimation(anim);
    }

    void dismissAnim(int animStyle) {
        Animation anim = AnimationUtils.loadAnimation(context, animStyle);
        anim.setAnimationListener(animationListener);
        backView.startAnimation(anim);
    }
}
