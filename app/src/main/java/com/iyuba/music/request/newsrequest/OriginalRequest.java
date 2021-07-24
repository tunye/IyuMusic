package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.List;

/**
 * Created by 10202 on 2015/9/30.
 */
public class OriginalRequest extends Request<BaseListEntity<List<Original>>> {
    public OriginalRequest(int id) {
        String musicOriginalUrl = "http://apps.iyuba.cn/afterclass/getText.jsp";
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("SongId", id);
        paras.put("appid", ConstantManager.appId);
        paras.put("uid", AccountManager.getInstance().getUserId());
        url = ParameterUrl.setRequestParameter(musicOriginalUrl, paras);
    }

    @Override
    public BaseListEntity<List<Original>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Original>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getIntValue("total"));
        baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), Original.class));
        return baseListEntity;
    }
}
