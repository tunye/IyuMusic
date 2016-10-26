package com.iyuba.music.request.merequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by 10202 on 2015/9/30.
 */
public class CancelAttentionRequest {
    private static CancelAttentionRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";

    public CancelAttentionRequest() {
    }

    public static synchronized CancelAttentionRequest getInstance() {
        if (instance == null) {
            instance = new CancelAttentionRequest();
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
                        //510 ok
                        response.response(jsonObject.getString("result"));
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public String generateUrl(String uid, String followid) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 50002);
        para.put("uid", uid);
        para.put("followid", followid);
        para.put("sign", MD5.getMD5ofStr(50002 + uid + followid + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
