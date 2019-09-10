package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class NewsListRequest extends Request<BaseListEntity<List<Article>>> {
    public NewsListRequest(int maxid) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/getSongList.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("maxId", maxid);
        map.put("pageNum", 1);
        map.put("pageCounts", 20);
        url = ParameterUrl.setRequestParameter(originalUrl, map);
    }

    @Override
    public BaseListEntity<List<Article>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Article>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getIntValue("total"));
        baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), Article.class));
        return baseListEntity;
    }
}
