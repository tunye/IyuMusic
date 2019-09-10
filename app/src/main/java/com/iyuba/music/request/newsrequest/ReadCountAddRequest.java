package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;

/**
 * Created by 10202 on 2015/10/8.
 */
public class ReadCountAddRequest extends Request<Boolean> {
    public ReadCountAddRequest(int voaid, String app) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 70001);
        para.put("counts", Utils.getRandomInt(2) + 1);
        para.put("format", "json");
        para.put("appName", app);
        para.put("voaids", voaid);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        returnDataType = Request.STRING_DATA;
    }

    @Override
    public Boolean parseStringImpl(String response) {
        return true;
    }
}
