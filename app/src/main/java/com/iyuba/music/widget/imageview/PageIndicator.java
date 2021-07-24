package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Looper;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;

import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.util.GetAppColor;
import com.iyuba.music.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 10202 on 2015/11/17.
 */
public class PageIndicator extends View {
    public static final int LEFT = 0x01;
    public static final int RIGHT = 0x02;
    public static final int NONE = 0x03;
    private Context context;
    private int strokeColor;
    private int fillColor;
    private float stroke;
    private float radius;
    private float circlePadding;
    private int circleCount;
    private int currentItem;
    private float movePercent;
    private Paint strokePaint, fillPaint;
    @Direction
    private int direction;

    public PageIndicator(Context context) {
        super(context);
        this.context = context;
        setAttr(null);
    }

    public PageIndicator(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
        setAttr(attributeSet);
    }

    public PageIndicator(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        this.context = context;
        setAttr(attributeSet);
    }

    private void setAttr(AttributeSet attr) {
        if (attr == null) {
            strokeColor = Color.WHITE;
            fillColor = Color.WHITE;
            stroke = RuntimeManager.getInstance().dip2px(1);
            circlePadding = RuntimeManager.getInstance().dip2px(4);
            radius = RuntimeManager.getInstance().dip2px(6);
            circleCount = 1;
        } else {
            TypedArray typedArray = context.obtainStyledAttributes(attr, R.styleable.PageIndicator);
            strokeColor = typedArray.getColor(R.styleable.PageIndicator_pi_strokecolor, GetAppColor.getInstance().getAppColor());
            fillColor = typedArray.getColor(R.styleable.PageIndicator_pi_fillcolor, GetAppColor.getInstance().getAppColorLight());
            stroke = typedArray.getDimension(R.styleable.PageIndicator_pi_stroke, RuntimeManager.getInstance().dip2px(1));
            radius = typedArray.getDimension(R.styleable.PageIndicator_pi_radius, RuntimeManager.getInstance().dip2px(6));
            circleCount = typedArray.getInt(R.styleable.PageIndicator_pi_count, 1);
            circlePadding = typedArray.getDimension(R.styleable.PageIndicator_pi_circlepadding, RuntimeManager.getInstance().dip2px(4));
            typedArray.recycle();
        }
        direction = NONE;
        strokePaint = new Paint();
        strokePaint.setAntiAlias(true);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setColor(strokeColor);
        strokePaint.setStrokeWidth(stroke);

        fillPaint = new Paint();
        fillPaint.setAntiAlias(true);
        fillPaint.setColor(fillColor);
    }

    private void invalidateView() {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            invalidate();
        } else {
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(measureLong(widthMeasureSpec), measureShort(heightMeasureSpec));
    }

    private int measureLong(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if ((specMode == MeasureSpec.EXACTLY) || (circleCount == 0)) {
            // We were told how big to be
            result = specSize;
        } else {
            // Calculate the width according the views count
            result = (int) (getPaddingLeft() + getPaddingRight()
                    + (circleCount * 2 * (radius + stroke)) + (circleCount - 1) * circlePadding);
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    private int measureShort(int measureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the height
            result = (int) (2 * (radius + stroke) + getPaddingTop() + getPaddingBottom());
            // Respect AT_MOST value if that was what is called for by
            // measureSpec
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (circleCount == 0) {
            return;
        }
        float length = (radius + stroke) * 2 * circleCount + (circleCount - 1) * circlePadding;
        float dx = (getWidth() - length) / 2 + (radius + stroke),
                dy = (radius + stroke);
        // Draw stroked circles
        for (int i = 0; i < circleCount; i++) {
            canvas.drawCircle(dx, dy, radius + stroke / 2.0f, this.strokePaint);
            if (i == currentItem) {
                switch (direction) {
                    case NONE:
                        canvas.drawCircle(dx, dy, radius, this.fillPaint);
                        break;
                    case LEFT:
                        canvas.drawCircle(dx - ((1 - movePercent) * (2 * (stroke + radius) + circlePadding)), radius + stroke, Math.max(movePercent, (1 - movePercent)) * radius, this.fillPaint);
                        break;
                    case RIGHT:
                        canvas.drawCircle(dx + (movePercent * (2 * (stroke + radius) + circlePadding)), radius + stroke, Math.max(movePercent, (1 - movePercent)) * radius, this.fillPaint);
                        break;
                }
            }
            dx += (radius + stroke) * 2 + circlePadding;
        }
    }

    public void setStrokeColor(int strokeColor) {
        this.strokeColor = strokeColor;
        strokePaint.setColor(strokeColor);
        invalidateView();
    }

    public void setFillColor(int fillColor) {
        this.fillColor = fillColor;
        fillPaint.setColor(fillColor);
        invalidateView();
    }

    public void setStroke(float stroke) {
        this.stroke = stroke;
        strokePaint.setStrokeWidth(stroke);
        invalidateView();
    }

    public void setRadius(float radius) {
        this.radius = radius;
        invalidateView();
    }

    public void setCirclePadding(float circlePadding) {
        this.circlePadding = circlePadding;
        invalidateView();
    }

    public void setCircleCount(int circleCount) {
        this.circleCount = circleCount;
        invalidateView();
    }

    public void setCurrentItem(int currentItem) {
        this.currentItem = currentItem;
        invalidateView();
    }

    public void setMovePercent(int currentItem, float movePercent) {
        this.movePercent = movePercent;
        this.currentItem = currentItem;
        invalidateView();
    }

    public void setDirection(@Direction int direction) {
        this.direction = direction;
    }

    @IntDef({LEFT, RIGHT, NONE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Direction {
    }
}
