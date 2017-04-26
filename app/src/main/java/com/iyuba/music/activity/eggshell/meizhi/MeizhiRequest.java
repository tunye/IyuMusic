package com.iyuba.music.activity.eggshell.meizhi;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
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
public class MeizhiRequest {
    private static final String originalUrl = "https://gank.io/api/data/%E7%A6%8F%E5%88%A9/20/";
    private static MeizhiRequest instance;

    public MeizhiRequest() {
    }

    public static MeizhiRequest getInstance() {
        if (instance == null) {
            instance = new MeizhiRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    Type listType = new TypeToken<ArrayList<Meizhi>>() {
                    }.getType();
                    try {
                        ArrayList<Meizhi> list = new Gson().fromJson(jsonObject.getString("results"), listType);
                        baseListEntity.setData(list);
                        if (list.size() == 0) {
                            baseListEntity.setIsLastPage(true);
                        } else {
                            baseListEntity.setIsLastPage(false);
                        }
                        response.response(baseListEntity);
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

    public String generateUrl(int page) {
        return originalUrl + page;
    }
}
