package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2016/3/21.
 */
public class ReadRequest extends Request<BaseListEntity<List<Comment>>> {
    public ReadRequest(int id, int page, String sort) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 600011);
        para.put("platform", "android");
        para.put("format", "json");
        para.put("voaid", id);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("appName", "music");
        para.put("sort", sort);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<Comment>> parseJsonImpl(com.alibaba.fastjson.JSONObject jsonObject) {
        String resultCode = jsonObject.getString("ResultCode");
        BaseListEntity<List<Comment>> baseListEntity = new BaseListEntity<>();
        if ("511".equals(resultCode)) {
            baseListEntity.setTotalCount(jsonObject.getIntValue("Counts"));
            baseListEntity.setCurPage(jsonObject.getIntValue("PageNumber"));
            baseListEntity.setTotalPage(jsonObject.getIntValue("TotalPage"));
            if (baseListEntity.getTotalPage() == baseListEntity.getCurPage()) {
                baseListEntity.setIsLastPage(true);
            } else {
                baseListEntity.setIsLastPage(false);
            }
            baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), Comment.class));
        } else {
            baseListEntity.setTotalCount(0);
        }
        return baseListEntity;
    }
}
