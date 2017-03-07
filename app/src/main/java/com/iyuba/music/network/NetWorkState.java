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
    public static final String WIFI = "WIFI";
    public static final String WIFI_NONET = "WIFI_NONET";
    public static final String FOURG = "4G";
    public static final String NO_NET = "NO-NET";
    public static final String TWOG = "2G";

    private String netWorkState;

    private NetWorkState() {

    }

    public static NetWorkState getInstance() {
        return InstanceHelper.instance;
    }

    public boolean isConnectByCondition(int stateIndex) {
        boolean result = true;
        switch (stateIndex) {
            case ONLY_WIFI:
                result = TextUtils.equals(netWorkState, WIFI);
                break;
            case EXCEPT_2G_3G:
                result = TextUtils.equals(netWorkState, WIFI) || TextUtils.equals(netWorkState, FOURG);
                break;
            case EXCEPT_2G:
                result = !(TextUtils.equals(netWorkState, NO_NET) || TextUtils.equals(netWorkState, TWOG) || TextUtils.equals(netWorkState, WIFI_NONET));
                break;
            case ALL_NET:
                result = !TextUtils.equals(netWorkState, NO_NET) && !TextUtils.equals(netWorkState, WIFI_NONET);
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

    private static class InstanceHelper {
        private static NetWorkState instance = new NetWorkState();
    }
}
