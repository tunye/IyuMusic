package com.iyuba.music.widget.view;

import android.view.View;

import com.iyuba.music.R;

/**
 * Created by 10202 on 2017/2/3.
 */

public class AddRippleEffect {
    public static void addRippleEffect(View view) {
        addRippleEffect(view, 150);
    }

    public static void addRippleEffect(View view, int duration) {
        MaterialRippleLayout.on(view)
                .rippleOverlay(true)
                .rippleAlpha(0.2f)
                .rippleDuration(duration)
                .rippleColor(view.getContext().getResources().getColor(R.color.text_complementary))
                .rippleHover(true)
                .create();
    }
}
