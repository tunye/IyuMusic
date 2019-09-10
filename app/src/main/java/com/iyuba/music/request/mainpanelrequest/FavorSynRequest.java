package com.iyuba.music.request.mainpanelrequest;

/**
 * Created by 10202 on 2016/3/10.
 */

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/10/8.
 */
public class FavorSynRequest extends Request<BaseListEntity<List<Article>>> {
    public FavorSynRequest(String userid) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/getCollect.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("userId", userid);
        para.put("pageNumber", 1);
        para.put("pageCounts", 100);
        para.put("format", "json");
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<Article>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Article>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getInteger("counts"));
        List<Article> list = JSON.parseArray(jsonObject.getString("data"), Article.class);
        if (list.size() > 0 && list.get(list.size() - 1) == null) {
            list.remove(list.size() - 1);
        }
        baseListEntity.setData(list);
        return baseListEntity;
    }
}
