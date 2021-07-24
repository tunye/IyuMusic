package com.iyuba.music.request.apprequest;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class QunRequest extends Request<BaseApiEntity<String>> {

    public QunRequest(String type) {
        String originalUrl = "http://m.iyuba.cn/m_login/getQQGroup.jsp";
        url = ParameterUrl.setRequestParameter(originalUrl, "type", type);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> baseApiEntity = new BaseApiEntity<>();
        baseApiEntity.setData(jsonObject.getString("QQ"));
        baseApiEntity.setValue(jsonObject.getString("key"));
        baseApiEntity.setState(BaseApiEntity.SUCCESS);
        return baseApiEntity;
    }
}
