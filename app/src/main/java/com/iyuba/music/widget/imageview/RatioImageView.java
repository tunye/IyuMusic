package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 2016/3/25.
 */
public class RatioImageView extends AppCompatImageView {
    private float ratio = 16f / 9;

    public RatioImageView(Context context) {
        super(context);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setRatio(float ratio) {
        this.ratio = ratio;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        if (width > 0) {
            height = (int) (width * ratio);
        }
        setMeasuredDimension(width, height);
    }
}
