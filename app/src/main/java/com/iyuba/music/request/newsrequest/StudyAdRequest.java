package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyStringRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

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
                    BaseApiEntity baseApiEntity = new BaseApiEntity();
                    try {
                        data = data.trim();
                        String cutResult = data.trim().substring(1, data.length() - 1);
                        JSONObject jsonObject = new JSONObject(cutResult);
                        if (jsonObject.getString("result").equals("1")) {
                            baseApiEntity.setState(BaseApiEntity.SUCCESS);
                            AdEntity adEntity = new Gson().fromJson(jsonObject.getString("data"), AdEntity.class);
                            if (!TextUtils.isEmpty(adEntity.getPicUrl())) {
                                adEntity.setPicUrl("http://app.iyuba.com/dev/" + adEntity.getPicUrl());
                                String url = adEntity.getLoadUrl();
                                String userId = AccountManager.getInstance().checkUserLogin() ? AccountManager.getInstance().getUserId() : "0";
                                if (url.contains("?")) {
                                    url += "&uid=" + AccountManager.getInstance().getUserId();
                                } else {
                                    url += "?uid=" + AccountManager.getInstance().getUserId();
                                }
                                adEntity.setLoadUrl(url);
                                baseApiEntity.setData(adEntity);
                            } else {
                                baseApiEntity.setState(BaseApiEntity.FAIL);
                            }
                        } else {
                            baseApiEntity.setState(BaseApiEntity.FAIL);
                        }
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
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl() {
        String originalUrl = "http://app.iyuba.com/dev/getAdEntryAll.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("uid", AccountManager.getInstance().checkUserLogin() ? AccountManager.getInstance().getUserId() : 0);
        para.put("appId", ConstantManager.appId);
        para.put("flag", 4);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
