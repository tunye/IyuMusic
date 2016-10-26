package com.iyuba.music.widget.seekbar;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.SeekBar;

/**
 * Created by etiennelawlor on 7/4/16.
 */

public class DiscreteSeekBar extends AppCompatSeekBar {

    // region Constants
    private static String PROGRESS_PROPERTY = "progress";
    private static int MULTIPLIER = 100;
    // endregion

    private float stepSize = 0.0f;
    private int superOldProgress = 0;
    // This counter detects if the user clicked the SeekBar or dragged the SeekBar
    // If this counter exceeds 1 then the user dragged the SeekBar otherwise
    // the user clicked the SeekBar
    private int fromUserCount = 0;
    private OnDiscreteSeekBarChangeListener onDiscreteSeekBarChangeListener;
    // endregion

    // region Constructors
    public DiscreteSeekBar(Context context) {
        super(context);
        init(context, null);
    }
    // endregion

    public DiscreteSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public DiscreteSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    // region Helper Methods
    private void init(Context context, AttributeSet attrs) {
        setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                superOldProgress = getProgress();
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    fromUserCount += 1;
            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                int oldProgress = seekBar.getProgress();
                final int newProgress;
                if ((oldProgress % stepSize) >= stepSize / 2F) {
                    newProgress = (int) (((oldProgress / (int) stepSize) + 1) * stepSize);
                } else {
                    newProgress = (int) (((oldProgress / (int) stepSize)) * stepSize);
                }

                if (fromUserCount > 1) { // SeekBar Dragged
                    ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, PROGRESS_PROPERTY, oldProgress, newProgress);
                    animation.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                } else { // SeekBar Clicked
                    ObjectAnimator animation = ObjectAnimator.ofInt(seekBar, PROGRESS_PROPERTY, superOldProgress, newProgress);
                    animation.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
                    animation.setInterpolator(new DecelerateInterpolator());
                    animation.start();
                }

                fromUserCount = 0;
                if (onDiscreteSeekBarChangeListener != null) {
                    onDiscreteSeekBarChangeListener.onPositionChanged(newProgress / MULTIPLIER);
                }
            }
        });
    }
    // endregion

    public void setTickMarkCount(int tickMarkCount) {
        int tickMarkCount1 = tickMarkCount < 2 ? 2 : tickMarkCount;
        setMax((tickMarkCount1 - 1) * MULTIPLIER);
        this.stepSize = getMax() / (tickMarkCount1 - 1);
    }

    public void setOnDiscreteSeekBarChangeListener(OnDiscreteSeekBarChangeListener onDiscreteSeekBarChangeListener) {
        this.onDiscreteSeekBarChangeListener = onDiscreteSeekBarChangeListener;
    }

    public void setPosition(int position) {
        if (position > stepSize) {

        } else {
            superOldProgress = getProgress();
            ObjectAnimator animation = ObjectAnimator.ofInt(this, PROGRESS_PROPERTY, superOldProgress, position * MULTIPLIER);
            animation.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
            onDiscreteSeekBarChangeListener.onPositionChanged(position);
        }
    }

    // region Interfaces
    public interface OnDiscreteSeekBarChangeListener {
        void onPositionChanged(int position);
    }
    // endregion


}
