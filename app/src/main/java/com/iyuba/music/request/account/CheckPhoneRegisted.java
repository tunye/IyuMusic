package com.iyuba.music.request.account;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/11/24.
 */
public class CheckPhoneRegisted extends Request<BaseApiEntity<Integer>> {

    public CheckPhoneRegisted(String phone) {
        String originalUrl = "http://api.iyuba.com.cn/sendMessage3.jsp";
        url = ParameterUrl.setRequestParameter(originalUrl, "userphone", phone);
    }

    @Override
    public BaseApiEntity<Integer> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<Integer> baseApiEntity = new BaseApiEntity<>();
        baseApiEntity.setData(jsonObject.getInteger("result"));
        if (baseApiEntity.getData() == 1) {
            baseApiEntity.setState(BaseApiEntity.SUCCESS);
        } else {
            baseApiEntity.setState(BaseApiEntity.FAIL);
        }
        return baseApiEntity;
    }
}
