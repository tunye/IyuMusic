package com.iyuba.music.entity;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 10202 on 2015/10/8.
 */
public class BaseListEntity<T> {
    public static final int SUCCESS = 0x01;
    public static final int FAIL = 0x02;
    public static final int ERROR = 0x03;
    public static final int NODATA = 0x04;
    @State
    private int state;
    private int totalCount;
    private int curPage;
    private int totalPage;
    private boolean isLastPage;
    private T data;

    public static boolean isSuccess(BaseListEntity result) {
        return result.getState() == SUCCESS;
    }

    public static boolean isFail(BaseListEntity result) {
        return result.getState() == FAIL;
    }

    public static boolean isError(BaseListEntity result) {
        return result.getState() == ERROR;
    }

    public static boolean isNodata(BaseListEntity result) {
        return result.getState() == NODATA;
    }

    @State
    public int getState() {
        return state;
    }

    public void setState(@State int state) {
        this.state = state;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public int getCurPage() {
        return curPage;
    }

    public void setCurPage(int curPage) {
        this.curPage = curPage;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @IntDef({SUCCESS, FAIL, ERROR, NODATA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface State {
    }
}
