package com.iyuba.music.request.mainpanelrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.volley.MyJsonRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/9/30.
 */
public class AnnouncerNewsRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    Type listType = new TypeToken<ArrayList<Article>>() {
                    }.getType();
                    try {
                        baseListEntity.setTotalCount(jsonObject.getInt("total"));
                        ArrayList<Article> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                        if (list.size() == 0) {
                            baseListEntity.setIsLastPage(true);
                        } else {
                            baseListEntity.setIsLastPage(false);
                            baseListEntity.setData(list);
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

    public static String generateUrl(int star, int page) {
        String originalUrl = "http://apps.iyuba.com/afterclass/getSongList.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("star", star);
        map.put("pageNum", page);
        map.put("pageCounts", 20);
        return ParameterUrl.setRequestParameter(originalUrl, map);
    }
}
