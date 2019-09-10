package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/10/8.
 */
public class FavorRequest extends Request<String> {
    public FavorRequest(String userid, int voaid, String type) {
        String originalUrl = "http://apps.iyuba.cn/afterclass/updateCollect.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("userId", userid);
        para.put("voaId", voaid);
        para.put("type", type);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        returnDataType = Request.STRING_DATA;
    }

    @Override
    public String parseStringImpl(String response) {
        if (response.contains("del")) {
            return "del";
        } else if (response.contains("insert")) {
            return "insert";
        } else {
            return null;
        }
    }
}
