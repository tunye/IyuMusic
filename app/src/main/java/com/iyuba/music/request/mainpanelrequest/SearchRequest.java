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
public class SearchRequest extends Request<BaseListEntity<List<Article>>> {
    public SearchRequest(String key, int curPage) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/searchApi.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("key", ParameterUrl.encode(ParameterUrl.encode(key)));
        map.put("pageNum", curPage);
        map.put("pageCounts", 20);
        map.put("format", "json");
        map.put("fields", "all");
        url = ParameterUrl.setRequestParameter(originalUrl, map);
    }

    @Override
    public BaseListEntity<List<Article>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Article>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getInteger("total"));
        if (baseListEntity.getTotalCount() == 0) {
            baseListEntity.setState(BaseListEntity.FAIL);
        } else {
            baseListEntity.setState(BaseListEntity.SUCCESS);
            if (jsonObject.containsKey("result") && jsonObject.getInteger("result") == 0) {
                baseListEntity.setIsLastPage(true);
            } else {
                baseListEntity.setTotalPage(baseListEntity.getTotalCount() / 20 + (baseListEntity.getTotalCount() % 20 == 0 ? 0 : 1));
                List<Article> list = JSON.parseArray(jsonObject.getString("data"), Article.class);
                if (list.size() == 0) {
                    baseListEntity.setIsLastPage(true);
                } else {
                    baseListEntity.setIsLastPage(false);
                    baseListEntity.setData(list);
                }
            }
        }
        return baseListEntity;
    }
}
