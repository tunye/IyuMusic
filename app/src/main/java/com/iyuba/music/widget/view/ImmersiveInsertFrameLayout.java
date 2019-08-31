package com.iyuba.music.widget.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.Utils;

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

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Window window = null;
        if (getContext() instanceof Activity) {
            window = ((Activity) getContext()).getWindow();
        }
        boolean softInputModeInvisible = window != null && window.getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE;
        if (softInputModeInvisible) {
            Rect outRect = new Rect();
            window.getDecorView().getWindowVisibleDisplayFrame(outRect);
            Utils.setMargins(this, 0, RuntimeManager.getWindowHeight() - outRect.height(), 0, 0);
        }
    }
}
