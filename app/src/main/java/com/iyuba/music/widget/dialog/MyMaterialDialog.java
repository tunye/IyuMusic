package com.iyuba.music.widget.dialog;

import android.app.AlertDialog;
import android.content.Context;

import com.iyuba.music.util.ImmersiveManager;

import java.lang.reflect.Field;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by chentong1 on 2017/6/2.
 */

public class MyMaterialDialog extends MaterialDialog {

    public MyMaterialDialog(Context context) {
        super(context);
    }

    @Override
    public void show() {
        super.show();
        try {
            Field field = MaterialDialog.class.getDeclaredField("mAlertDialog");
            field.setAccessible(true);
            AlertDialog dialog = (AlertDialog) field.get(this);
            ImmersiveManager.getInstance().updateImmersiveStatus(dialog);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
