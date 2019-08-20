package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
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
 * Created by 10202 on 2015/10/8.
 */
public class StudyAdRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
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
                            case "addam":
                                adEntity.setType(type);
                                break;
                            case "web":
                                adEntity = new Gson().fromJson(jsonObject.getString("data"), AdEntity.class);
                                if (!TextUtils.isEmpty(adEntity.getPicUrl())) {
                                    adEntity.setPicUrl("http://app.iyuba.cn/dev/" + adEntity.getPicUrl());
                                    String url = adEntity.getLoadUrl();
                                    String userId = AccountManager.getInstance().getUserId();
                                    if (url.contains("?")) {
                                        url += "&uid=" + userId;
                                    } else {
                                        url += "?uid=" + userId;
                                    }
                                    adEntity.setLoadUrl(url);
                                    adEntity.setType(type);
                                } else {
                                    adEntity.setType("youdao");
                                }
                                break;
                            case "youdao":
                            default:
                                adEntity.setType("youdao");
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
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl() {
        String originalUrl = "http://app.iyuba.cn/dev/getAdEntryAll.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("uid", AccountManager.getInstance().getUserId());
        para.put("appId", ConstantManager.appId);
        para.put("flag", 4);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
