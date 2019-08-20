package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by 10202 on 2017/1/10.
 */

public class WxPay {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        apiEntity.setMessage(jsonObject.getString("retmsg"));
                        if (0 == jsonObject.getInt("retcode")) {
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
                        response.response(apiEntity);
                    } catch (JSONException e) {
                        response.onServerError(RuntimeManager.getString(R.string.data_error));
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    response.onServerError(VolleyErrorHelper.getMessage(error));
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
            response.onNetError(RuntimeManager.getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String cost, String month, String productId) {
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
        return ParameterUrl.setRequestParameter(originalUrl, paras);
    }

    private static String generateSign(String appid, String uid, String money, String amount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd", Locale.CHINA);
        String sb = appid + uid + money + amount +
                sdf.format(System.currentTimeMillis());
        return MD5.getMD5ofStr(sb);
    }

    private static String buildWeixinSign(PayReq payReq, String key) {
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
