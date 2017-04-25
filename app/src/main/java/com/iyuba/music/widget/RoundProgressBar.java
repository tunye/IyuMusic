package com.iyuba.music.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.iyuba.music.R;

/**
 * 下载圆形进度条
 *
 * @author 陈彤
 */
public class RoundProgressBar extends View {

    public static final int STROKE = 0;
    public static final int FILL = 1;
    private Paint paint;
    private RectF oval;
    private int roundColor;
    private int roundProgressColor;
    private int textColor;
    private float textSize;
    private float roundWidth;
    private int max;
    private int progress;
    private boolean textIsDisplayable;
    private int style;

    public RoundProgressBar(Context context) {
        this(context, null);
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        paint = new Paint();
        paint.setAntiAlias(true);
        oval = new RectF();
        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.circleProgressBar);
        roundColor = mTypedArray.getColor(
                R.styleable.circleProgressBar_cp_circleColor, Color.RED);
        roundProgressColor = mTypedArray.getColor(
                R.styleable.circleProgressBar_cp_circleProgressColor, Color.GREEN);
        textColor = mTypedArray.getColor(
                R.styleable.circleProgressBar_cp_textColor, Color.GREEN);
        textSize = mTypedArray.getDimension(
                R.styleable.circleProgressBar_cp_textSize, 16);
        roundWidth = mTypedArray.getDimension(
                R.styleable.circleProgressBar_cp_circleWidth, 6);
        max = mTypedArray.getInteger(R.styleable.circleProgressBar_cp_max, 100);
        textIsDisplayable = mTypedArray.getBoolean(
                R.styleable.circleProgressBar_cp_textIsDisplayable, true);
        style = mTypedArray.getInt(R.styleable.circleProgressBar_cp_style, 0);
        mTypedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int centre = getWidth() / 2;
        int radius = (int) (centre - roundWidth / 2);
        paint.setColor(roundColor);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(roundWidth);
        canvas.drawCircle(centre, centre, radius, paint);
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int) (100f * progress / max);
        float textWidth = paint.measureText(String.valueOf(percent));
        if (textIsDisplayable && style == STROKE) {
            canvas.drawText(String.valueOf(percent), centre - textWidth / 2,
                    centre + textSize / 2, paint);
        }
        paint.setStrokeWidth(roundWidth);
        paint.setColor(roundProgressColor);
        oval.set(centre - radius, centre - radius, centre + radius, centre + radius);
        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawArc(oval, 270, (float) (360.0 / max * progress), false,
                        paint);
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, 270, (float) (360.0 / max * progress), true,
                            paint);
                break;
            }
        }
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }

    public int getCricleColor() {
        return roundColor;
    }

    public void setCricleColor(int cricleColor) {
        this.roundColor = cricleColor;
    }

    public int getCricleProgressColor() {
        return roundProgressColor;
    }

    public void setCricleProgressColor(int cricleProgressColor) {
        this.roundProgressColor = cricleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }
}
