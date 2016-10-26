package com.iyuba.music.widget.boundnumber;

/**
 * Created by ct on 2015/8/19.
 */
public interface RiseNumberBase {
    void start();

    RiseNumberTextView withNumber(float number);

    RiseNumberTextView withNumber(float number, boolean flag);

    RiseNumberTextView withNumber(int number, boolean flag);

    RiseNumberTextView withNumber(int number);

    RiseNumberTextView setDuration(long duration);

    void setOnEnd(RiseNumberTextView.EndListener callback);
}
