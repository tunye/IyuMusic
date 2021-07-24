package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.user.MostDetailInfo;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class UserInfoDetailRequest extends Request<MostDetailInfo> {
    public UserInfoDetailRequest(String uid) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 20002);
        para.put("id", uid);
        para.put("platform", "android");
        para.put("format", "json");
        para.put("sign", MD5.getMD5ofStr("20002" + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public MostDetailInfo parseJsonImpl(JSONObject jsonObject) {
        String resultCode = jsonObject.getString("result");
        if ("211".equals(resultCode)) {
            return JSON.parseObject(jsonObject.toString(), MostDetailInfo.class);
        }
        return null;
    }
}
