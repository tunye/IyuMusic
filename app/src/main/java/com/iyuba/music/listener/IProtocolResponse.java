package com.iyuba.music.listener;

/**
 * Created by 10202 on 2015/9/30.
 */
public interface IProtocolResponse {
    void onNetError(String msg);

    void onServerError(String msg);

    void response(Object object);

}
