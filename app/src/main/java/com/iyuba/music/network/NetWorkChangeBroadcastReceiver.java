package com.iyuba.music.network;

/**
 * Created by 10202 on 2015/10/8.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String oldState = NetWorkState.getInstance().getNetWorkState();
        String netWorkState = NetWorkType.getNetworkType(context);
        NetWorkState.getInstance().setNetWorkState(netWorkState);
        if (netWorkState.equals(oldState)) {

        } else {
            if (TextUtils.equals(netWorkState, "NO-NET")) {
                Toast.makeText(context, "网络不可用，如果继续，请先设置网络！", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.equals(oldState, "WIFI")) {
                Toast.makeText(context, "网络仍然连接中，但WIFI已断开！", Toast.LENGTH_LONG).show();
            }
        }
    }
}
