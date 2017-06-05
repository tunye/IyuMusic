package com.iyuba.music.activity;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.Mathematics;


/**
 * Created by 10202 on 2015/10/23.
 */
public abstract class BaseInputActivity extends BaseActivity {

    @Override
    protected void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
                Rect outRect = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(outRect);
                Mathematics.setMargins(toolBarLayout, 0, RuntimeManager.getWindowHeight() - outRect.height(), 0, 0);
            }
        }
    }
}
