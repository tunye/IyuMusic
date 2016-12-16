package com.iyuba.music.request.apprequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by 10202 on 2016/3/12.
 */

public class ShareRequest {
    private static ShareRequest instance;
    private final String originalUrl = "http://api.iyuba.com/credits/updateScore.jsp";

    public ShareRequest() {
    }

    public static ShareRequest getInstance() {
        if (instance == null) {
            instance = new ShareRequest();
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
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        int result = jsonObject.getInt("result");
                        switch (result) {
                            case 200:
                                apiEntity.setState(BaseApiEntity.State.SUCCESS);
                                apiEntity.setValue(jsonObject.getString("totalcredit"));
                                apiEntity.setMessage(jsonObject.getString("addcredit"));
                                break;
                            case 201:
                                apiEntity.setState(BaseApiEntity.State.FAIL);
                                apiEntity.setMessage(jsonObject.getString("message"));
                                break;
                            default:
                                apiEntity.setState(BaseApiEntity.State.ERROR);
                                apiEntity.setMessage(RuntimeManager.getString(R.string.unknown_error));
                                break;
                        }
                        response.response(apiEntity);
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

    public String generateUrl(String uid, int id, int type) {
        HashMap<String, Object> paras = new HashMap<>();
        if (type == 2) {
            paras.put("srid", 38);
        } else {
            paras.put("srid", 49);
        }
        paras.put("uid", uid);
        paras.put("appid", 209);
        paras.put("idindex", id);
        paras.put("mobile", 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        paras.put("flag", "1234567890" + df.format(new Date()));
        return ParameterUrl.setRequestParameter(originalUrl, paras);
    }
}

