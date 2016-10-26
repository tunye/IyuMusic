package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.iyuba.music.R;

/**
 * Created by 10202 on 2016/4/1.
 */
public class GoImageView extends View {
    private int color;
    private float lineWidth;
    private Direction direction;
    private Paint paint;

    public GoImageView(Context context) {
        this(context, null);
        color = context.getResources().getColor(R.color.background_light);
        lineWidth = 12;
        direction = Direction.LEFT;
        initPaint();
    }

    public GoImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.GoImageView);
        int directionIndex = a.getInt(R.styleable.GoImageView_go_direction, 0);
        switch (directionIndex) {
            case 0:
                direction = Direction.LEFT;
                break;
            case 1:
                direction = Direction.RIGHT;
                break;
            case 2:
                direction = Direction.TOP;
                break;
            case 3:
                direction = Direction.BOTTOM;
                break;
        }
        color = a.getColor(R.styleable.GoImageView_go_color, context.getResources().getColor(R.color.background_light));
        lineWidth = a.getDimension(R.styleable.GoImageView_go_stroke, 12);
        a.recycle();
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        int actualWidth, actualHeight;
        if (width == 0) {
            width = 24;
        } else if (height == 0) {
            height = 24;
        }
        actualWidth = actualHeight = Math.min(width, height);
        switch (direction) {
            case LEFT:
                if (actualHeight == height && actualHeight == width) {
                    canvas.drawLine(width / 4, height, width / 4 * 3 + (int) Math.sqrt(3) / 4 * lineWidth, height / 2 - lineWidth / 4, paint);
                    canvas.drawLine(width / 4, 0, width / 4 * 3 + lineWidth / 4, height / 2 + (int) Math.sqrt(3) / 4 * lineWidth, paint);
                } else if (actualHeight == height) {
                    canvas.drawLine((width - actualWidth) / 2 + actualWidth / 4, height, actualWidth / 4 * 3 + (int) Math.sqrt(3) / 4 * lineWidth, height / 2 - lineWidth / 4, paint);
                    canvas.drawLine((width - actualWidth) / 2 + actualWidth / 4, 0, actualWidth / 4 * 3 + lineWidth / 4, height / 2 + (int) Math.sqrt(3) / 4 * lineWidth, paint);
                } else {
                    canvas.drawLine(width / 4, actualHeight + (height - actualHeight) / 2, width / 4 * 3 + (int) Math.sqrt(3) / 4 * lineWidth, actualHeight / 2 + (height - actualHeight) / 2 - lineWidth / 4, paint);
                    canvas.drawLine(width / 4, (height - actualHeight) / 2, width / 4 * 3 + lineWidth / 4, actualHeight / 2 + (height - actualHeight) / 2 + (int) Math.sqrt(3) / 4 * lineWidth, paint);
                }
                break;
            case RIGHT:
                if (actualHeight == height && actualHeight == width) {
                    canvas.drawLine(width / 4 * 3, height, width / 4 - (int) Math.sqrt(3) / 4 * lineWidth, height / 2 - lineWidth / 4, paint);
                    canvas.drawLine(width / 4 * 3, 0, width / 4 - lineWidth / 4, height / 2 + (int) Math.sqrt(3) / 4 * lineWidth, paint);
                } else if (actualHeight == height) {
                    canvas.drawLine((width - actualWidth) / 2 + actualWidth / 4 * 3, height, (width - actualWidth) / 2 + actualWidth / 4 - (int) Math.sqrt(3) / 4 * lineWidth, height / 2 - lineWidth / 4, paint);
                    canvas.drawLine((width - actualWidth) / 2 + actualWidth / 4 * 3, 0, (width - actualWidth) / 2 + actualWidth / 4 - lineWidth / 4, height / 2 + (int) Math.sqrt(3) / 4 * lineWidth, paint);
                } else {
                    canvas.drawLine(width / 4 * 3, actualHeight + (height - actualHeight) / 2, width / 4 - (int) Math.sqrt(3) / 4 * lineWidth, actualHeight / 2 + (height - actualHeight) / 2 - lineWidth / 4, paint);
                    canvas.drawLine(width / 4 * 3, (height - actualHeight) / 2, width / 4 - lineWidth / 4, actualHeight / 2 + (height - actualHeight) / 2 + (int) Math.sqrt(3) / 4 * lineWidth, paint);
                }
                break;
            case BOTTOM:
                if (actualHeight == height && actualHeight == width) {
                    canvas.drawLine(0, height / 4 * 3, width / 2, height / 4, paint);
                    canvas.drawLine(width, height / 4 * 3, width / 2, height / 4, paint);
                } else if (actualHeight == height) {
                    canvas.drawLine((width - actualWidth) / 2, height / 4 * 3, width / 2, height / 4, paint);
                    canvas.drawLine(width - (width - actualWidth) / 2, height / 4 * 3, width / 2, height / 4, paint);
                } else {
                    canvas.drawLine(0, actualHeight / 4 * 3 + (height - actualHeight) / 2, width / 2, actualHeight / 4 + (height - actualHeight) / 2, paint);
                    canvas.drawLine(width, actualHeight / 4 * 3 + (height - actualHeight) / 2, width / 2, actualHeight / 4 + (height - actualHeight) / 2, paint);
                }
                break;
            case TOP:
                if (actualHeight == height && actualHeight == width) {
                    canvas.drawLine(0, height / 4, width / 2, height / 4 * 3, paint);
                    canvas.drawLine(width, height / 4, width / 2, height / 4 * 3, paint);
                } else if (actualHeight == height) {
                    canvas.drawLine((width - actualWidth) / 2, height / 4, width / 2, height / 4 * 3, paint);
                    canvas.drawLine(width - (width - actualWidth) / 2, height / 4, width / 2, height / 4 * 3, paint);
                } else {
                    canvas.drawLine(0, actualHeight / 4 + (height - actualHeight) / 2, width / 2, actualHeight / 4 * 3 + (height - actualHeight) / 2, paint);
                    canvas.drawLine(width, actualHeight / 4 + (height - actualHeight) / 2, width / 2, actualHeight / 4 * 3 + (height - actualHeight) / 2, paint);
                }
                break;
        }
    }

    private void initPaint() {
        paint = new Paint();
        paint.setAntiAlias(true);//抗锯齿功能
        paint.setColor(color);  //设置画笔颜色
        paint.setStyle(Paint.Style.FILL);//设置填充样式   Style.FILL/Style.FILL_AND_STROKE/Style.STROKE
        paint.setStrokeWidth(lineWidth);//设置画笔宽度
    }

    public enum Direction {TOP, LEFT, RIGHT, BOTTOM}
}
