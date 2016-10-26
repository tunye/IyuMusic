package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.AboutActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.manager.ConfigManager;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2015/12/4.
 */
public class CustomDialog {
    public static void showLoginDialog(final Context context) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(R.string.login_login);
        dialog.setMessage(R.string.personal_no_login);
        dialog.setPositiveButton(R.string.login_login, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, LoginActivity.class));
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void saveChangeDialog(final Context context, final IOperationResult iOperationResult) {
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(R.string.dialog_save_change);
        dialog.setPositiveButton(R.string.dialog_save, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOperationResult.success(null);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOperationResult.fail(null);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void updateDialog(final Context context, String object) {
        final String[] para = object.split("@@@");
        final MaterialDialog dialog = new MaterialDialog(context);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(context.getString(R.string.about_update_message, para[0]));
        dialog.setPositiveButton(R.string.about_update_accept, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfigManager.instance.putInt("updateVersion", Integer.parseInt(para[1]));
                Intent intent = new Intent(context, AboutActivity.class);
                intent.putExtra("update", true);
                intent.putExtra("url", para[2]);
                intent.putExtra("version", para[0]);
                context.startActivity(intent);
            }
        });
        dialog.setNegativeButton(R.string.about_update_deny, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfigManager.instance.putInt("updateVersion", Integer.parseInt(para[1]));
            }
        });
        dialog.show();
    }
}
