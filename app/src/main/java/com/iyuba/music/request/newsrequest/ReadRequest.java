package com.iyuba.music.request.newsrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.comment.Comment;
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
 * Created by 10202 on 2016/3/21.
 */
public class ReadRequest {
    private static ReadRequest instance;
    private final String originalUrl = "http://daxue.iyuba.com/appApi/UnicomApi";

    public ReadRequest() {
    }

    public static ReadRequest getInstance() {
        if (instance == null) {
            instance = new ReadRequest();
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
                        String resultCode = jsonObject.getString("ResultCode");
                        if ("511".equals(resultCode)) {
                            BaseListEntity baseListEntity = new BaseListEntity();
                            Type listType = new TypeToken<ArrayList<Comment>>() {
                            }.getType();
                            baseListEntity.setTotalCount(jsonObject.getInt("Counts"));
                            baseListEntity.setCurPage(jsonObject.getInt("PageNumber"));
                            baseListEntity.setTotalPage(jsonObject.getInt("TotalPage"));
                            if (baseListEntity.getTotalPage() == baseListEntity.getCurPage()) {
                                baseListEntity.setIsLastPage(true);
                            } else {
                                baseListEntity.setIsLastPage(false);
                            }
                            ArrayList<Comment> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                            baseListEntity.setData(list);
                            response.response(baseListEntity);
                        } else {
                            BaseListEntity baseListEntity = new BaseListEntity();
                            baseListEntity.setTotalCount(0);
                            response.response(baseListEntity);
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

    public String generateUrl(int id, int page, String sort) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 600011);
        para.put("platform", "android");
        para.put("format", "json");
        para.put("voaid", id);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("appName", "music");
        para.put("sort", sort);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
