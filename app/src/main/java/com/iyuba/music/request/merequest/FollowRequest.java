package com.iyuba.music.request.merequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.Follows;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 10202 on 2015/9/30.
 */
public class FollowRequest {
    private static FollowRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";

    public FollowRequest() {
    }

    public static synchronized FollowRequest getInstance() {
        if (instance == null) {
            instance = new FollowRequest();
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
                        String resultCode = jsonObject.getString("result");
                        if ("550".equals(resultCode)) {
                            BaseListEntity baseListEntity = new BaseListEntity();
                            Type listType = new TypeToken<ArrayList<Follows>>() {
                            }.getType();
                            ArrayList<Follows> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            baseListEntity.setTotalCount(jsonObject.getInt("num"));
                            baseListEntity.setTotalPage(baseListEntity.getTotalCount() / 20 + (baseListEntity.getTotalCount() % 20 == 0 ? 0 : 1));

                            if (list.size() == 0) {
                                baseListEntity.setIsLastPage(true);
                            } else {
                                baseListEntity.setIsLastPage(false);
                                for (Follows fans : list) {
                                    fans.setDoing(ParameterUrl.decode(fans.getDoing()));
                                }
                                baseListEntity.setData(list);
                            }
                            response.response(baseListEntity);
                        }
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

    public String generateUrl(String uid, int page) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 51001);
        para.put("uid", uid);
        para.put("format", "json");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr(51001 + uid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
