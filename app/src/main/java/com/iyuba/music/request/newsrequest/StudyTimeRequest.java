package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.util.Calendar;

/**
 * Created by 10202 on 2015/10/8.
 */
public class StudyTimeRequest extends Request<BaseApiEntity<Integer>> {
    public StudyTimeRequest() {
        String originalUrl = "http://daxue.iyuba.cn/ecollege/getMyTime.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        Calendar init = Calendar.getInstance();
        init.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance();
        long intervalMilli = now.getTimeInMillis() - init.getTimeInMillis();
        int days = (int) (intervalMilli / (24 * 60 * 60 * 1000));
        para.put("uid", AccountManager.getInstance().getUserId());
        para.put("day", days);
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<Integer> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<Integer> apiEntity = new BaseApiEntity<>();
        if (jsonObject.getIntValue("result") == 1) {
            apiEntity.setState(BaseApiEntity.SUCCESS);
            apiEntity.setData(jsonObject.getIntValue("totalTime"));
        } else {
            apiEntity.setState(BaseApiEntity.FAIL);
        }
        return apiEntity;
    }
}
