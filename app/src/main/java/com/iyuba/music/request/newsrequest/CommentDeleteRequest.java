package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/9/30.
 */
public class CommentDeleteRequest extends Request<BaseApiEntity<String>> {
    public CommentDeleteRequest(int id) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60004);
        para.put("id", id);
        para.put("code", MD5.getMD5ofStr("60004" + id + "Iyuba"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> result = new BaseApiEntity<>();
        result.setData(jsonObject.getString("result"));
        if (result.getData().equals("1")) {
            result.setState(BaseApiEntity.SUCCESS);
        } else {
            result.setState(BaseApiEntity.FAIL);
        }
        return result;
    }
}
