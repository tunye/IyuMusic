package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;
import com.iyuba.music.volley.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by 10202 on 2015/10/8.
 */
public class PayRequest {
    public static void exeRequest(String url, final IProtocolResponse<BaseApiEntity<UserInfo>> response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        UserInfo userInfo = AccountManager.getInstance().getUserInfo();
                        BaseApiEntity<UserInfo> apiEntity = new BaseApiEntity<>();
                        String nodeName;
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("response".equals(nodeName)) {

                                    }
                                    if ("result".equals(nodeName)) {
                                        String result = xmlPullParser.nextText();
                                        if (result.equals("1")) {
                                            apiEntity.setState(BaseApiEntity.SUCCESS);
                                        } else {
                                            apiEntity.setState(BaseApiEntity.FAIL);
                                        }
                                    }
                                    if ("msg".equals(nodeName)) {
                                        apiEntity.setMessage(xmlPullParser.nextText());
                                    }
                                    if ("amount".equals(nodeName)) {
                                        userInfo.setIyubi(xmlPullParser.nextText());
                                    }
                                    if ("VipFlg".equals(nodeName)) {
                                        userInfo.setVipStatus(xmlPullParser.nextText());
                                    }
                                    if ("VipEndTime".equals(nodeName)) {
                                        userInfo.setDeadline(xmlPullParser.nextText().split(" ")[0]);
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("response".equals(nodeName)) {
                                        apiEntity.setData(userInfo);
                                        response.response(apiEntity);
                                    }
                                    break;
                            }
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        response.onServerError(RuntimeManager.getInstance().getString(R.string.data_error));
                    } catch (IOException e) {
                        e.printStackTrace();
                        response.onServerError(RuntimeManager.getInstance().getString(R.string.data_error));
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
            response.onNetError(RuntimeManager.getInstance().getString(R.string.no_internet));
        }
    }

    public static String generateUrl(String[] paras) {
        String originalUrl = "http://app.iyuba.cn/pay/payVipApi.jsp";
        Map<String, Object> para = new ArrayMap<>();
        para.put("userId", paras[0]);
        para.put("amount", paras[1]);
        para.put("appId", ConstantManager.appId);
        para.put("productId", paras[2]);
        para.put("sign", MD5.getMD5ofStr(paras[1] + ConstantManager.appId
                + paras[0] + paras[2] + "iyuba"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
