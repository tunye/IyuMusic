package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/9/30.
 */
public class SendDoingCommentRequest extends Request<BaseApiEntity<String>> {
    public SendDoingCommentRequest(DoingComment doingComment, Doing doing, String fromUid, String fromMessage) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 30007);
        para.put("uid", doingComment.getUid());
        para.put("username", ParameterUrl.encode(doingComment.getUsername()));
        para.put("doid", doing.getDoid());
        para.put("upid", doingComment.getUpid());
        para.put("message", ParameterUrl.encode(ParameterUrl.encode(doingComment.getMessage())));
        para.put("grade", doingComment.getGrade());
        para.put("fromUid", fromUid);// 要回复的人的id
        para.put("fromMsg", ParameterUrl.encode(ParameterUrl.encode(fromMessage)));// 要回复的人的内容
        para.put("orignUid", doing.getUid());// 该doing 作者的id
        para.put("orignMsg", ParameterUrl.encode(ParameterUrl.encode(doing.getMessage())));// 该doing 的内容
        para.put("sign", MD5.getMD5ofStr(30007 + doingComment.getUid()
                + doingComment.getUsername() + doingComment.getMessage() + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> baseApiEntity = new BaseApiEntity<>();
        baseApiEntity.setData(jsonObject.getString("result"));
        if (baseApiEntity.getData().equals("361")) {
            baseApiEntity.setState(BaseApiEntity.SUCCESS);
            return baseApiEntity;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        return RuntimeManager.getInstance().getString(R.string.message_send_fail);
    }
}
