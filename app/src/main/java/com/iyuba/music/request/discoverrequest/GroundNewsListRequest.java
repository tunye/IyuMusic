package com.iyuba.music.request.discoverrequest;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.request.Request;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class GroundNewsListRequest extends Request<BaseListEntity<List<Article>>> {
    private String app;

    public GroundNewsListRequest(String url, String app) {
        this.url = url;
        this.app = app;
    }

    @Override
    public BaseListEntity<List<Article>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Article>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getInteger("total"));
        ArrayList<Article> list = new ArrayList<>();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        JSONObject jsonObjectData;
        Article article;
        for (int i = 0; i < jsonArray.size(); i++) {
            article = new Article();
            jsonObjectData = jsonArray.getJSONObject(i);
            if (jsonObjectData.containsKey("VoaId")) {
                article.setId(jsonObjectData.getInteger("VoaId"));
            } else if (jsonObjectData.containsKey("BbcId")) {
                article.setId(jsonObjectData.getInteger("BbcId"));
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
        return baseListEntity;
    }
}
