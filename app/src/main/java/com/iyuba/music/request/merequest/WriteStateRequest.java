package com.iyuba.music.request.merequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 10202 on 2015/9/30.
 */
public class WriteStateRequest {
    private static WriteStateRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";

    public WriteStateRequest() {
    }

    public static WriteStateRequest getInstance() {
        if (instance == null) {
            instance = new WriteStateRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        response.response(jsonObject.getString("result"));
                    } catch (JSONException e) {
                        response.onServerError(RuntimeManager.getString(R.string.data_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    response.onServerError(VolleyErrorHelper.getMessage(error));
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public String generateUrl(String uid, String uname, String content) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("platform", "android");
        para.put("format", "json");
        para.put("protocol", 30006);
        para.put("uid", uid);
        para.put("username", TextAttr.encode(uname));
        para.put("from", "android");
        para.put("message", TextAttr.encode(TextAttr.encode(content)));
        para.put("sign", MD5.getMD5ofStr("30006" + uid + uname + content + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
