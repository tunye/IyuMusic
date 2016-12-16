package com.iyuba.music.request.mainpanelrequest;

/**
 * Created by 10202 on 2016/3/10.
 */

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
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
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/8.
 */
public class FavorSynRequest {
    private static FavorSynRequest instance;
    private final String originalUrl = "http://apps.iyuba.com/afterclass/getCollect.jsp";

    public FavorSynRequest() {
    }

    public static FavorSynRequest getInstance() {
        if (instance == null) {
            instance = new FavorSynRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    Type listType = new TypeToken<ArrayList<Article>>() {
                    }.getType();
                    try {
                        baseListEntity.setTotalCount(jsonObject.getInt("counts"));
                        ArrayList<Article> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                        if (list.get(list.size() - 1) == null) {
                            list.remove(list.size() - 1);
                        }
                        baseListEntity.setData(list);
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

    public String generateUrl(String userid) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("userId", userid);
        para.put("pageNumber", 1);
        para.put("pageCounts", 100);
        para.put("format", "json");
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
