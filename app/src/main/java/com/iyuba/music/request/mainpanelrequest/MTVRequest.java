package com.iyuba.music.request.mainpanelrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by 10202 on 2017/10/30.
 */
public class MTVRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    try {
                        baseListEntity.setTotalCount(jsonObject.getInt("total"));
                        if (jsonObject.has("result")) {
                            baseListEntity.setIsLastPage(true);
                        } else {
                            baseListEntity.setIsLastPage(false);
                            ArrayList<Article> list = new ArrayList<>();
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            JSONObject jsonObjectData;
                            Article article;
                            for (int i = 0; i < jsonArray.length(); i++) {
                                article = new Article();
                                jsonObjectData = jsonArray.getJSONObject(i);
                                article.setId(jsonObjectData.getInt("VoaId"));
                                article.setTitle_cn(jsonObjectData.getString("Title_cn"));
                                article.setTitle(jsonObjectData.getString("Title"));
                                article.setContent(jsonObjectData.getString("DescCn"));
                                article.setMusicUrl("http://video.iyuba.cn/voa/" + article.getId() + ".mp4");
                                article.setPicUrl(jsonObjectData.getString("Pic"));
                                article.setTime(jsonObjectData.getString("CreatTime"));
                                article.setReadCount(jsonObjectData.getString("ReadCount"));
                                article.setCategory(jsonObjectData.getString("Category"));
                                article.setApp(ConstantManager.appId);
                                list.add(article);
                            }
                            baseListEntity.setData(list);
                        }
                        response.response(baseListEntity);
                    } catch (JSONException e) {
                        e.printStackTrace();
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

    public static String generateUrl(int page) {
        String originalUrl = "http://apps.iyuba.cn/iyuba/titleTed.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("type", "Android");
        map.put("parentID", 401);
        map.put("content", "videoTopTitle");
        map.put("maxid", 0);
        map.put("pages", page);
        map.put("pageNum", 20);
        return ParameterUrl.setRequestParameter(originalUrl, map);
    }
}
