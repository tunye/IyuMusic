package com.iyuba.music.volley;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.iyuba.music.manager.RuntimeManager;

import java.util.Map;


/**
 * Created by 10202 on 2017/3/7.
 */

public class MyStringRequest extends StringRequest {

    public MyStringRequest(int method, String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(method, url, listener, errorListener);
    }

    public MyStringRequest(String url, Response.Listener<String> listener, Response.ErrorListener errorListener) {
        super(url, listener, errorListener);
    }

    @Override
    public Map<String, String> getHeaders() {
        // self-defined user agent
        Map<String, String> headerMap = new ArrayMap<>();
        headerMap.put("user-app", RuntimeManager.getApplication().getPackageName());
        return headerMap;
    }
}