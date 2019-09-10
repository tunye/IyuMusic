package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/9/30.
 */
public class GradeRequest extends Request<String> {
    public GradeRequest(String uid) {
        String originalUrl = "http://daxue.iyuba.cn/ecollege/getPaiming.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("format", "json");
        para.put("uid", uid);
        para.put("appName", ConstantManager.appEnglishName);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        return jsonObject.getString("positionByTime") + "@@@" + jsonObject.getString("totalTime");
    }
}
