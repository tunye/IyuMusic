package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10202 on 2016/2/16.
 */
public class CommentExpressRequest {
    public static void exeRequest(String url, final IProtocolResponse<String> response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        response.response(jsonObject.getString("ResultCode"));
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

    public static String generateUrl(String... paras) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60002);
        para.put("voaid", paras[0]);
        para.put("platform", "android");
        para.put("shuoshuotype", 0);
        para.put("appName", "music");
        para.put("userid", paras[1]);
        para.put("format", "json");
        para.put("username", ParameterUrl.encode(ParameterUrl.encode(paras[2])));
        para.put("content", ParameterUrl.encode(ParameterUrl.encode(paras[3])));
        para.put("imgsrc", ParameterUrl.encode("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=" + paras[1] + "&size=big"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
