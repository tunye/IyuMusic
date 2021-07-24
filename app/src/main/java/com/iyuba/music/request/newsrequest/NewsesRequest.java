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
public class NewsesRequest extends Request<BaseListEntity<List<Article>>> {
    public NewsesRequest(String ids) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/getSongList.jsp";
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("simpleflg", 2);
        paras.put("songIdList", ids);
        url = ParameterUrl.setRequestParameter(originalUrl, paras);
    }

    @Override
    public BaseListEntity<List<Article>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Article>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getIntValue("total"));
        baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), Article.class));
        return baseListEntity;
    }
}
