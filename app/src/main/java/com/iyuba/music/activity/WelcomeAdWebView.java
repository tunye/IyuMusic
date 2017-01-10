package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by 10202 on 2017/1/10.
 */

public class WelcomeAdWebView extends WebViewActivity {
    private int nextActivity;
    private static final String NEXT_ACTIVITY = "next_activity";

    public static void launch(Context context, String url, int nextActivity) {
        Intent intent = new Intent(context, WelcomeAdWebView.class);
        intent.putExtra("url", url);
        intent.putExtra(NEXT_ACTIVITY, nextActivity);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nextActivity = getIntent().getIntExtra(NEXT_ACTIVITY, 0);
    }

    @Override
    public void finish() {
        switch (nextActivity) {
            case 0:
                startActivity(new Intent(WelcomeAdWebView.this, MainActivity.class));
                break;
            case 1:
                break;
        }
        super.finish();
    }
}
