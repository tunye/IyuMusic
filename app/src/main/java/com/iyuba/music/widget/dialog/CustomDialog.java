package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringRes;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.activity.AboutActivity;
import com.iyuba.music.activity.LoginActivity;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.request.apprequest.VisitorIdRequest;

/**
 * Created by 10202 on 2015/12/4.
 */
public class CustomDialog {
    public static void showLoginDialog(final Context context, boolean useVisitorMode, final IOperationFinish finish) {
        if (useVisitorMode) {
            if (AccountManager.getInstance().needGetVisitorID()) {
                VisitorIdRequest.exeRequest(VisitorIdRequest.generateUrl(), new IProtocolResponse() {
                    @Override
                    public void onNetError(String msg) {
                        showLogin(context, finish);
                    }

                    @Override
                    public void onServerError(String msg) {
                        showLogin(context, finish);
                    }

                    @Override
                    public void response(Object object) {
                        finish.finish();
                    }
                });
            }
        } else {
            showLogin(context, finish);
        }
    }

    private static void showLogin(final Context context, final IOperationFinish finish) {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.login_login);
        dialog.setMessage(R.string.personal_no_login);
        dialog.setPositiveButton(R.string.login_login, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.launch(context, new IOperationResult() {
                    @Override
                    public void success(Object object) {
                        finish.finish();
                    }

                    @Override
                    public void fail(Object object) {
                    }
                });
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public static void saveChangeDialog(final Context context, final IOperationResult iOperationResult) {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(R.string.dialog_save_change);
        dialog.setPositiveButton(R.string.dialog_save, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iOperationResult.success(null);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
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
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.app_name);
        dialog.setMessage(context.getString(R.string.about_update_message, para[0]));
        dialog.setPositiveButton(R.string.about_update_accept, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                ConfigManager.getInstance().putInt("updateVersion", Integer.parseInt(para[1]));
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
                ConfigManager.getInstance().putInt("updateVersion", Integer.parseInt(para[1]));
            }
        });
        dialog.show();
    }

    public static void clearDownload(Context context, @StringRes int hintCode, final IOperationResult result) {
        final MyMaterialDialog dialog = new MyMaterialDialog(context);
        dialog.setTitle(R.string.article_clear_all);
        dialog.setMessage(hintCode);
        dialog.setPositiveButton(R.string.article_search_clear_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.success(null);
                dialog.dismiss();
            }
        });
        dialog.setNegativeButton(R.string.app_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                result.fail(null);
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
