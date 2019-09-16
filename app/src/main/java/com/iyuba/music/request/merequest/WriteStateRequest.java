package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/9/30.
 */
public class WriteStateRequest extends Request<BaseApiEntity<String>> {
    public WriteStateRequest(String uid, String uname, String content) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("platform", "android");
        para.put("format", "json");
        para.put("protocol", 30006);
        para.put("uid", uid);
        para.put("username", ParameterUrl.encode(uname));
        para.put("from", "android");
        para.put("message", ParameterUrl.encode(ParameterUrl.encode(content)));
        para.put("sign", MD5.getMD5ofStr("30006" + uid + uname + content + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> result = new BaseApiEntity<>();
        result.setData(jsonObject.getString("result"));
        if (result.getData().equals("351")) {
            result.setState(BaseApiEntity.SUCCESS);
        } else {
            result.setState(BaseApiEntity.FAIL);
        }
        return result;
    }
}
