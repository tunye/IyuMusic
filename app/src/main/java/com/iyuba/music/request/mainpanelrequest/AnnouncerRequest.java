package com.iyuba.music.request.mainpanelrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
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
public class AnnouncerRequest {
    public static void exeRequest(final IProtocolResponse<BaseListEntity<ArrayList<Announcer>>> response) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/getStar.jsp";
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    originalUrl, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseListEntity<ArrayList<Announcer>> baseListEntity = new BaseListEntity<>();
                        Type listType = new TypeToken<ArrayList<Announcer>>() {
                        }.getType();
                        baseListEntity.setTotalCount(jsonObject.getInt("total"));
                        ArrayList<Announcer> list = new Gson().fromJson(jsonObject.getString("data"), listType);
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
}
