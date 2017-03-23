package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

import com.iyuba.music.R;

/**
 * 圆形ImageView，可设置最多两个宽度不同且颜色不同的圆形边框。
 *
 * @author Alan
 */
public class RoundImageView extends AppCompatImageView {
    private int mBorderThickness = 0;
    private int defaultColor = 0xFFFFFFFF;
    // 如果只有其中一个有值，则只画一个圆形边框
    private int mBorderOutsideColor = 0;
    private int mBorderInsideColor = 0;
    private int mBorderProgressColor = 0;
    // 控件默认长、宽
    private int defaultWidth = 0;
    private int defaultHeight = 0;
    private float progress = 0;
    // 绘图控件
    private boolean isShow;
    private int radius;

    public RoundImageView(Context context) {
        super(context);
        isShow = true;
        radius = 0;
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setCustomAttributes(context, attrs);
    }

    public RoundImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCustomAttributes(context, attrs);
    }

    private void setCustomAttributes(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.roundedimageview);
        radius = 0;
        mBorderThickness = a.getDimensionPixelSize(
                R.styleable.roundedimageview_border_thickness, 0);
        mBorderOutsideColor = a
                .getColor(R.styleable.roundedimageview_border_outside_color,
                        defaultColor);
        mBorderInsideColor = a.getColor(
                R.styleable.roundedimageview_border_inside_color, defaultColor);
        mBorderProgressColor = a.getColor(
                R.styleable.roundedimageview_border_progress_color,
                defaultColor);
        isShow = a.getBoolean(R.styleable.roundedimageview_show_image, true);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return;
        }

        if (getWidth() == 0 || getHeight() == 0) {
            return;
        }
        this.measure(0, 0);
        if (drawable.getClass() == NinePatchDrawable.class)
            return;
        Bitmap b = ((BitmapDrawable) drawable).getBitmap();
        Bitmap bitmap = b.copy(Config.ARGB_8888, true);
        if (defaultWidth == 0) {
            defaultWidth = getWidth();
        }
        if (defaultHeight == 0) {
            defaultHeight = getHeight();
        }
        radius = 0;
        int centre = (defaultWidth < defaultHeight ? defaultWidth
                : defaultHeight) / 2;
        if (mBorderInsideColor != defaultColor
                && mBorderOutsideColor != defaultColor) {// 定义画两个边框，分别为外圆边框和内圆边框
            radius = centre - 2 * mBorderThickness;
            // 画内圆
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);
            // 画外圆
            drawCircleBorder(canvas, radius + mBorderThickness
                    + mBorderThickness / 2, mBorderOutsideColor);
            if (progress != 0) {
                drawArcProgressBorder(canvas, centre);
            }
        } else if (mBorderInsideColor != defaultColor
                && mBorderOutsideColor == defaultColor) {// 定义画一个边框
            radius = centre - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderInsideColor);
            if (progress != 0) {
                drawArcProgressBorder(canvas, centre);
            }
        } else if (mBorderInsideColor == defaultColor
                && mBorderOutsideColor != defaultColor) {// 定义画一个边框
            radius = centre - mBorderThickness;
            drawCircleBorder(canvas, radius + mBorderThickness / 2,
                    mBorderOutsideColor);
            if (progress != 0) {
                drawArcProgressBorder(canvas, centre);
            }
        } else {// 没有边框
            radius = centre;
        }
        if (isShow) {
            Bitmap roundBitmap = getCroppedRoundBitmap(bitmap);
            canvas.drawBitmap(roundBitmap, defaultWidth / 2 - radius,
                    defaultHeight / 2 - radius, null);
        }
    }

    /**
     * 获取裁剪后的圆形图片
     */
    public Bitmap getCroppedRoundBitmap(Bitmap bmp) {
        Bitmap scaledSrcBmp;
        int diameter = radius * 2;

        // 为了防止宽高不相等，造成圆形图片变形，因此截取长方形中处于中间位置最大的正方形图片
        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();
        int squareWidth = 0, squareHeight = 0;
        int x = 0, y = 0;
        Bitmap squareBitmap;
        if (bmpHeight > bmpWidth) {// 高大于宽
            squareWidth = squareHeight = bmpWidth;
            x = 0;
            y = (bmpHeight - bmpWidth) / 2;
            // 截取正方形图片
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else if (bmpHeight < bmpWidth) {// 宽大于高
            squareWidth = squareHeight = bmpHeight;
            x = (bmpWidth - bmpHeight) / 2;
            y = 0;
            squareBitmap = Bitmap.createBitmap(bmp, x, y, squareWidth,
                    squareHeight);
        } else {
            squareBitmap = bmp;
        }

        if (squareBitmap.getWidth() != diameter
                || squareBitmap.getHeight() != diameter) {
            scaledSrcBmp = Bitmap.createScaledBitmap(squareBitmap, diameter,
                    diameter, true);

        } else {
            scaledSrcBmp = squareBitmap;
        }
        Bitmap output = Bitmap.createBitmap(scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paint = new Paint();
        Rect rect = new Rect(0, 0, scaledSrcBmp.getWidth(),
                scaledSrcBmp.getHeight());

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(scaledSrcBmp.getWidth() / 2,
                scaledSrcBmp.getHeight() / 2, scaledSrcBmp.getWidth() / 2,
                paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(scaledSrcBmp, rect, rect, paint);
        // bitmap回收(recycle导致在布局文件XML看不到效果)
        bmp.recycle();
        squareBitmap.recycle();
        scaledSrcBmp.recycle();
//        bmp = null;
//        squareBitmap = null;
//        scaledSrcBmp = null;
        return output;
    }

    /**
     * 边缘画圆
     */
    private void drawCircleBorder(Canvas canvas, int radius, int color) {
        Paint paint = new Paint();
        /* 去锯齿 */
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(color);
        /* 设置paint的　style　为STROKE：空心 */
        paint.setStyle(Paint.Style.STROKE);
        /* 设置paint的外框宽度 */
        paint.setStrokeWidth(mBorderThickness);
        canvas.drawCircle(defaultWidth / 2, defaultHeight / 2, radius, paint);
    }

    private void drawArcProgressBorder(Canvas canvas, int centre) {
        Paint paint = new Paint();
        RectF oval = new RectF(centre - radius - mBorderThickness / 2, centre
                - radius - mBorderThickness / 2, centre + radius
                + mBorderThickness / 2, centre + radius
                + mBorderThickness / 2);
        paint.setColor(mBorderProgressColor);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mBorderThickness);
        paint.setAntiAlias(true);
        canvas.drawArc(oval, 270, 360 * progress, false, paint);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public boolean isShow() {
        return isShow;
    }

    public void setIsShow(boolean isShow) {
        this.isShow = isShow;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public int getmBorderThickness() {
        return mBorderThickness;
    }

    public void setmBorderThickness(int mBorderThickness) {
        this.mBorderThickness = mBorderThickness;
    }

    public int getmBorderProgressColor() {
        return mBorderProgressColor;
    }

    public void setmBorderProgressColor(int mBorderProgressColor) {
        this.mBorderProgressColor = mBorderProgressColor;
    }

    public int getmBorderInsideColor() {
        return mBorderInsideColor;
    }

    public void setmBorderInsideColor(int mBorderInsideColor) {
        this.mBorderInsideColor = mBorderInsideColor;
    }

    public int getmBorderOutsideColor() {
        return mBorderOutsideColor;
    }

    public void setmBorderOutsideColor(int mBorderOutsideColor) {
        this.mBorderOutsideColor = mBorderOutsideColor;
    }
}
