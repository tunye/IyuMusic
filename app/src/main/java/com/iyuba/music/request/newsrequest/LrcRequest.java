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
public class LrcRequest extends Request<BaseListEntity<List<Original>>> {
    public LrcRequest(int id, int type) {
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("appid", ConstantManager.appId);
        paras.put("uid", AccountManager.getInstance().getUserId());
        switch (type) {
            case 0:
                paras.put("voaid", id);
                String voaOriginalUrl = "http://apps.iyuba.cn/voa/textNewApi.jsp";
                url = ParameterUrl.setRequestParameter(voaOriginalUrl, paras);
                break;
            case 1:
                paras.put("bbcid", id);
                String bbcOriginalUrl = "http://apps.iyuba.cn/minutes/textApi.jsp";
                url = ParameterUrl.setRequestParameter(bbcOriginalUrl, paras);
                break;
            case 2:
                paras.put("SongId", id);
                String musicOriginalUrl = "http://apps.iyuba.cn/afterclass/getLyrics.jsp";
                url = ParameterUrl.setRequestParameter(musicOriginalUrl, paras);
                break;
            default:
                url = "";
                break;
        }
    }

    @Override
    public BaseListEntity<List<Original>> parseJsonImpl(JSONObject jsonObject) {
        BaseListEntity<List<Original>> baseListEntity = new BaseListEntity<>();
        baseListEntity.setTotalCount(jsonObject.getIntValue("total"));
        baseListEntity.setData(JSON.parseArray(jsonObject.getString("data"), Original.class));
        return baseListEntity;
    }
}
