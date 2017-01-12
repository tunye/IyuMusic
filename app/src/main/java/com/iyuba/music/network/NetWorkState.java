package com.iyuba.music.network;

import android.text.TextUtils;

/**
 * Created by 10202 on 2015/10/8.
 */
public class NetWorkState {
    public static final int ONLY_WIFI = 0;
    public static final int EXCEPT_2G_3G = 1;
    public static final int EXCEPT_2G = 2;
    public static final int ALL_NET = 3;
    private static NetWorkState instance;
    private String netWorkState;

    private NetWorkState() {
    }

    public static NetWorkState getInstance() {
        if (instance == null) {
            instance = new NetWorkState();
        }
        return instance;
    }

    public boolean isConnectByCondition(int stateIndex) {
        boolean result = true;
        switch (stateIndex) {
            case ONLY_WIFI:
                result = TextUtils.equals(netWorkState, "WIFI");
                break;
            case EXCEPT_2G_3G:
                result = TextUtils.equals(netWorkState, "WIFI") || TextUtils.equals(netWorkState, "4G");
                break;
            case EXCEPT_2G:
                result = !(TextUtils.equals(netWorkState, "NO-NET") || TextUtils.equals(netWorkState, "2G"));
                break;
            case ALL_NET:
                result = !TextUtils.equals(netWorkState, "NO-NET");
                break;
        }
        return result;
    }

    public String getNetWorkState() {
        return netWorkState;
    }

    public void setNetWorkState(String netWorkState) {
        this.netWorkState = netWorkState;
    }
}
