package com.iyuba.music.entity;

/**
 * Created by 10202 on 2016/3/17.
 */
public class BaseApiEntity {
    private State state;
    private String message;
    private String value;
    private Object data;

    public State getState() {
        return state;
    }

    public void setState(State state) {
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

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public enum State {SUCCESS, FAIL, ERROR}
}
