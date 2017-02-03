package com.iyuba.music.widget.view;

import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;
import com.iyuba.music.R;

/**
 * Created by 10202 on 2017/2/3.
 */

public class AddRippleEffect {
    public static void addRippleEffect(View view) {
        MaterialRippleLayout.on(view)
                .rippleOverlay(true)
                .rippleAlpha(0.2f)
                .rippleDuration(600)
                .rippleColor(view.getContext().getResources().getColor(R.color.text_complementary))
                .rippleHover(true)
                .create();
    }
}
