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
public class CancelAttentionRequest extends Request<BaseApiEntity<String>> {
    public CancelAttentionRequest(String uid, String followid) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 50002);
        para.put("uid", uid);
        para.put("followid", followid);
        para.put("sign", MD5.getMD5ofStr(50002 + uid + followid + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> baseApiEntity = new BaseApiEntity<>();
        baseApiEntity.setData(jsonObject.getString("result"));
        if (baseApiEntity.getData().equals("510")) {
            baseApiEntity.setState(BaseApiEntity.SUCCESS);
            return baseApiEntity;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        return RuntimeManager.getInstance().getString(R.string.person_attention_cancel_fail);
    }
}
