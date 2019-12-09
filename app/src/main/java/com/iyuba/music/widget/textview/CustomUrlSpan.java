package com.iyuba.music.widget.textview;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.URLSpan;
import android.view.View;

import com.buaa.ct.core.util.GetAppColor;
import com.iyuba.music.activity.WebViewActivity;

public class CustomUrlSpan extends URLSpan {

    private Context context;
    private String url;
    private String title;

    public CustomUrlSpan(Context context, String url) {
        this(context, url, "");
    }

    public CustomUrlSpan(Context context, String url, String title) {
        super(url);
        this.context = context;
        this.url = url;
        this.title = title;
    }

    @Override
    public void onClick(@NonNull View widget) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra("url", url);
        if (!TextUtils.isEmpty(url)) {
            intent.putExtra("title", title);
        }
        widget.getContext().startActivity(intent);
    }

    @Override
    public void updateDrawState(@NonNull TextPaint ds) {
        ds.setColor(GetAppColor.getInstance().getAppColor());
        ds.setUnderlineText(false);
    }
}