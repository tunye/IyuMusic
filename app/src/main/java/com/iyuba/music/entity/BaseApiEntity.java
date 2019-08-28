package com.iyuba.music.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 10202 on 2016/3/17.
 */
public class BaseApiEntity<T> {
    public static final int SUCCESS = 0x01;
    public static final int FAIL = 0x02;
    public static final int ERROR = 0x03;
    @State
    private int state;
    private String message;
    private String value;
    private T data;

    public static boolean isSuccess(BaseApiEntity result) {
        return result.getState() == SUCCESS;
    }

    public static boolean isFail(BaseApiEntity result) {
        return result.getState() == FAIL;
    }

    public static boolean isError(BaseApiEntity result) {
        return result.getState() == ERROR;
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @IntDef({SUCCESS, FAIL, ERROR})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }
}
