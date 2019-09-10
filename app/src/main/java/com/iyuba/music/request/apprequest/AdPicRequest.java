package com.iyuba.music.request.apprequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.util.SPUtils;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class AdPicRequest extends Request<AdEntity> {
    public AdPicRequest() {
        String originalUrl = "http://app.iyuba.cn/dev/getAdEntryAll.jsp";
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("appId", ConstantManager.appId);
        paras.put("flag", 1);
        paras.put("uid", SPUtils.loadString(ConfigManager.getInstance().getPreferences(),"userId", "0"));
        url = ParameterUrl.setRequestParameter(originalUrl, paras);
        returnDataType = Request.STRING_DATA;
    }

    @Override
    public AdEntity parseStringImpl(String response) {
        response = response.trim();
        JSONObject jsonObject = JSON.parseArray(response).getJSONObject(0);
        String type = jsonObject.getJSONObject("data").getString("type");
        AdEntity adEntity = new AdEntity();
        switch (type) {
            default:
            case "youdao":
                adEntity.setType("youdao");
                break;
            case "web":
                adEntity = JSON.parseObject(jsonObject.getString("data"), AdEntity.class);
                adEntity.setPicUrl("http://app.iyuba.cn/dev/" + adEntity.getPicUrl());
                adEntity.setType("web");
                break;
        }
        return adEntity;
    }
}
