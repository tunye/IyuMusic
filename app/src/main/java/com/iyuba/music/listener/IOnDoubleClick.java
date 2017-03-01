package com.iyuba.music.listener;

import android.view.MotionEvent;
import android.view.View;

import com.iyuba.music.widget.CustomToast;

/**
 * Created by 10202 on 2015/12/21.
 */
public class IOnDoubleClick implements View.OnTouchListener {
    private int count;
    private long firClick, secClick;
    private String failMsg;
    private IOnClickListener onClickListener;

    public IOnDoubleClick(IOnClickListener onClickListener, String msg) {
        this.onClickListener = onClickListener;
        this.failMsg = msg;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (MotionEvent.ACTION_DOWN == event.getAction()) {
            count++;
            if (count == 1) {
                firClick = System.currentTimeMillis();
                v.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (count == 1) {
                            count = 0;
                            firClick = 0;
                            secClick = 0;
                            CustomToast.getInstance().showToast(failMsg);
                        }
                    }
                }, 1000);
            } else if (count == 2) {
                secClick = System.currentTimeMillis();
                if (secClick - firClick < 790) {
                    onClickListener.onClick(v, "click");
                }
                count = 0;
                firClick = 0;
                secClick = 0;
            }
        }
        return true;
    }
}
