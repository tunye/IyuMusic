package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/9/30.
 */
public class SendMessageRequest extends Request<String> {
    public SendMessageRequest(String uid, String uname, String content) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60002);
        para.put("uid", uid);
        para.put("username", ParameterUrl.encode(uname));
        para.put("context", ParameterUrl.encode(ParameterUrl.encode(content)));
        para.put("sign", MD5.getMD5ofStr(60002 + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        return jsonObject.getString("result");
    }
}
