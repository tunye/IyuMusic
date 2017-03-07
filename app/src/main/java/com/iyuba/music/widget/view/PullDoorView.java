package com.iyuba.music.widget.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.iyuba.music.R;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.widget.bitmap.ReadBitmap;

/**
 * Created by 10202 on 2016/7/25.
 */
public class PullDoorView extends RelativeLayout {
    private Context mContext;
    private Scroller mScroller;
    private int mLastDownY = 0;
    private boolean mCloseFlag = false;
    private IOperationFinish iOperationFinish;
    private ImageView mImgView;

    public PullDoorView(Context context) {
        super(context);
        mContext = context;
        setupView();
    }

    public PullDoorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setupView();
    }

    public void setIOperationFinish(IOperationFinish iOperationFinish) {
        this.iOperationFinish = iOperationFinish;
    }

    @SuppressLint("NewApi")
    private void setupView() {

        // 这个Interpolator你可以设置别的 我这里选择的是有弹跳效果的Interpolator
        BounceInterpolator polator = new BounceInterpolator();
        mScroller = new Scroller(mContext, polator);

        // 这里你一定要设置成透明背景,不然会影响你看到底层布局
        this.setBackgroundColor(Color.argb(0, 0, 0, 0));
        mImgView = new ImageView(mContext);
        mImgView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));
        mImgView.setScaleType(ImageView.ScaleType.FIT_XY);// 填充整个屏幕
        mImgView.setImageBitmap(ReadBitmap.readBitmap(getContext(), R.raw.help5)); // 默认背景
        addView(mImgView);
    }

    // 设置推动门背景
    public void setBgImage(int id) {
        mImgView.setImageResource(id);
    }

    // 设置推动门背景
    public void setBgImage(Drawable drawable) {
        mImgView.setImageDrawable(drawable);
    }

    // 推动门的动画
    public void startBounceAnim(int startY, int dy, int duration) {
        mScroller.startScroll(0, startY, 0, dy, duration);
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mLastDownY = (int) event.getY();
                return true;
            case MotionEvent.ACTION_MOVE:
                int mDelY = (int) event.getY() - mLastDownY;
                if (mDelY < 0) {
                    scrollTo(0, -mDelY);
                }
                break;
            case MotionEvent.ACTION_UP:
                mDelY = (int) event.getY() - mLastDownY;
                if (mDelY < 0) {
                    if (Math.abs(mDelY) > RuntimeManager.getWindowHeight() / 2) {
                        // 向上滑动超过半个屏幕高的时候 开启向上消失动画
                        startBounceAnim(this.getScrollY(), RuntimeManager.getWindowHeight(), 450);
                        if (iOperationFinish != null)
                            iOperationFinish.finish();
                        mCloseFlag = true;
                    } else {
                        // 向上滑动未超过半个屏幕高的时候 开启向下弹动动画
                        startBounceAnim(this.getScrollY(), -this.getScrollY(), 1000);
                    }
                }

                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void computeScroll() {

        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            // 不要忘记更新界面
            postInvalidate();
        } else {
            if (mCloseFlag) {
                RuntimeManager.getContext().sendBroadcast(new Intent("pulldoor.finish"));
                this.setVisibility(View.GONE);
            }
        }
    }
}
