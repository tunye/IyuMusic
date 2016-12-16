package com.iyuba.music.request.merequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 10202 on 2015/9/30.
 */
public class SetMessageReadRequest {
    private static SetMessageReadRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";

    public SetMessageReadRequest() {
    }

    public static SetMessageReadRequest getInstance() {
        if (instance == null) {
            instance = new SetMessageReadRequest();
        }
        return instance;
    }

    public void exeRequest(String url) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        //621 ok
                        String result = jsonObject.getString("result");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
        }
    }

    public String generateUrl(String uid, String plid) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 60003);
        para.put("uid", uid);
        para.put("plid", plid);
        para.put("sign", MD5.getMD5ofStr(60003 + uid + plid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
