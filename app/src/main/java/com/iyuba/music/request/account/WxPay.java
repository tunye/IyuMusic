package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.tencent.mm.opensdk.modelpay.PayReq;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by 10202 on 2017/1/10.
 */

public class WxPay extends Request<BaseApiEntity<PayReq>> {

    public WxPay(String cost, String month, String productId) {
        String originalUrl = "http://vip.iyuba.cn/weixinPay.jsp";
        ArrayMap<String, Object> paras = new ArrayMap<>();
        paras.put("wxkey", ConstantManager.WXID);
        paras.put("format", "json");
        paras.put("money", cost);
        paras.put("appid", ConstantManager.appId);
        paras.put("uid", AccountManager.getInstance().getUserId());
        paras.put("amount", month);
        paras.put("productid", productId);
        paras.put("sign", generateSign(ConstantManager.appId,
                AccountManager.getInstance().getUserId(), cost, month));
        url = ParameterUrl.setRequestParameter(originalUrl, paras);
    }

    @Override
    public BaseApiEntity<PayReq> parseJsonImpl(com.alibaba.fastjson.JSONObject jsonObject) {
        BaseApiEntity<PayReq> apiEntity = new BaseApiEntity<>();
        apiEntity.setMessage(jsonObject.getString("retmsg"));
        if (0 == jsonObject.getIntValue("retcode")) {
            apiEntity.setState(BaseApiEntity.SUCCESS);
            PayReq req = new PayReq();
            req.appId = ConstantManager.WXID;
            req.partnerId = jsonObject.getString("mch_id");
            req.prepayId = jsonObject.getString("prepayid");
            req.nonceStr = jsonObject.getString("noncestr");
            req.timeStamp = jsonObject.getString("timestamp");
            req.packageValue = "Sign=WXPay";
            req.sign = buildWeixinSign(req, jsonObject.getString("mch_key"));
            apiEntity.setData(req);
        } else {
            apiEntity.setState(BaseApiEntity.FAIL);
        }
        return apiEntity;
    }

    private String generateSign(String appid, String uid, String money, String amount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String sb = appid + uid + money + amount +
                sdf.format(System.currentTimeMillis());
        return MD5.getMD5ofStr(sb);
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        String sb = "appid=" + payReq.appId +
                "&noncestr=" + payReq.nonceStr +
                "&package=" + payReq.packageValue +
                "&partnerid=" + payReq.partnerId +
                "&prepayid=" + payReq.prepayId +
                "&timestamp=" + payReq.timeStamp +
                "&key=" + key;
        return MD5.getMD5ofStr(sb).toUpperCase();
    }
}
