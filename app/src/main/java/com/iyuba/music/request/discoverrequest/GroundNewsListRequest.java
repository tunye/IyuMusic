package com.iyuba.music.request.discoverrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/9/30.
 */
public class GroundNewsListRequest {
    public static void exeRequest(String url, final String app, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    try {
                        baseListEntity.setTotalCount(jsonObject.getInt("total"));
                        ArrayList<Article> list = new ArrayList<>();
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        JSONObject jsonObjectData;
                        Article article;
                        for (int i = 0; i < jsonArray.length(); i++) {
                            article = new Article();
                            jsonObjectData = jsonArray.getJSONObject(i);
                            try {
                                article.setId(jsonObjectData.getInt("VoaId"));
                            } catch (JSONException e) {

                            }
                            try {
                                article.setId(jsonObjectData.getInt("BbcId"));
                            } catch (JSONException e) {

                            }
                            article.setTitle_cn(jsonObjectData.getString("Title_cn"));
                            article.setTitle(jsonObjectData.getString("Title"));
                            article.setContent(jsonObjectData.getString("DescCn"));
                            article.setMusicUrl(jsonObjectData.getString("Sound"));
                            article.setPicUrl(jsonObjectData.getString("Pic"));
                            article.setTime(jsonObjectData.getString("CreatTime"));
                            article.setReadCount(jsonObjectData.getString("ReadCount"));
                            article.setApp(app);
                            list.add(article);
                        }
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
}
