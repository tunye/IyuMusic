package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.comment.Comment;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class CommentRequest extends Request<BaseListEntity<List<Comment>>> {
    public CommentRequest(int id, int page) {
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60001);
        para.put("platform", "android");
        para.put("format", "json");
        para.put("voaid", id);
        para.put("appid", ConstantManager.appId);
        para.put("shuoshuotype", 0);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("appName", "music");
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<Comment>> parseJsonImpl(JSONObject jsonObject) {
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
            List<Comment> list = JSON.parseArray(jsonObject.getString("data"), Comment.class);
            for (Comment comment : list) {
                comment.setShuoshuo(ParameterUrl.decode(comment.getShuoshuo()));
            }
            baseListEntity.setData(list);
        } else {
            baseListEntity.setTotalCount(0);
        }
        return baseListEntity;
    }
}
