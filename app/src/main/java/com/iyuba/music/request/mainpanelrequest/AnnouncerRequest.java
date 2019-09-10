package com.iyuba.music.request.mainpanelrequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.request.Request;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class AnnouncerRequest extends Request<BaseListEntity<List<Announcer>>> {
    public AnnouncerRequest() {
        url = "http://apps.iyuba.cn/afterclass/getStar.jsp";
    }

    @Override
    public BaseListEntity<List<Announcer>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Announcer>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getInteger("total"));
        baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), Announcer.class));
        return baseListEntity;
    }
}
