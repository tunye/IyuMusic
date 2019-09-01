package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
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
public class LrcRequest {
    public static void exeRequest(String url, final IProtocolResponse<BaseListEntity<ArrayList<Original>>> response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity<ArrayList<Original>> baseListEntity = new BaseListEntity<>();
                    Type listType = new TypeToken<ArrayList<Original>>() {
                    }.getType();
                    try {
                        baseListEntity.setTotalCount(jsonObject.getInt("total"));
                        ArrayList<Original> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                        baseListEntity.setData(list);
                        response.response(baseListEntity);
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

    public static String generateUrl(int id, int type) {
        String url;
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("appid", ConstantManager.appId);
        paras.put("uid", AccountManager.getInstance().getUserId());
        switch (type) {
            case 0:
                paras.put("voaid", id);
                String voaOriginalUrl = "http://apps.iyuba.cn/voa/textNewApi.jsp";
                url = ParameterUrl.setRequestParameter(voaOriginalUrl, paras);
                break;
            case 1:
                paras.put("bbcid", id);
                String bbcOriginalUrl = "http://apps.iyuba.cn/minutes/textApi.jsp";
                url = ParameterUrl.setRequestParameter(bbcOriginalUrl, paras);
                break;
            case 2:
                paras.put("SongId", id);
                String musicOriginalUrl = "http://apps.iyuba.cn/afterclass/getLyrics.jsp";
                url = ParameterUrl.setRequestParameter(musicOriginalUrl, paras);
                break;
            default:
                url = "";
                break;
        }
        return url;
    }
}
