package com.iyuba.music.listener;

/**
 * Created by 10202 on 2015/12/22.
 */
public interface IPlayerListener {
    void onPrepare();

    void onBufferChange(int buffer);

    void onFinish();

    void onError();
}
