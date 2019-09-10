package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class CommentAgreeRequest extends Request<String> {
    public CommentAgreeRequest(int protocol, int id) {
        String originalUrl = "http://daxue.iyuba.cn/appApi//UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", protocol);
        para.put("id", id);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        return jsonObject.getString("ResultCode");
    }
}
