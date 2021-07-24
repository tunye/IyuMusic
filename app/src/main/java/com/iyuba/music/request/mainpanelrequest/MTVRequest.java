package com.iyuba.music.request.mainpanelrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2017/10/30.
 */
public class MTVRequest extends Request<BaseListEntity<List<Article>>> {
    public MTVRequest(int page) {
        String originalUrl = "http://apps.iyuba.cn/iyuba/titleTed.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("type", "Android");
        map.put("parentID", 401);
        map.put("content", "videoTopTitle");
        map.put("maxid", 0);
        map.put("pages", page);
        map.put("pageNum", 20);
        url = ParameterUrl.setRequestParameter(originalUrl, map);
    }

    @Override
    public BaseListEntity<List<Article>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Article>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getInteger("total"));
        if (jsonObject.containsKey("result")) {
            baseListEntity.setIsLastPage(true);
        } else {
            baseListEntity.setIsLastPage(false);
            ArrayList<Article> list = new ArrayList<>();
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            JSONObject jsonObjectData;
            Article article;
            for (int i = 0; i < jsonArray.size(); i++) {
                article = new Article();
                jsonObjectData = jsonArray.getJSONObject(i);
                article.setId(jsonObjectData.getInteger("VoaId"));
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
        return baseListEntity;
    }
}
