package com.iyuba.music.receiver;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.network.NetWorkState;
import com.buaa.ct.core.network.NetWorkType;
import com.buaa.ct.core.network.PingIPThread;
import com.buaa.ct.core.util.GetAppColor;
import com.buaa.ct.pudding.util.PuddingBuilder;
import com.iyuba.music.R;
import com.iyuba.music.util.Utils;

public class NetWorkChangeBroadcastReceiver extends BroadcastReceiver {

    public NetWorkChangeBroadcastReceiver() {
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String oldState = NetWorkState.getInstance().getNetWorkState();
        String netWorkState = NetWorkType.getNetworkType(context);
        NetWorkState.getInstance().setNetWorkState(netWorkState);
        if (!netWorkState.equals(oldState)) {
            if (TextUtils.equals(netWorkState, NetWorkState.NO_NET)) {
                setNetwork(context);
            } else if (TextUtils.equals(oldState, NetWorkState.WIFI)) {
                wifiCut(context);
            } else if (TextUtils.equals(netWorkState, NetWorkState.WIFI)) {
                PingIPThread pingIPThread = new PingIPThread(new PingIPThread.PingResult() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void fail() {
                        NetWorkState.getInstance().setNetWorkState(NetWorkState.WIFI_NONET);
                        checkWifiSignIn(context);
                    }
                });
                pingIPThread.start();
            }
        }
    }

    private void setNetwork(Context context) {
        Activity curActivity = Utils.getMusicApplication().getForeground();
        if (!(curActivity instanceof AppCompatActivity)) {
            return;
        }
        final AppCompatActivity curAppcompatActivity = (AppCompatActivity) curActivity;
        new PuddingBuilder().setTitleText(context.getString(R.string.app_name))
                .setSubTitleText(context.getString(R.string.net_no_net))
                .setTitleColor(GetAppColor.getInstance().getAppColorAccent())
                .setSubTitleColor(GetAppColor.getInstance().getAppColorLight())
                .setEnableSwipeDismiss()
                .setEnableIconAnimation()
                .setPositive(context.getString(R.string.sure), R.style.PuddingButton, new INoDoubleClick() {
                    @Override
                    public void activeClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_SETTINGS);
                        curAppcompatActivity.startActivity(intent);
                    }
                })
                .setNegative(context.getString(R.string.cancel), R.style.PuddingButton, new INoDoubleClick() {
                    @Override
                    public void activeClick(View v) {
                    }
                })
                .create(curAppcompatActivity).show();

    }

    private void checkWifiSignIn(Context context) {
        Activity curActivity = Utils.getMusicApplication().getForeground();
        if (!(curActivity instanceof AppCompatActivity)) {
            return;
        }
        final AppCompatActivity curAppcompatActivity = (AppCompatActivity) curActivity;
        new PuddingBuilder().setTitleText(context.getString(R.string.app_name))
                .setSubTitleText(context.getString(R.string.net_wifi_sign_in))
                .setTitleColor(GetAppColor.getInstance().getAppColorAccent())
                .setSubTitleColor(GetAppColor.getInstance().getAppColorLight())
                .setEnableSwipeDismiss()
                .setEnableIconAnimation()
                .setPositive(context.getString(R.string.net_sign_in), R.style.PuddingButton, new INoDoubleClick() {
                    @Override
                    public void activeClick(View v) {
                        try {
                            Intent intent = new Intent();
                            intent.setAction("android.intent.action.VIEW");
                            Uri content_url = Uri.parse("http://m.baidu.com");
                            intent.setData(content_url);
                            curAppcompatActivity.startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                })
                .setNegative(context.getString(R.string.cancel), R.style.PuddingButton, new INoDoubleClick() {
                    @Override
                    public void activeClick(View v) {
                    }
                })
                .create(curAppcompatActivity).show();

    }

    private void wifiCut(Context context) {
        Activity curActivity = Utils.getMusicApplication().getForeground();
        if (!(curActivity instanceof AppCompatActivity)) {
            return;
        }
        final AppCompatActivity curAppcompatActivity = (AppCompatActivity) curActivity;
        new PuddingBuilder().setTitleText(context.getString(R.string.app_name))
                .setSubTitleText(context.getString(R.string.net_cut_wifi))
                .setTitleColor(GetAppColor.getInstance().getAppColorAccent())
                .setSubTitleColor(GetAppColor.getInstance().getAppColorLight())
                .setEnableSwipeDismiss()
                .setEnableIconAnimation()
                .create(curAppcompatActivity).show();
    }
}