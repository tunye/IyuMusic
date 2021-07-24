package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class EditUserInfoRequest extends Request<BaseApiEntity<String>> {
    public EditUserInfoRequest(String uid, String key, String value) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 20003);
        para.put("id", uid);
        para.put("key", key);
        para.put("value", ParameterUrl.encode(ParameterUrl.encode(value)));
        para.put("sign", MD5.getMD5ofStr("20003" + uid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> result = new BaseApiEntity<>();
        result.setData(jsonObject.getString("result"));
        if (result.getData().equals("221")) {
            result.setState(BaseApiEntity.SUCCESS);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        return RuntimeManager.getInstance().getString(R.string.person_detail_fail);
    }
}
