package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.iyuba.music.local_music.LocalMusicActivity;

/**
 * Created by 10202 on 2017/1/10.
 */

public class WelcomeAdWebView extends WebViewActivity {
    private static final String NEXT_ACTIVITY = "next_activity";
    private int nextActivity;

    public static void launch(Context context, String url, int nextActivity) {
        Intent intent = new Intent(context, WelcomeAdWebView.class);
        intent.putExtra("url", url);
        intent.putExtra(NEXT_ACTIVITY, nextActivity);
        context.startActivity(intent);
    }

    @Override
    public void beforeSetLayout(Bundle savedInstanceState) {
        super.beforeSetLayout(savedInstanceState);
        nextActivity = getIntent().getIntExtra(NEXT_ACTIVITY, 0);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void finish() {
        switch (nextActivity) {
            case 0:
                startActivity(new Intent(WelcomeAdWebView.this, MainActivity.class));
                break;
            case 1:
                startActivity(new Intent(WelcomeAdWebView.this, LocalMusicActivity.class));
                break;
            case -1:
                break;
        }
        super.finish();
    }
}
