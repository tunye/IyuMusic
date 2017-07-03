package com.iyuba.music.widget.imageview;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.AttrRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.iyuba.music.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chentong1 on 2017/6/21.
 */

public class MorphButton extends FrameLayout {
    public static final int PLAY_STATE = 1;
    public static final int PAUSE_STATE = 2;
    private static final int pauseDrawable = R.drawable.play;
    private static final int playDrawable = R.drawable.pause;
    @MorphButton.MorphState
    int morphButtonState;
    private ImageView imageView;
    private Drawable pause, play;

    public MorphButton(@NonNull Context context) {
        super(context);
        init(context);
    }
    public MorphButton(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MorphButton(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        pause = getResources().getDrawable(pauseDrawable);
        play = getResources().getDrawable(playDrawable);
        imageView = new ImageView(context);
        imageView.setImageDrawable(pause);
        addView(imageView);
    }

    public void setForegroundColorFilter(int color, PorterDuff.Mode mode) {
        pause.setColorFilter(color, mode);
        play.setColorFilter(color, mode);
    }

    public int getState() {
        return morphButtonState;
    }

    public void setState(int state) {
        morphButtonState = state;
        switch (state) {
            case PLAY_STATE:
                imageView.setImageDrawable(play);
                break;
            case PAUSE_STATE:
                imageView.setImageDrawable(pause);
                break;
        }
    }

    @IntDef({PLAY_STATE, PAUSE_STATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface MorphState {
    }
}

