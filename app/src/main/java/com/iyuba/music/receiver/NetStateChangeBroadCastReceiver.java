package com.iyuba.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

/**
 * Created by 10202 on 2015/11/3.
 */
public class NetStateChangeBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        setUINetwork(context, intent.getStringExtra("dialog_message"));
    }

    private void setUINetwork(final Context context, String message) {
        final MyMaterialDialog mMaterialDialog = new MyMaterialDialog(context);
        mMaterialDialog.setTitle("网络提示信息")
                .setMessage(message)
                .setPositiveButton("设置", new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        mMaterialDialog.dismiss();
                        Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("关闭", new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        mMaterialDialog.dismiss();
                    }
                });
        mMaterialDialog.show();
    }

}
