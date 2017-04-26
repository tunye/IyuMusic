package com.iyuba.music.volley;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

/**
 * Created by 10202 on 2017/3/7.
 */

public class MyJsonRequest extends JsonObjectRequest {
    public MyJsonRequest(String url, JSONObject jsonRequest, Response.Listener<JSONObject> listener, Response.ErrorListener errorListener) {
        super(url, jsonRequest, listener, errorListener);
    }


    //    @Override
//    public Map<String, String> getHeaders() throws AuthFailureError {
//        // self-defined user agent
//        Map<String, String> headerMap = new HashMap<>();
//        headerMap.put("user-agent", RuntimeManager.getApplication().getPackageName());
//        return headerMap;
//    }
    @Override
    public void cancel() {
        super.cancel();
        onFinish();
    }
}
