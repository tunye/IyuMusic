package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;
import com.iyuba.music.util.GetAppColor;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by 10202 on 2015/10/26.
 */
public class WaitingDialog {

    public static class Builder {

        private Context context;
        private String message;

        public Builder(Context context) {
            this.context = context;
            this.message = null;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Dialog create() {
            LayoutInflater inflater = LayoutInflater.from(context);
            View layout = inflater.inflate(R.layout.waitting, null);

            TextView animationMessage = (TextView) layout.findViewById(R.id.waitting_text);
            if (TextUtils.isEmpty(message)) {
                animationMessage.setVisibility(View.GONE);
            } else {
                animationMessage.setText(message);
            }
            AVLoadingIndicatorView loading=(AVLoadingIndicatorView)layout.findViewById(R.id.waitting_animation);
            loading.setIndicatorColor(GetAppColor.instance.getAppColor(context));
            return new Dialog(context, layout, false);
        }
    }
}
