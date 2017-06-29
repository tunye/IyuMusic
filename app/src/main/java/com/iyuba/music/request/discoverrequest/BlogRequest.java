package com.iyuba.music.request.discoverrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
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

/**
 * Created by 10202 on 2015/9/30.
 */
public class BlogRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        String resultCode = jsonObject.getString("result");
                        BaseApiEntity baseApiEntity = new BaseApiEntity();
                        if ("251".equals(resultCode)) {
                            baseApiEntity.setState(BaseApiEntity.SUCCESS);
                            baseApiEntity.setData(jsonObject.getString("subject"));
                            baseApiEntity.setMessage(jsonObject.getString("message"));
                            String sb = jsonObject.getString("username") + "@@" +
                                    jsonObject.getString("viewnum") + "@@" +
                                    jsonObject.getString("dateline");
                            baseApiEntity.setValue(sb);
                            response.response(baseApiEntity);
                        } else {
                            response.onServerError(RuntimeManager.getString(R.string.data_error));
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

    public static String generateUrl(int id) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 20008);
        para.put("blogid", id);
        para.put("sign", MD5.getMD5ofStr("20008" + id + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
