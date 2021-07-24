package com.iyuba.music.request.apprequest;

import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

/**
 * Created by 10202 on 2015/11/20.
 */
public class UpdateRequest extends Request<BaseApiEntity<String>> {
    public UpdateRequest(int version) {
        String updateUrl = "http://api.iyuba.cn/mobile/android/iyumusic/islatestn.plain";
        url = ParameterUrl.setRequestParameter(updateUrl, "currver", version);
        returnDataType = Request.STRING_DATA;
    }

    @Override
    public BaseApiEntity<String> parseStringImpl(String response) {
        response = response.trim();
        String[] content = response.split(",");
        BaseApiEntity<String> apiEntity = new BaseApiEntity<>();
        if (content[0].equals("NO")) {
            apiEntity.setState(BaseApiEntity.SUCCESS);
            apiEntity.setValue(content[2].replace("||", "@@@"));
        } else {
            apiEntity.setState(BaseApiEntity.FAIL);
        }
        return apiEntity;
    }
}
