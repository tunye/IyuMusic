package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/9/30.
 */
public class CommentCountRequest extends Request<String> {
    public CommentCountRequest(int id) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/getShuoShuoCount.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("SongId", id);
        para.put("format", "json");
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        return jsonObject.getString("counts");
    }
}
