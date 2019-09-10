package com.iyuba.music.request.apprequest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.ad.BannerEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class BannerPicRequest extends Request<BaseListEntity<List<BannerEntity>>> {

    public BannerPicRequest(String type) {
        String originalUrl = "http://app.iyuba.cn/dev/getScrollPicApi.jsp";
        url = ParameterUrl.setRequestParameter(originalUrl, "type", type);
    }

    @Override
    public BaseListEntity<List<BannerEntity>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<BannerEntity>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(2);
        baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), BannerEntity.class));
        return baseListEntity;
    }
}
