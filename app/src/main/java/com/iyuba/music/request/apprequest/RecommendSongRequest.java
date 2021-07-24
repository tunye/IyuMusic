package com.iyuba.music.request.apprequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;


/**
 * Created by 10202 on 2015/11/21.
 */
public class RecommendSongRequest extends Request<BaseApiEntity<String>> {
    public RecommendSongRequest(String uid, String title, String singer) {
        String feedbackUrl = "http://apps.iyuba.cn/afterclass/suggestApi.jsp";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("uid", uid);
        map.put("songtitle", ParameterUrl.encode(title));
        map.put("songsinger", ParameterUrl.encode(singer));
        url = ParameterUrl.setRequestParameter(feedbackUrl, map);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> result = new BaseApiEntity<>();
        result.setData(jsonObject.getString("result"));
        if (result.getData().equals("1")) {
            result.setState(BaseApiEntity.SUCCESS);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        return RuntimeManager.getInstance().getString(R.string.study_recommend_fail);
    }
}

