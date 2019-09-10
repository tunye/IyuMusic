package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2016/2/16.
 */
public class CommentExpressRequest extends Request<String> {
    public CommentExpressRequest(String... paras) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60002);
        para.put("voaid", paras[0]);
        para.put("platform", "android");
        para.put("shuoshuotype", 0);
        para.put("appName", "music");
        para.put("userid", paras[1]);
        para.put("format", "json");
        para.put("username", ParameterUrl.encode(ParameterUrl.encode(paras[2])));
        para.put("content", ParameterUrl.encode(ParameterUrl.encode(paras[3])));
        para.put("imgsrc", ParameterUrl.encode("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=" + paras[1] + "&size=big"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        return jsonObject.getString("ResultCode");
    }
}
