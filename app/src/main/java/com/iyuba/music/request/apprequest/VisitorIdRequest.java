package com.iyuba.music.request.apprequest;


import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class VisitorIdRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        ConfigManager.getInstance().putString("visitorId", jsonObject.getString("uid"));
                        ConfigManager.getInstance().putBoolean("gotVisitor", true);
                        response.response("success");
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

    public static String generateUrl() {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> param = new ArrayMap<>();
        param.put("protocol", 11003);
        param.put("platform", "Android");
        param.put("format", "json");
        param.put("appid", ConstantManager.appId);
        param.put("deviceId", RuntimeManager.newPhoneDiviceId());
        param.put("sign", MD5.getMD5ofStr(RuntimeManager.newPhoneDiviceId() + ConstantManager.appId + "Android" +
                DateFormat.getTimeDate() + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, param);
    }
}
