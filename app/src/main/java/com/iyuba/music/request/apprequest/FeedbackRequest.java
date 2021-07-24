package com.iyuba.music.request.apprequest;

import android.support.v4.util.ArrayMap;

import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/11/21.
 */
public class FeedbackRequest extends Request<BaseApiEntity<String>> {
    public FeedbackRequest(String uid, String content, String contact) {
        String feedbackUrl = "http://api.iyuba.cn/mobile/android/afterclass/feedback.plain";
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("uid", uid);
        map.put("content", content);
        map.put("email", contact);
        url = ParameterUrl.setRequestParameter(feedbackUrl, map);
        returnDataType = Request.STRING_DATA;
    }

    @Override
    public BaseApiEntity<String> parseStringImpl(String response) {
        String[] content = response.split(",");
        BaseApiEntity<String> result = new BaseApiEntity<>();
        result.setData(content[0]);
        if (result.getData().equals("OK")) {
            result.setState(BaseApiEntity.SUCCESS);
            return result;
        } else {
            return null;
        }
    }

    @Override
    public String getDataErrorMsg() {
        return RuntimeManager.getInstance().getString(R.string.feedback_fail);
    }
}

