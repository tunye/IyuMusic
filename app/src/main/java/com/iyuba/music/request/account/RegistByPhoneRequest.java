package com.iyuba.music.request.account;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
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
 * Created by 10202 on 2015/11/25.
 */
public class RegistByPhoneRequest {
    private static RegistByPhoneRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";


    public RegistByPhoneRequest() {
    }

    public static  RegistByPhoneRequest getInstance() {
        if (instance == null) {
            instance = new RegistByPhoneRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        response.response(jsonObject.getInt("result"));
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
        } else if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            response.onNetError(RuntimeManager.getString(R.string.net_speed_slow));
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public String generateUrl(String[] paras) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 11002);
        para.put("platform", "android");
        para.put("username", paras[0]);
        para.put("password", MD5.getMD5ofStr(paras[1]));
        para.put("mobile", paras[2]);
        para.put("app", ConstantManager.instance.getAppEnglishName());
        para.put("format", "json");
        para.put("sign", MD5.getMD5ofStr("11002" + paras[0]
                + MD5.getMD5ofStr(paras[1]) + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
