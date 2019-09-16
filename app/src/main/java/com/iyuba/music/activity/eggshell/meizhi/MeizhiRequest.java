package com.iyuba.music.activity.eggshell.meizhi;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.request.Request;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class MeizhiRequest extends Request<BaseListEntity<List<Meizhi>>> {
    private static final String originalUrl = "http://gank.io/api/data/%E7%A6%8F%E5%88%A9/20/";

    public MeizhiRequest(int page) {
        url = originalUrl + page;
    }

    @Override
    public BaseListEntity<List<Meizhi>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Meizhi>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setData(JSON.parseArray(jsonObject.getString("results"), Meizhi.class));
        if (baseListEntity.getData().size() == 0) {
            baseListEntity.setIsLastPage(true);
        } else {
            baseListEntity.setIsLastPage(false);
        }
        return baseListEntity;
    }
}
