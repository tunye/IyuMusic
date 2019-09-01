package com.iyuba.music.widget.seekbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;

/**
 * Created by etiennelawlor on 7/4/16.
 */

public class DiscreteSlider extends FrameLayout {

    // region Views
    private DiscreteSliderBackdrop discreteSliderBackdrop;
    private DiscreteSeekBar discreteSeekBar;
    // endregion

    // region Member Variables
    private int tickMarkCount;
    private float tickMarkRadius;
    private OnDiscreteSliderChangeListener onDiscreteSliderChangeListener;
    private int discreteSeekBarLeftPadding = RuntimeManager.getInstance().dip2px(32);
    private int discreteSeekBarRightPadding = RuntimeManager.getInstance().dip2px(32);

    // endregion

    // region Constructors
    public DiscreteSlider(Context context) {
        super(context);
        init(context, null);
    }
    // endregion

    public DiscreteSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DiscreteSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // region Helper Methods
    private void init(Context context, AttributeSet attrs) {
        TypedArray attributeArray = context.obtainStyledAttributes(attrs, R.styleable.DiscreteSlider);
        float horizontalBarThickness;
        int backdropFillColor;
        int backdropStrokeColor;
        float backdropStrokeWidth;
        Drawable thumb;
        Drawable progressDrawable;
        try {
            tickMarkCount = attributeArray.getInteger(R.styleable.DiscreteSlider_tickMarkCount, 5);
            tickMarkRadius = attributeArray.getDimension(R.styleable.DiscreteSlider_tickMarkRadius, 8);
            horizontalBarThickness = attributeArray.getDimension(R.styleable.DiscreteSlider_horizontalBarThickness, 4);
            backdropFillColor = attributeArray.getColor(R.styleable.DiscreteSlider_backdropFillColor, Color.GRAY);
            backdropStrokeColor = attributeArray.getColor(R.styleable.DiscreteSlider_backdropStrokeColor, Color.GRAY);
            backdropStrokeWidth = attributeArray.getDimension(R.styleable.DiscreteSlider_backdropStrokeWidth, 1);
            thumb = attributeArray.getDrawable(R.styleable.DiscreteSlider_thumb);
            progressDrawable = attributeArray.getDrawable(R.styleable.DiscreteSlider_progressDrawable);
        } finally {
            attributeArray.recycle();
        }

        View view = inflate(context, R.layout.discrete_slider, this);
        discreteSliderBackdrop = (DiscreteSliderBackdrop) view.findViewById(R.id.discrete_slider_backdrop);
        discreteSliderBackdrop.setTickMarkCount(tickMarkCount);
        discreteSliderBackdrop.setTickMarkRadius(tickMarkRadius);
        discreteSliderBackdrop.setHorizontalBarThickness(horizontalBarThickness);
        discreteSliderBackdrop.setBackdropFillColor(backdropFillColor);
        discreteSliderBackdrop.setBackdropStrokeColor(backdropStrokeColor);
        discreteSliderBackdrop.setBackdropStrokeWidth(backdropStrokeWidth);

        discreteSeekBar = (DiscreteSeekBar) view.findViewById(R.id.discrete_seek_bar);
        discreteSeekBar.setTickMarkCount(tickMarkCount);
        discreteSeekBar.setPadding(discreteSeekBarLeftPadding, 0, discreteSeekBarRightPadding, 0);
        if (thumb != null)
            discreteSeekBar.setThumb(thumb);
        if (progressDrawable != null)
            discreteSeekBar.setProgressDrawable(progressDrawable);
        discreteSeekBar.setOnDiscreteSeekBarChangeListener(new DiscreteSeekBar.OnDiscreteSeekBarChangeListener() {
            @Override
            public void onPositionChanged(int position) {
                if (onDiscreteSliderChangeListener != null) {
                    onDiscreteSliderChangeListener.onPositionChanged(position);
                }
            }
        });
    }
    // endregion

    public void setHorizontalBarThickness(float horizontalBarThickness) {
        discreteSliderBackdrop.setHorizontalBarThickness(horizontalBarThickness);
    }

    public void setBackdropFillColor(int backdropFillColor) {
        discreteSliderBackdrop.setBackdropFillColor(backdropFillColor);
    }

    public void setBackdropStrokeColor(int backdropStrokeColor) {
        discreteSliderBackdrop.setBackdropStrokeColor(backdropStrokeColor);
    }

    public void setBackdropStrokeWidth(float backdropStrokeWidth) {
        discreteSliderBackdrop.setBackdropStrokeWidth(backdropStrokeWidth);
    }

    public void setThumb(Drawable thumb) {
        discreteSeekBar.setThumb(thumb);
    }

    public void setProgressDrawable(Drawable progressDrawable) {
        discreteSeekBar.setProgressDrawable(progressDrawable);
    }

    public void setOnDiscreteSliderChangeListener(OnDiscreteSliderChangeListener onDiscreteSliderChangeListener) {
        this.onDiscreteSliderChangeListener = onDiscreteSliderChangeListener;
    }

    public int getTickMarkCount() {
        return tickMarkCount;
    }

    public void setTickMarkCount(int tickMarkCount) {
        discreteSliderBackdrop.setTickMarkCount(tickMarkCount);
        discreteSeekBar.setTickMarkCount(tickMarkCount);
    }

    public float getTickMarkRadius() {
        return tickMarkRadius;
    }

    public void setTickMarkRadius(float tickMarkRadius) {
        discreteSliderBackdrop.setTickMarkRadius(tickMarkRadius);
    }

    public void setSelected(int position) {
        discreteSeekBar.setPosition(position);
    }

    // region Interfaces
    public interface OnDiscreteSliderChangeListener {
        void onPositionChanged(int position);
    }

    // endregion
}
