package com.iyuba.music.request.apprequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/11/21.
 */
public class RecommendSongRequest extends Request<String> {
    public RecommendSongRequest(String uid, String title, String singer) {
        String feedbackUrl = "http://apps.iyuba.cn/afterclass/suggestApi.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("uid", uid);
        map.put("songtitle", ParameterUrl.encode(title));
        map.put("songsinger", ParameterUrl.encode(singer));
        url = ParameterUrl.setRequestParameter(feedbackUrl, map);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        return jsonObject.getString("result");
    }
}

