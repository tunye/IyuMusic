package com.iyuba.music.entity;

/**
 * Created by 10202 on 2015/10/8.
 */
public class BaseListEntity {
    private State state;
    private int totalCount;
    private int curPage;
    private int totalPage;
    private boolean isLastPage;
    private Object data;

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
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

    public enum State {SUCCESS, FAIL, ERROR}
}
