package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;
import android.util.Base64;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 10202 on 2016/3/12.
 */

public class ScoreOperRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        int result = jsonObject.getInt("result");
                        switch (result) {
                            case 200:
                                apiEntity.setState(BaseApiEntity.SUCCESS);
                                apiEntity.setValue(jsonObject.getString("totalcredit"));
                                apiEntity.setMessage(jsonObject.getString("addcredit"));
                                UserInfo userInfo = AccountManager.getInstance().getUserInfo();
                                if (userInfo != null) {
                                    userInfo.setIcoins(apiEntity.getValue());
                                }
                                break;
                            case 201:
                                apiEntity.setState(BaseApiEntity.FAIL);
                                apiEntity.setMessage(jsonObject.getString("message"));
                                break;
                            case 203:
                                apiEntity.setState(BaseApiEntity.FAIL);
                                apiEntity.setMessage(jsonObject.getString("message"));
                                break;
                            default:
                                apiEntity.setState(BaseApiEntity.ERROR);
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

    public static String generateUrl(String uid, int id, int type) {
        String originalUrl = "http://api.iyuba.com/credits/updateScore.jsp";
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("srid", type);
        paras.put("uid", uid);
        paras.put("appid", 209);
        paras.put("idindex", id);
        paras.put("mobile", 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);//设置日期格式
        if (type == 40) {
            try {
                paras.put("flag", Base64.encodeToString(URLEncoder.encode(df.format(new Date()), "UTF-8").getBytes(), Base64.DEFAULT));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            paras.put("flag", "1234567890" + df.format(new Date()));
        }
        return ParameterUrl.setRequestParameter(originalUrl, paras);
    }
}

