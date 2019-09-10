package com.iyuba.music.request.discoverrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.doings.Circle;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class CircleRequest extends Request<BaseListEntity<List<Circle>>> {
    public CircleRequest(String uid, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 31001);
        para.put("uid", uid);
        para.put("find", 2);
        para.put("appid", ConstantManager.appId);
        para.put("feeds", "blog,doing,album");
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("31001" + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<Circle>> parseJsonImpl(JSONObject jsonObject) {
        String resultCode = jsonObject.getString("result");
        BaseListEntity<List<Circle>> baseListEntity = new BaseListEntity<>();
        if ("391".equals(resultCode)) {
            baseListEntity.setTotalCount(jsonObject.getInteger("cnt"));
            baseListEntity.setIsLastPage(false);
            baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), Circle.class));
        } else {
            baseListEntity.setIsLastPage(true);
        }
        return baseListEntity;
    }
}
