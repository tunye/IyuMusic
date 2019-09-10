package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.message.MessageLetterContent;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class ChattingRequest extends Request<BaseListEntity<List<MessageLetterContent>>> {
    public ChattingRequest(String uid, String fid, int page) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60004);
        para.put("uid", uid);
        para.put("friendid", fid);
        para.put("asc", 0);
        para.put("pageNumber", page);
        para.put("pageCounts", 20);
        para.put("sign", MD5.getMD5ofStr("60004" + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseListEntity<List<MessageLetterContent>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<MessageLetterContent>> baseListEntity = new BaseListEntity<>();
        String resultCode = jsonObject.getString("result");
        if ("631".equals(resultCode)) {
            String mid = jsonObject.getString("plid");
            baseListEntity.setIsLastPage(false);
            List<MessageLetterContent> list = JSON.parseArray(jsonObject.getString("data"), MessageLetterContent.class);
            for (MessageLetterContent message : list) {
                message.setMessageid(mid);
                message.setContent(ParameterUrl.decode(message.getContent()));
            }
            baseListEntity.setData(list);
            baseListEntity.setTotalCount(list.size());
        } else if ("632".equals(resultCode)) {
            baseListEntity.setIsLastPage(true);
        }
        return baseListEntity;
    }
}
