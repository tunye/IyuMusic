package com.iyuba.music.listener;

/**
 * Created by 10202 on 2015/10/10.
 */
public interface IOperationResult {
    void success(Object object);

    void fail(Object object);
}
