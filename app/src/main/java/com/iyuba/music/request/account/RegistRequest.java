package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.util.Map;

/**
 * Created by 10202 on 2015/11/25.
 */
public class RegistRequest extends Request<BaseApiEntity<Integer>> {
    public RegistRequest(String[] paras) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        Map<String, Object> para = new ArrayMap<>();
        para.put("protocol", 10002);
        para.put("platform", "android");
        para.put("username", paras[0]);
        para.put("password", MD5.getMD5ofStr(paras[1]));
        para.put("email", paras[2]);
        para.put("appid", ConstantManager.appId);
        para.put("format", "json");
        para.put("sign", MD5.getMD5ofStr("10002" + paras[0]
                + MD5.getMD5ofStr(paras[1]) + paras[2] + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<Integer> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<Integer> baseApiEntity = new BaseApiEntity<>();
        baseApiEntity.setData(jsonObject.getInteger("result"));
        baseApiEntity.setMessage(jsonObject.getString("message"));
        if (baseApiEntity.getData() == 111) {
            baseApiEntity.setState(BaseApiEntity.SUCCESS);
        } else if (baseApiEntity.getData() == 112) {
            baseApiEntity.setState(BaseApiEntity.FAIL);
            baseApiEntity.setMessage(RuntimeManager.getInstance().getString(R.string.regist_userid_same));
        } else if (baseApiEntity.getData() == 113) {
            baseApiEntity.setState(BaseApiEntity.FAIL);
            baseApiEntity.setMessage(RuntimeManager.getInstance().getString(R.string.regist_email_same));
        } else {
            baseApiEntity.setState(BaseApiEntity.ERROR);
        }
        return baseApiEntity;
    }
}
