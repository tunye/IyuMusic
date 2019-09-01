package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.RecommendFriend;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/9/30.
 */
public class RecommendRequest {
    public static void exeRequest(String url, final IProtocolResponse<BaseListEntity<ArrayList<RecommendFriend>>> response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("result");
                        BaseListEntity<ArrayList<RecommendFriend>> baseListEntity = new BaseListEntity<>();
                        if ("591".equals(resultCode)) {
                            Type listType = new TypeToken<ArrayList<RecommendFriend>>() {
                            }.getType();
                            ArrayList<RecommendFriend> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            baseListEntity.setIsLastPage(false);
                            baseListEntity.setData(list);
                            baseListEntity.setState(BaseListEntity.SUCCESS);
                            response.response(baseListEntity);
                        } else if ("592".equals(resultCode)) {
                            baseListEntity.setIsLastPage(true);
                            baseListEntity.setState(BaseListEntity.SUCCESS);
                            response.response(baseListEntity);
                        } else {
                            response.onServerError(RuntimeManager.getInstance().getString(R.string.data_error));
                        }
                    } catch (JSONException e) {
                        response.onServerError(RuntimeManager.getInstance().getString(R.string.data_error));
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
            response.onNetError(RuntimeManager.getInstance().getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String uid, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 52003);
        para.put("uid", uid);
        para.put("format", "json");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr(52003 + uid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
