package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.listener.IOnClickListener;
import com.iyuba.music.manager.RuntimeManager;


public class ChangeColorIcon extends View {

    private static final String INSTANCE_STATE = "instance_state";
    private static final String STATE_ALPHA = "state_alpha";
    private Bitmap mBitmap;
    private boolean drawText = false;
    private int originalColor;
    /**
     * 高亮颜色
     */
    private int mColor = 0xFF45C01A;
    /**
     * 透明度 0.0-1.0
     */
    private float mAlpha = 0f;
    /**
     * 图标
     */
    private Bitmap mIconBitmap;
    /**
     * 限制绘制icon的范围
     */
    private Rect mIconRect;
    /**
     * icon底部文本
     */
    private String mText = "";
    private int mTextSize = 10;
    private Paint mTextPaint;
    private Rect mTextBound = new Rect();

    public ChangeColorIcon(Context context) {
        super(context);
    }

    /**
     * 初始化自定义属性值
     *
     * @param context
     * @param attrs
     */
    public ChangeColorIcon(Context context, AttributeSet attrs) {
        super(context, attrs);

        // 获取设置的图标
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.ChangeColorIconView);
            int n = a.getIndexCount();
            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);
                switch (attr) {
                    case R.styleable.ChangeColorIconView_cci_icon:
                        BitmapDrawable drawable = (BitmapDrawable) a.getDrawable(attr);
                        mIconBitmap = drawable.getBitmap();
                        break;
                    case R.styleable.ChangeColorIconView_cci_color:
                        mColor = a.getColor(attr, 0x45C01A);
                        break;
                    case R.styleable.ChangeColorIconView_cci_text:
                        mText = a.getString(attr);
                        break;
                    case R.styleable.ChangeColorIconView_cci_text_size:
                        mTextSize = a.getDimensionPixelOffset(attr, RuntimeManager.sp2px(10));
                        break;
                    case R.styleable.ChangeColorIconView_cci_text_color:
                        originalColor = a.getColor(attr, 0xff555555);
                        break;
                    case R.styleable.ChangeColorIconView_cci_alpha:
                        mAlpha = a.getFloat(attr, 1f);
                        break;
                }
            }
            a.recycle();
        } else {
            originalColor = 0xff555555;
            mTextSize = RuntimeManager.sp2px(10);
            mColor = 0x45C01A;
            mText = "";
            mAlpha = 1f;
        }
        drawText = !TextUtils.isEmpty(mText);
        mTextPaint = new Paint();
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(originalColor);
        // 得到text绘制范围
        mTextPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int bitmapWidth, left, top;
        // 得到绘制icon的宽
        if (drawText) {
            bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                    - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                    - getPaddingBottom() - mTextBound.height() - RuntimeManager.dip2px(5));
            top = (getMeasuredHeight() - mTextBound.height() - RuntimeManager.dip2px(5)) / 2 - bitmapWidth
                    / 2;
        } else {
            bitmapWidth = Math.min(getMeasuredWidth() - getPaddingLeft()
                    - getPaddingRight(), getMeasuredHeight() - getPaddingTop()
                    - getPaddingBottom());
            top = getMeasuredHeight() / 2 - bitmapWidth / 2;
        }
        left = getMeasuredWidth() / 2 - bitmapWidth / 2;
        // 设置icon的绘制范围
        mIconRect = new Rect(left, top, left + bitmapWidth, top + bitmapWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int alpha = (int) Math.ceil((255 * mAlpha));
        canvas.drawBitmap(mIconBitmap, null, mIconRect, null);
        setupTargetBitmap(alpha);
        if (drawText) {
            drawSourceText(canvas, alpha);
            drawTargetText(canvas, alpha);
        }
        canvas.drawBitmap(mBitmap, 0, 0, null);
    }

    private void setupTargetBitmap(int alpha) {
        mBitmap = Bitmap.createBitmap(getMeasuredWidth(), getMeasuredHeight(),
                Config.ARGB_8888);
        Canvas mCanvas = new Canvas(mBitmap);
        Paint mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setAlpha(alpha);
        mCanvas.drawRect(mIconRect, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setAlpha(255);
        mCanvas.drawBitmap(mIconBitmap, null, mIconRect, mPaint);
    }

    private void drawSourceText(Canvas canvas, int alpha) {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(originalColor);
        mTextPaint.setAlpha(255 - alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2,
                mIconRect.bottom + RuntimeManager.dip2px(5) + mTextBound.height() / 2, mTextPaint);
    }

    private void drawTargetText(Canvas canvas, int alpha) {
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mColor);
        mTextPaint.setAlpha(alpha);
        canvas.drawText(mText, mIconRect.left + mIconRect.width() / 2
                        - mTextBound.width() / 2,
                mIconRect.bottom + RuntimeManager.dip2px(5) + mTextBound.height() / 2, mTextPaint);
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    public void setIcon(int resId) {
        this.mIconBitmap = BitmapFactory.decodeResource(getResources(), resId);
        if (mIconRect != null)
            invalidateView();
    }

    public void setIcon(Bitmap iconBitmap) {
        this.mIconBitmap = iconBitmap;
        if (mIconRect != null)
            invalidateView();
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState());
        bundle.putFloat(STATE_ALPHA, mAlpha);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mAlpha = bundle.getFloat(STATE_ALPHA);
            super.onRestoreInstanceState(bundle.getParcelable(INSTANCE_STATE));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    public void setOnIClickListener(final IOnClickListener onIClickListener) {
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        setmAlpha(1f);
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        setmAlpha(0f);
                        onIClickListener.onClick(v, null);
                        break;
                }
                return true;
            }
        });
    }

    public void setOriginalColor(int originalColor) {
        this.originalColor = originalColor;
        invalidateView();
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
        invalidateView();
    }

    public void setmAlpha(float mAlpha) {
        this.mAlpha = mAlpha;
        invalidateView();
    }

    public void setmText(String mText) {
        this.mText = mText;
        this.drawText = !TextUtils.isEmpty(mText);
        invalidateView();
    }

    public void setmTextSize(int mTextSize) {
        this.mTextSize = RuntimeManager.sp2px(mTextSize);
        invalidateView();
    }
}
