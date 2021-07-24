package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class DoingRequest extends Request<BaseListEntity<List<Doing>>> {
    public DoingRequest(String uid, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 30001);
        para.put("userId", uid);
        para.put("format", "json");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("30001" + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<Doing>> parseJsonImpl(com.alibaba.fastjson.JSONObject jsonObject) {
        String resultCode = jsonObject.getString("result");
        if ("301".equals(resultCode)) {
            BaseListEntity<List<Doing>> baseListEntity = new BaseListEntity<>();
            if (jsonObject.getInteger("counts") == 0) {
                baseListEntity.setIsLastPage(true);
            } else {
                baseListEntity.setIsLastPage(false);
                String uid = jsonObject.getString("uid");
                String username = jsonObject.getString("username");
                List<Doing> list = JSON.parseArray(jsonObject.getString("data"), Doing.class);
                for (Doing temp : list) {
                    temp.setUid(uid);
                    temp.setMessage(ParameterUrl.decode(temp.getMessage()));
                    temp.setUsername(username);
                }
                baseListEntity.setData(list);
            }
            return baseListEntity;
        }
        return null;
    }
}
