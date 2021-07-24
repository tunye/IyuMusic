package com.iyuba.music.request.mainpanelrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.mainpanel.SongCategory;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class SongCategoryRequest extends Request<BaseListEntity<List<SongCategory>>> {
    public SongCategoryRequest(int curPage) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/getStyles.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("simpleflg", 1);
        map.put("pageNum", curPage);
        map.put("pageCounts", 20);
        map.put("format", "json");
        url = ParameterUrl.setRequestParameter(originalUrl, map);
    }

    @Override
    public BaseListEntity<List<SongCategory>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<SongCategory>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getInteger("total"));
        List<SongCategory> list = JSON.parseArray(jsonObject.getString("data"), SongCategory.class);
        if (list.size() == 0) {
            baseListEntity.setIsLastPage(true);
        } else {
            baseListEntity.setIsLastPage(false);
            baseListEntity.setData(list);
        }
        return baseListEntity;
    }
}
