package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class DoingCommentRequest extends Request<BaseListEntity<List<DoingComment>>> {
    public DoingCommentRequest(String id, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 30002);
        para.put("doing", id);
        para.put("format", "json");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("30002" + id + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<DoingComment>> parseJsonImpl(JSONObject jsonObject) {
        String resultCode = jsonObject.getString("result");
        if ("311".equals(resultCode)) {
            BaseListEntity<List<DoingComment>> baseListEntity = new BaseListEntity<>();
            if (jsonObject.getInteger("counts") == 0) {
                baseListEntity.setIsLastPage(true);
            } else {
                baseListEntity.setIsLastPage(false);
                List<DoingComment> list = JSON.parseArray(jsonObject.getString("data"), DoingComment.class);
                for (DoingComment doingComment : list) {
                    doingComment.setMessage(ParameterUrl.decode(doingComment.getMessage()));
                }
                baseListEntity.setData(list);
            }
            return baseListEntity;
        } else {
            return null;
        }
    }
}
