package com.iyuba.music.widget.SwipeRefreshLayout;

/**
 * @author xutao
 */
public enum MySwipeRefreshLayoutDirection {

    TOP(0), // 只有下拉刷新
    BOTTOM(1), // 只有加载更多
    BOTH(2);// 全都有

    private int mValue;

    MySwipeRefreshLayoutDirection(int value) {
        this.mValue = value;
    }

    public static MySwipeRefreshLayoutDirection getFromInt(int value) {
        for (MySwipeRefreshLayoutDirection direction : MySwipeRefreshLayoutDirection
                .values()) {
            if (direction.mValue == value) {
                return direction;
            }
        }
        return BOTH;
    }

}
