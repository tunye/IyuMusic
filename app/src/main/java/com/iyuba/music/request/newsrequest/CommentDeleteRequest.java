package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/9/30.
 */
public class CommentDeleteRequest extends Request<String> {
    public CommentDeleteRequest(int id) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60004);
        para.put("id", id);
        para.put("code", MD5.getMD5ofStr("60004" + id + "Iyuba"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        return jsonObject.getString("result");
    }
}
