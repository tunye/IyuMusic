package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetterContent;
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
public class ChattingRequest {
    public static void exeRequest(String url, final IProtocolResponse<BaseListEntity<ArrayList<MessageLetterContent>>> response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("result");
                        BaseListEntity<ArrayList<MessageLetterContent>> baseListEntity = new BaseListEntity<>();
                        if ("631".equals(resultCode)) {
                            String mid = jsonObject.getString("plid");
                            Type listType = new TypeToken<ArrayList<MessageLetterContent>>() {
                            }.getType();
                            baseListEntity.setIsLastPage(false);
                            ArrayList<MessageLetterContent> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            for (MessageLetterContent message : list) {
                                message.setMessageid(mid);
                                message.setContent(ParameterUrl.decode(message.getContent()));
                            }
                            baseListEntity.setData(list);
                            baseListEntity.setTotalCount(list.size());
                            response.response(baseListEntity);
                        } else if ("632".equals(resultCode)) {
                            baseListEntity.setIsLastPage(true);
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

    public static String generateUrl(String uid, String fid, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60004);
        para.put("uid", uid);
        para.put("friendid", fid);
        para.put("asc", 0);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("60004" + uid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
