package com.iyuba.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/11/3.
 */
public class NetStateChangeBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setUINetwork(context, intent.getStringExtra("dialog_message"));
    }

    private void setUINetwork(final Context context, String message) {
        final MaterialDialog mMaterialDialog = new MaterialDialog(context);
        mMaterialDialog.setTitle("网络提示信息")
                .setMessage(message)
                .setPositiveButton("设置", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        Intent intent;
                        // 先判断当前系统版本
                        if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
                            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        } else {
                            intent = new Intent();
                            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
                        }
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("关闭", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

}
