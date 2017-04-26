package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.friends.SearchFriend;
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
public class SearchFriendRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseListEntity baseListEntity = new BaseListEntity();
                        String resultCode = jsonObject.getString("result");
                        if ("591".equals(resultCode)) {
                            baseListEntity.setState(BaseListEntity.SUCCESS);
                            Type listType = new TypeToken<ArrayList<SearchFriend>>() {
                            }.getType();
                            ArrayList<SearchFriend> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            baseListEntity.setTotalPage(jsonObject.getInt("lastPage"));
                            if (jsonObject.getInt("nextPage") == jsonObject.getInt("lastPage")) {
                                baseListEntity.setIsLastPage(true);
                            } else {
                                baseListEntity.setIsLastPage(false);
                            }
                            baseListEntity.setData(list);
                            response.response(baseListEntity);
                        } else {
                            baseListEntity.setState(BaseListEntity.FAIL);
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
            request.setRetryPolicy(new DefaultRetryPolicy(
                    8000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String uid, String content, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 52001);
        para.put("uid", uid);
        para.put("search", ParameterUrl.encode(content));
        para.put("format", "json");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr(52001 + uid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
