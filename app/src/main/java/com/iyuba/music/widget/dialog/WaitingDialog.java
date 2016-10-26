package com.iyuba.music.widget.dialog;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.iyuba.music.R;

/**
 * Created by 10202 on 2015/10/26.
 */
public class WaitingDialog {

    public static class Builder {

        private Context context;
        private TextView animationMessage;
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

            animationMessage = (TextView) layout.findViewById(R.id.waitting_text);
            if (TextUtils.isEmpty(message)) {
                animationMessage.setVisibility(View.GONE);
            } else {
                animationMessage.setText(message);
            }
            return new Dialog(context, layout, false);
        }
    }
}
