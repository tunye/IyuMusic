package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class SetMessageReadRequest extends Request<String> {
    public SetMessageReadRequest(String uid, String plid) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60003);
        para.put("uid", uid);
        para.put("plid", plid);
        para.put("sign", MD5.getMD5ofStr(60003 + uid + plid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        //621 ok
        return jsonObject.getString("result");
    }
}
