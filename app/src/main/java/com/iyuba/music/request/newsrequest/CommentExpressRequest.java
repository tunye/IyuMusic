package com.iyuba.music.request.newsrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 10202 on 2016/2/16.
 */
public class CommentExpressRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        response.response(jsonObject.getString("ResultCode"));
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

    public static String generateUrl(String... paras) {
        String originalUrl = "http://daxue.iyuba.com/appApi/UnicomApi";
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 60002);
        para.put("voaid", paras[0]);
        para.put("platform", "android");
        para.put("shuoshuotype", 0);
        para.put("appName", "music");
        para.put("userid", paras[1]);
        para.put("format", "json");
        para.put("username", TextAttr.encode(TextAttr.encode(paras[2])));
        para.put("content", TextAttr.encode(TextAttr.encode(paras[3])));
        para.put("imgsrc", TextAttr.encode("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=" + paras[1] + "&size=big"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
