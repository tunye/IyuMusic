package com.iyuba.music.widget.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ScrollView;

/**
 * 下拉控件
 *
 * @author 陈彤
 */
public class FlexibleView extends FrameLayout {
    private final static int MAX_MOVE = 480;
    private int mLastMotionY;
    private int actualDistance;
    private ScrollView scrollView;
    private AdapterView<?> adapterView;
    private WebView webView;
    private View customView;
    Animation.AnimationListener animationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            if (webView != null) {
                webView.clearAnimation();
                webView.setY(0);
            } else if (scrollView != null) {
                scrollView.clearAnimation();
                scrollView.setY(0);
            } else if (adapterView != null) {
                adapterView.clearAnimation();
                adapterView.setY(0);
            } else {
                customView.clearAnimation();
                customView.setY(0);
            }
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };

    public FlexibleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlexibleView(Context context) {
        this(context, null);
    }

    private void init() {
        // Load all of the animations we need in code rather than through XML
        actualDistance = 0;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // footer view 在此添加保证添加到linearlayout中的最后
        initContentAdapterView();
    }

    private void initContentAdapterView() {
        int count = getChildCount();
        View view;
        for (int i = 0; i < count; i++) {
            view = getChildAt(i);
            if (view instanceof AdapterView<?>) {
                adapterView = (AdapterView<?>) view;
            }
            if (view instanceof ScrollView) {
                scrollView = (ScrollView) view;
            }
            if (view instanceof WebView) {
                webView = (WebView) view;
            } else {
                customView = view;
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        int y = (int) e.getRawY();
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 首先拦截down事件,记录y坐标
                mLastMotionY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                // deltaY > 0 是向下运动,< 0是向上运动
                int deltaY = y - mLastMotionY;
                if (isRefreshViewScroll(deltaY)) {
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int y = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                int deltaY = y - mLastMotionY;
                moveTargetView(deltaY);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                resetState();
                break;
        }
        return super.onTouchEvent(event);
    }

    private boolean isRefreshViewScroll(int deltaY) {
        if (scrollView != null) {
            // 子scroll view滑动到最顶端
            if (deltaY > 0 && scrollView.getScrollY() == 0) {
                return true;
            } else {
                return false;
            }
        } else if (webView != null) {
            // 子scroll view滑动到最顶端
            if (deltaY > 0 && webView.getScrollY() == 0) {
                return true;
            } else {
                return false;
            }
        } else if (adapterView != null) {
            // 子view(ListView or GridView)滑动到最顶端
            if (deltaY > 0) {
                View child = adapterView.getChildAt(0);
                if (child == null) {
                    // 如果mAdapterView中没有数据,不拦截
                    return false;
                }
                if (adapterView.getFirstVisiblePosition() == 0
                        && child.getTop() == 0) {
                    return true;
                }
                int top = child.getTop();
                int padding = adapterView.getPaddingTop();
                if (adapterView.getFirstVisiblePosition() == 0
                        && Math.abs(top - padding) <= 8) {// 这里之前用3可以判断,但现在不行,还没找到原因
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void moveTargetView(int deltaY) {
        deltaY = Math.min(MAX_MOVE * deltaY / getContext().getResources().getDisplayMetrics().heightPixels, MAX_MOVE);
        actualDistance = Math.max(deltaY, actualDistance);
        if (scrollView != null) {
            scrollView.setY(deltaY);
        } else if (webView != null) {
            webView.setY(deltaY);
        } else if (adapterView != null) {
            adapterView.setY(deltaY);
        } else {
            customView.setY(deltaY);
        }
    }

    private void resetState() {
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -actualDistance);
        translateAnimation.setInterpolator(new AccelerateInterpolator());
        translateAnimation.setDuration(600);
        translateAnimation.setAnimationListener(animationListener);
        actualDistance = 0;
        if (webView != null) {
            webView.startAnimation(translateAnimation);
        } else if (scrollView != null) {
            scrollView.startAnimation(translateAnimation);
        } else if (adapterView != null) {
            adapterView.startAnimation(translateAnimation);
        } else {
            customView.startAnimation(translateAnimation);
        }
    }
}
