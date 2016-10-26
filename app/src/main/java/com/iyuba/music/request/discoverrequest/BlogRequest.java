package com.iyuba.music.request.discoverrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
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
public class BlogRequest {
    private static BlogRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";

    public BlogRequest() {
    }

    public static synchronized BlogRequest getInstance() {
        if (instance == null) {
            instance = new BlogRequest();
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
                        BaseApiEntity baseApiEntity = new BaseApiEntity();
                        if ("251".equals(resultCode)) {
                            baseApiEntity.setState(BaseApiEntity.State.SUCCESS);
                            baseApiEntity.setData(jsonObject.getString("subject"));
                            baseApiEntity.setMessage(jsonObject.getString("message"));
                            StringBuilder sb = new StringBuilder();
                            sb.append(jsonObject.getString("username")).append("@@");
                            sb.append(jsonObject.getString("viewnum")).append("@@");
                            sb.append(jsonObject.getString("dateline"));
                            baseApiEntity.setValue(sb.toString());
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

    public String generateUrl(int id) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 20008);
        para.put("blogid", id);
        para.put("sign", MD5.getMD5ofStr("20008" + id + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
