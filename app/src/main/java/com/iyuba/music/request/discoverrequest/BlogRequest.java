package com.iyuba.music.request.discoverrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class BlogRequest extends Request<BaseApiEntity<String>> {
    public BlogRequest(int id) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 20008);
        para.put("blogid", id);
        para.put("sign", MD5.getMD5ofStr("20008" + id + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        String resultCode = jsonObject.getString("result");
        BaseApiEntity<String> baseApiEntity = new BaseApiEntity();
        if ("251".equals(resultCode)) {
            baseApiEntity.setState(BaseApiEntity.SUCCESS);
            baseApiEntity.setData(jsonObject.getString("subject"));
            baseApiEntity.setMessage(jsonObject.getString("message"));
            String sb = jsonObject.getString("username") + "@@" +
                    jsonObject.getString("viewnum") + "@@" +
                    jsonObject.getString("dateline");
            baseApiEntity.setValue(sb);
            return baseApiEntity;
        } else {
            return null;
        }
    }
}
