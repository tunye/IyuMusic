package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2016/2/16.
 */
public class CommentExpressRequest extends Request<BaseApiEntity<String>> {
    public CommentExpressRequest(String... paras) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 60002);
        para.put("voaid", paras[0]);
        para.put("platform", "android");
        para.put("shuoshuotype", 0);
        para.put("appName", "music");
        para.put("userid", paras[1]);
        para.put("format", "json");
        para.put("username", ParameterUrl.encode(ParameterUrl.encode(paras[2])));
        para.put("content", ParameterUrl.encode(ParameterUrl.encode(paras[3])));
        para.put("imgsrc", ParameterUrl.encode("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=" + paras[1] + "&size=big"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> result = new BaseApiEntity<>();
        result.setData(jsonObject.getString("ResultCode"));
        if (result.getData().equals("501")) {
            result.setState(BaseApiEntity.SUCCESS);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        return RuntimeManager.getInstance().getString(R.string.comment_send_fail);
    }
}
