package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetter;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class MessageRequest extends Request<BaseListEntity<List<MessageLetter>>> {
    public MessageRequest(String uid, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60001);
        para.put("uid", uid);
        para.put("format", "json");
        para.put("asc", 0);
        para.put("appid", ConstantManager.appId);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("60001" + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<MessageLetter>> parseJsonImpl(JSONObject jsonObject) {
        String resultCode = jsonObject.getString("result");
        if ("601".equals(resultCode)) {
            BaseListEntity<List<MessageLetter>> baseListEntity = new BaseListEntity<>();
            baseListEntity.setTotalPage(jsonObject.getInteger("lastPage"));
            if (jsonObject.getInteger("nextPage") == jsonObject.getInteger("lastPage")) {
                baseListEntity.setIsLastPage(true);
            } else {
                baseListEntity.setIsLastPage(false);
            }
            List<MessageLetter> list = JSON.parseArray(jsonObject.getString("data"), MessageLetter.class);
            for (MessageLetter letter : list) {
                letter.setLastmessage(ParameterUrl.decode(letter.getLastmessage()));
            }
            baseListEntity.setData(list);
            baseListEntity.setTotalCount(list.size());
            return baseListEntity;
        }
        return null;
    }
}
