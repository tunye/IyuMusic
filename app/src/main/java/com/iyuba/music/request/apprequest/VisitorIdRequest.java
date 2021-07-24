package com.iyuba.music.request.apprequest;


import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.util.SPUtils;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;


/**
 * Created by 10202 on 2015/9/30.
 */
public class VisitorIdRequest extends Request<String> {
    public VisitorIdRequest() {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> param = new ArrayMap<>();
        param.put("protocol", 11003);
        param.put("platform", "Android");
        param.put("format", "json");
        param.put("appid", ConstantManager.appId);
        param.put("deviceId", Utils.newPhoneDiviceId());
        param.put("sign", MD5.getMD5ofStr(Utils.newPhoneDiviceId() + ConstantManager.appId + "Android" +
                DateFormat.getTimeDate() + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, param);
    }

    @Override
    public String parseJsonImpl(JSONObject jsonObject) {
        if (TextUtils.isEmpty(jsonObject.getString("uid"))) {
            return null;
        } else {
            SPUtils.putString(ConfigManager.getInstance().getPreferences(), "visitorId", jsonObject.getString("uid"));
            SPUtils.putBoolean(ConfigManager.getInstance().getPreferences(), "gotVisitor", true);
            AccountManager.getInstance().setVisitorId(jsonObject.getString("uid"));
        }
        return "success";
    }
}
