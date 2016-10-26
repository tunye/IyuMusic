package com.iyuba.music.volley;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.iyuba.music.manager.RuntimeManager;

/**
 * Created by 10202 on 2015/9/30.
 */
public class MyVolley {
    private static MyVolley instance;
    private RequestQueue mRequestQueue;

    public MyVolley() {
        mRequestQueue = getRequestQueue();
    }

    public static synchronized MyVolley getInstance() {
        if (instance == null) {
            instance = new MyVolley();
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(RuntimeManager.getContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}
