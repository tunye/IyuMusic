package com.iyuba.music.request.apprequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyStringRequest;
import com.iyuba.music.volley.MyVolley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class AdPicRequest {
    public static void exeRequest(String url, final IProtocolResponse<AdEntity> response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyStringRequest request = new MyStringRequest(StringRequest.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {
                    try {
                        data = data.trim();
                        JSONObject jsonObject = new JSONArray(data).getJSONObject(0);
                        String type = jsonObject.getJSONObject("data").getString("type");
                        AdEntity adEntity = new AdEntity();
                        switch (type) {
                            default:
                            case "youdao":
                                adEntity.setType("youdao");
                                break;
                            case "web":
                                adEntity = new Gson().fromJson(jsonObject.getString("data"), AdEntity.class);
                                adEntity.setPicUrl("http://app.iyuba.cn/dev/" + adEntity.getPicUrl());
                                adEntity.setType("web");
                                break;
                        }
                        response.response(adEntity);
                    } catch (JSONException e) {
                        AdEntity adEntity = new AdEntity();
                        adEntity.setType("youdao");
                        response.response(adEntity);
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AdEntity adEntity = new AdEntity();
                    adEntity.setType("youdao");
                    response.response(adEntity);
                }
            });
            request.setRetryPolicy(new DefaultRetryPolicy(1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getInstance().getString(R.string.no_internet));
        }
    }

    public static String generateUrl() {
        String originalUrl = "http://app.iyuba.cn/dev/getAdEntryAll.jsp";
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("appId", ConstantManager.appId);
        paras.put("flag", 1);
        paras.put("uid", ConfigManager.getInstance().loadString("userId", "0"));
        return ParameterUrl.setRequestParameter(originalUrl, paras);
    }
}
