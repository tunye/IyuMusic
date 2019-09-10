package com.iyuba.music.request.apprequest;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/11/21.
 */
public class FeedbackRequest extends Request<String> {
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
    public String parseStringImpl(String response) {
        String[] content = response.split(",");
        return content[0];
    }
}

