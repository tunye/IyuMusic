package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class CommentAgreeRequest extends Request<BaseApiEntity<String>> {
    public static final int AGREE_PROTOCOL = 61001;
    public static final int DISAGREE_PROTOCOL = 61002;
    private int protocol;

    public CommentAgreeRequest(int protocol, int id) {
        String originalUrl = "http://daxue.iyuba.cn/appApi//UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", protocol);
        para.put("id", id);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        this.protocol = protocol;
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> baseApiEntity = new BaseApiEntity<>();
        baseApiEntity.setData(jsonObject.getString("ResultCode"));
        if (baseApiEntity.getData().equals("001")) {
            baseApiEntity.setState(BaseApiEntity.SUCCESS);
            return baseApiEntity;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        if (protocol == AGREE_PROTOCOL) {
            return RuntimeManager.getInstance().getString(R.string.comment_agree_fail);
        } else if (protocol == DISAGREE_PROTOCOL) {
            return RuntimeManager.getInstance().getString(R.string.comment_against_fail);
        } else {
            return RuntimeManager.getInstance().getString(R.string.data_error);
        }
    }
}
