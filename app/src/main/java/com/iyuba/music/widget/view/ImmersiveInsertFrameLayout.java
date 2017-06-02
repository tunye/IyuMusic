package com.iyuba.music.widget.view;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.WindowInsets;
import android.widget.FrameLayout;

/**
 * 软键盘沉浸式出现无法自适应
 * 通过继承FrameLayout解决
 */

public class ImmersiveInsertFrameLayout extends FrameLayout {
    public ImmersiveInsertFrameLayout(Context context) {
        this(context, null);
    }

    public ImmersiveInsertFrameLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setFitsSystemWindows(true);
    }

    protected final boolean fitSystemWindows(Rect rect) {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            rect.left = 0;
            rect.top = 0;
            rect.right = 0;
        }
        return super.fitSystemWindows(rect);
    }

    public final WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return super.onApplyWindowInsets(windowInsets.replaceSystemWindowInsets(0, 0, 0, windowInsets.getSystemWindowInsetBottom()));
        }
        return windowInsets;
    }
}
