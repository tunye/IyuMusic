package com.iyuba.music.request.account;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;
import com.tencent.mm.sdk.modelpay.PayReq;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 * Created by 10202 on 2017/1/10.
 */

public class WxPay {
    private static WxPay instance;
    private final String originalUrl = "http://vip.iyuba.com/weixinPay.jsp";

    private WxPay() {
    }

    public static WxPay getInstance() {
        if (instance == null) {
            instance = new WxPay();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, new JSONObject(), new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        apiEntity.setMessage(jsonObject.getString("retmsg"));
                        if (0 == jsonObject.getInt("retcode")) {
                            apiEntity.setState(BaseApiEntity.State.SUCCESS);
                            PayReq req = new PayReq();
                            req.appId=ConstantManager.WXSECRET;
                            req.partnerId = jsonObject.getString("mch_id");
                            req.prepayId = jsonObject.getString("prepayid");
                            req.nonceStr = jsonObject.getString("noncestr");
                            req.timeStamp = jsonObject.getString("timestamp");
                            req.packageValue = "Sign=WXPay";
                            req.sign = buildWeixinSign(req, jsonObject.getString("mch_key"));
                            apiEntity.setData(req);
                        } else {
                            apiEntity.setState(BaseApiEntity.State.FAIL);
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

    public String generateUrl(String cost, String month, String productId) {
        HashMap<String, Object> paras = new HashMap<>();
        paras.put("wxkey", ConstantManager.WXSECRET);
        paras.put("format", "json");
        paras.put("money", cost);
        paras.put("appid", ConstantManager.instance.getAppId());
        paras.put("uid", AccountManager.instance.getUserId());
        paras.put("amount", month);
        paras.put("productid", productId);
        paras.put("sign", generateSign(ConstantManager.instance.getAppId(),
                AccountManager.instance.getUserId(), cost, month));
        return ParameterUrl.setRequestParameter(originalUrl, paras);
    }

    private String generateSign(String appid, String uid, String money, String amount) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        StringBuilder sb = new StringBuilder();
        sb.append(appid).append(uid).append(money).append(amount);
        sb.append(sdf.format(System.currentTimeMillis()));
        return MD5.getMD5ofStr(sb.toString());
    }

    private String buildWeixinSign(PayReq payReq, String key) {
        StringBuilder sb = new StringBuilder();
        sb.append("appid=").append(payReq.appId);
        sb.append("&noncestr=").append(payReq.nonceStr);
        sb.append("&package=").append(payReq.packageValue);
        sb.append("&partnerid=").append(payReq.partnerId);
        sb.append("&prepayid=").append(payReq.prepayId);
        sb.append("&timestamp=").append(payReq.timeStamp);
        sb.append("&key=").append(key);
        return MD5.getMD5ofStr(sb.toString()).toUpperCase();
    }
}
