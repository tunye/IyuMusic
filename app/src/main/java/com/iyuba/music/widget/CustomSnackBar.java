package com.iyuba.music.widget;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.iyuba.music.util.GetAppColor;

/**
 * Created by 10202 on 2017/3/7.
 */

public class CustomSnackBar {
    private static final int color_danger = 0xFFDB4E46;
    private static final int action_color = 0xFFFFFFFF;
    private static int color_info = 0xFF009FE8;
    private static int color_warning = 0xFFFAAA3C;
    private Snackbar mSnackbar;

    private CustomSnackBar(Snackbar snackbar) {
        mSnackbar = snackbar;
    }

    public static CustomSnackBar make(View view, String text) {
        Snackbar snackbar = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
        setAppColor(view.getContext());
        return new CustomSnackBar(snackbar);
    }

    private static void setAppColor(Context context) {
        color_info = GetAppColor.getInstance().getAppColor(context);
        color_warning = GetAppColor.getInstance().getAppColorAccent(context);
    }

    private View getSnackBarLayout(Snackbar snackbar) {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private Snackbar setSnackBarBackColor(int colorId) {
        View snackBarView = getSnackBarLayout(mSnackbar);
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(colorId);
        }
        return mSnackbar;
    }

    public void info() {
        setSnackBarBackColor(color_info);
        show();
    }

    public void info(String actionText, View.OnClickListener listener) {
        setSnackBarBackColor(color_info);
        show(actionText, listener);
    }

    public void warning() {
        setSnackBarBackColor(color_warning);
        show();
    }

    public void warning(String actionText, View.OnClickListener listener) {
        setSnackBarBackColor(color_warning);
        show(actionText, listener);
    }

    public void danger() {
        setSnackBarBackColor(color_danger);
        show();
    }

    public void danger(String actionText, View.OnClickListener listener) {
        mSnackbar.setDuration(Snackbar.LENGTH_INDEFINITE);
        setSnackBarBackColor(color_danger);
        show(actionText, listener);
    }

    public void show() {
        mSnackbar.show();
    }

    public void show(String actionText, View.OnClickListener listener) {
        mSnackbar.setActionTextColor(action_color);
        mSnackbar.setAction(actionText, listener).show();
    }
}
