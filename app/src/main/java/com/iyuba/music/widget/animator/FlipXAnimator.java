package com.iyuba.music.widget.animator;

import android.view.View;

import com.daimajia.androidanimations.library.BaseViewAnimator;
import com.nineoldandroids.animation.ObjectAnimator;

public class FlipXAnimator extends BaseViewAnimator {
    @Override
    public void prepare(View target) {
        getAnimatorAgent().playTogether(
                ObjectAnimator.ofFloat(target, "rotationX", 0, 45, 90, 135, 180),
                ObjectAnimator.ofFloat(target, "alpha", 1, 0.75f, 0.5f, 0.25f, 0)
        );
    }
}