package com.iyuba.music.request.mainpanelrequest;

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
public class MusicRequest extends Request<BaseListEntity<List<Article>>> {
    public MusicRequest(int page) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/getSongList.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("simpleflg", 1);
        map.put("pageNum", page);
        map.put("pageCounts", 20);
        url = ParameterUrl.setRequestParameter(originalUrl, map);
    }

    @Override
    public BaseListEntity<List<Article>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Article>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getInteger("total"));
        List<Article> list = JSON.parseArray(jsonObject.getString("data"), Article.class);
        if (list.size() == 0) {
            baseListEntity.setIsLastPage(true);
        } else {
            baseListEntity.setIsLastPage(false);
            baseListEntity.setData(list);
        }
        return baseListEntity;
    }
}
