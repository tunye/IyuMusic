package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by 10202 on 2017/1/9.
 */

public class AliPay extends Request<BaseApiEntity<String>> {
    public AliPay(String subject, String body, String cost, String month, String productId) {
        String originalUrl = "http://vip.iyuba.cn/chargeapinew.jsp";
        Map<String, Object> paras = new ArrayMap<>();
        paras.put("WIDseller_email", "iyuba@sina.com");
        paras.put("WIDout_trade_no", getOutTradeNo());
        paras.put("WIDsubject", subject);
        paras.put("WIDbody", body);
        paras.put("WIDtotal_fee", cost);
        paras.put("WIDdefaultbank", "");
        paras.put("app_id", ConstantManager.appId);
        paras.put("userId", AccountManager.getInstance().getUserId());
        paras.put("amount", month);
        paras.put("product_id", productId);
        paras.put("code", generateCode(AccountManager.getInstance().getUserId()));
        url = ParameterUrl.setRequestParameter(originalUrl, paras);
    }

    private static String generateCode(String userId) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        return MD5.getMD5ofStr(userId + "iyuba" + df.format(System.currentTimeMillis()));
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> apiEntity = new BaseApiEntity<>();
        apiEntity.setMessage(jsonObject.getString("message"));
        if ("1".equals(jsonObject.getString("result"))) {
            apiEntity.setData(ParameterUrl.decode(jsonObject.getString("orderInfo")));
            apiEntity.setValue(jsonObject.getString("sign"));
            apiEntity.setState(BaseApiEntity.SUCCESS);
        } else {
            apiEntity.setState(BaseApiEntity.FAIL);
        }
        return apiEntity;
    }

    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);
        key = key + Math.abs(new Random().nextInt());
        key = key.substring(0, 15);
        return key;
    }

}
