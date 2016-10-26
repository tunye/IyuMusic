package com.iyuba.music.widget.SwipeBack;

import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;


/**
 * Created by Eric on 15/3/3.
 */
public class SwipeBackFragmentActivity extends FragmentActivity implements SwipeBackLayout.SwipeBackListener {

    private SwipeBackLayout swipeBackLayout;
    private ImageView ivShadow;
    private OnSwipeBackFinish finish;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        swipeBackLayout.addView(view);
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.setOnSwipeBackListener(this);
        ivShadow = new ImageView(this);
        ivShadow.setBackgroundColor(0x7f000000);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        container.addView(ivShadow, params);
        container.addView(swipeBackLayout);
        return container;
    }

    public void setDragEdge(SwipeBackLayout.DragEdge dragEdge) {
        swipeBackLayout.setDragEdge(dragEdge);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackLayout;
    }

    public void setOnSwipeBackFinishListener(OnSwipeBackFinish finish) {
        this.finish = finish;
    }

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        ivShadow.setAlpha(1 - fractionScreen);
        if (fractionScreen == 1) {
            finish.swipeBackFinish();
        }
    }

    public interface OnSwipeBackFinish {
        void swipeBackFinish();
    }

}
