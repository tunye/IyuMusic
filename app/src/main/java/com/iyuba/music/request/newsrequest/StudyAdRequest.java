package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.ad.AdEntity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/10/8.
 */
public class StudyAdRequest extends Request<AdEntity> {
    public StudyAdRequest() {
        String originalUrl = "http://app.iyuba.cn/dev/getAdEntryAll.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("uid", AccountManager.getInstance().getUserId());
        para.put("appId", ConstantManager.appId);
        para.put("flag", 4);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        returnDataType = Request.STRING_DATA;
    }

    @Override
    public AdEntity parseStringImpl(String response) {
        JSONObject jsonObject = JSON.parseArray(response.trim()).getJSONObject(0);
        String type = jsonObject.getJSONObject("data").getString("type");
        AdEntity adEntity = new AdEntity();
        switch (type) {
            case "addam":
                adEntity.setType(type);
                break;
            case "web":
                adEntity = JSON.parseObject(jsonObject.getString("data"), AdEntity.class);
                if (!TextUtils.isEmpty(adEntity.getPicUrl())) {
                    adEntity.setPicUrl("http://app.iyuba.cn/dev/" + adEntity.getPicUrl());
                    String url = adEntity.getLoadUrl();
                    String userId = AccountManager.getInstance().getUserId();
                    if (url.contains("?")) {
                        url += "&uid=" + userId;
                    } else {
                        url += "?uid=" + userId;
                    }
                    adEntity.setLoadUrl(url);
                    adEntity.setType(type);
                } else {
                    adEntity.setType("youdao");
                }
                break;
            case "youdao":
            default:
                adEntity.setType("youdao");
                break;
        }
        return adEntity;
    }
}
