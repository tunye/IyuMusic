package com.iyuba.music.listener;

/**
 * Created by 10202 on 2015/9/30.
 */
public interface IProtocolResponse<T> {
    void onNetError(String msg);

    void onServerError(String msg);

    void response(T object);

}
