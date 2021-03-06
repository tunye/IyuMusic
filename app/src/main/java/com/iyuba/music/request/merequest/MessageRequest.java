package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
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
public class MessageRequest {
    public static void exeRequest(String url, final IProtocolResponse<BaseListEntity<ArrayList<MessageLetter>>> response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("result");
                        if ("601".equals(resultCode)) {
                            BaseListEntity<ArrayList<MessageLetter>> baseListEntity = new BaseListEntity<>();
                            Type listType = new TypeToken<ArrayList<MessageLetter>>() {
                            }.getType();
                            baseListEntity.setTotalPage(jsonObject.getInt("lastPage"));
                            if (jsonObject.getInt("nextPage") == jsonObject.getInt("lastPage")) {
                                baseListEntity.setIsLastPage(true);
                            } else {
                                baseListEntity.setIsLastPage(false);
                            }
                            ArrayList<MessageLetter> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            for (MessageLetter letter : list) {
                                letter.setLastmessage(ParameterUrl.decode(letter.getLastmessage()));
                            }
                            baseListEntity.setData(list);
                            baseListEntity.setTotalCount(list.size());
                            response.response(baseListEntity);
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
        para.put("protocol", 60001);
        para.put("uid", uid);
        para.put("format", "json");
        para.put("asc", 0);
        para.put("appid", ConstantManager.appId);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("60001" + uid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
