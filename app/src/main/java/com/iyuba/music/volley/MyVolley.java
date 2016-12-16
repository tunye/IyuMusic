package com.iyuba.music.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.music.manager.RuntimeManager;

/**
 * Created by 10202 on 2015/9/30.
 */
public class MyVolley {
    private RequestQueue mRequestQueue;

    MyVolley() {
        mRequestQueue = Volley.newRequestQueue(RuntimeManager.getContext());
    }

    public static MyVolley getInstance() {
        return HelperInstance.sInstance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        mRequestQueue.add(req);
    }

    private static class HelperInstance {
        private static MyVolley sInstance = new MyVolley();
    }
}
