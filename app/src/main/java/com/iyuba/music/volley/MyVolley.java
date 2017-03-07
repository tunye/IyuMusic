package com.iyuba.music.volley;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.iyuba.music.manager.RuntimeManager;

/**
 * Created by 10202 on 2015/9/30.
 */
public class MyVolley {
    private RequestQueue mRequestQueue;

    private MyVolley() {
        mRequestQueue = Volley.newRequestQueue(RuntimeManager.getContext());
    }

    public static MyVolley getInstance() {
        return HelperInstance.sInstance;
    }

    public <T> void addToRequestQueue(Request<T> req)  {
        req.setRetryPolicy(new DefaultRetryPolicy(8000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        try {
            Log.e("aaa", new Gson().toJson(req.getHeaders()));
        } catch (AuthFailureError authFailureError) {
            authFailureError.printStackTrace();
        }
        mRequestQueue.add(req);
    }

    private static class HelperInstance {
        private static MyVolley sInstance = new MyVolley();
    }
}
