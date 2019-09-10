package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;
import android.util.Base64;

import com.alibaba.fastjson.JSONObject;
import com.buaa.ct.core.manager.RuntimeManager;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by 10202 on 2016/3/12.
 */

public class ScoreOperRequest extends Request<BaseApiEntity<String>> {
    public ScoreOperRequest(String uid, int id, int type) {
        String originalUrl = "http://api.iyuba.cn/credits/updateScore.jsp";
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("srid", type);
        paras.put("uid", uid);
        paras.put("appid", 209);
        paras.put("idindex", id);
        paras.put("mobile", 1);
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss", Locale.CHINA);//设置日期格式
        if (type == 40) {
            try {
                paras.put("flag", Base64.encodeToString(URLEncoder.encode(df.format(new Date()), "UTF-8").getBytes(), Base64.DEFAULT));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        } else {
            paras.put("flag", "1234567890" + df.format(new Date()));
        }
        url = ParameterUrl.setRequestParameter(originalUrl, paras);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> apiEntity = new BaseApiEntity<>();
        int result = jsonObject.getIntValue("result");
        switch (result) {
            case 200:
                apiEntity.setState(BaseApiEntity.SUCCESS);
                apiEntity.setValue(jsonObject.getString("totalcredit"));
                apiEntity.setMessage(jsonObject.getString("addcredit"));
                UserInfo userInfo = AccountManager.getInstance().getUserInfo();
                if (userInfo != null) {
                    userInfo.setIcoins(apiEntity.getValue());
                }
                break;
            case 201:
            case 203:
                apiEntity.setState(BaseApiEntity.FAIL);
                apiEntity.setMessage(jsonObject.getString("message"));
                break;
            default:
                apiEntity.setState(BaseApiEntity.ERROR);
                apiEntity.setMessage(RuntimeManager.getInstance().getString(R.string.unknown_error));
                break;
        }
        return apiEntity;
    }
}

