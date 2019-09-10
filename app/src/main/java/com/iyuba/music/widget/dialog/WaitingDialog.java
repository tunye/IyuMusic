package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.buaa.ct.core.util.GetAppColor;
import com.iyuba.music.R;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by 10202 on 2015/10/26.
 */
public class WaitingDialog {

    public static IyubaDialog create(Context context, String message) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.waitting, null);

        TextView animationMessage = layout.findViewById(R.id.waitting_text);
        if (TextUtils.isEmpty(message)) {
            animationMessage.setVisibility(View.GONE);
        } else {
            animationMessage.setText(message);
        }
        AVLoadingIndicatorView loading = layout.findViewById(R.id.waitting_animation);
        loading.setIndicatorColor(GetAppColor.getInstance().getAppColor());
        return new IyubaDialog(context, layout, false);
    }
}
