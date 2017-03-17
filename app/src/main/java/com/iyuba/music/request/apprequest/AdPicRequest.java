package com.iyuba.music.request.apprequest;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyStringRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 10202 on 2015/9/30.
 */
public class AdPicRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyStringRequest request = new MyStringRequest(StringRequest.Method.GET,
                    url, new Response.Listener<String>() {
                @Override
                public void onResponse(String data) {
                    BaseApiEntity baseApiEntity = new BaseApiEntity();
                    try {
                        data = data.trim();
                        String cutResult = data.trim().substring(1, data.length() - 1);
                        JSONObject jsonObject = new JSONObject(cutResult);
                        baseApiEntity.setState(BaseApiEntity.SUCCESS);
                        AdEntity adEntity = new Gson().fromJson(jsonObject.getString("data"), AdEntity.class);
                        adEntity.setPicUrl("http://app.iyuba.com/dev/" + adEntity.getPicUrl());
                        baseApiEntity.setData(adEntity);
                        response.response(baseApiEntity);
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
            request.setRetryPolicy(new DefaultRetryPolicy(1000, 1, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl() {
        String originalUrl = "http://app.iyuba.com/dev/getAdEntryAll.jsp";
        HashMap<String, Object> paras = new HashMap<>();
        paras.put("appId", ConstantManager.getInstance().getAppId());
        paras.put("flag", 1);
        return ParameterUrl.setRequestParameter(originalUrl, paras);
    }
}
