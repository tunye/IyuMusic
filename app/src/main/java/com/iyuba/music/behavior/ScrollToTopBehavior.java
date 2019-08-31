package com.iyuba.music.behavior;

import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;

/**
 * Created by 10202 on 2016/5/18.
 */
public class ScrollToTopBehavior extends CoordinatorLayout.Behavior<View> {
    private int offsetTotal = 0;

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View directTargetChild, @NonNull View target, int nestedScrollAxes) {
        return true;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout, @NonNull View child, @NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        offset(child, dyConsumed);
    }

    private void offset(View child, int dy) {
        int old = offsetTotal;
        int top = offsetTotal - dy;
        top = Math.max(top, -child.getHeight());
        top = Math.min(top, 0);
        offsetTotal = top;
        if (old == offsetTotal) {
            return;
        }
        int delta = offsetTotal - old;
        child.offsetTopAndBottom(delta);
    }

}