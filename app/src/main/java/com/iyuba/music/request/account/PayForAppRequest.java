package com.iyuba.music.request.account;

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
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/8.
 */
public class PayForAppRequest {
    private static PayForAppRequest instance;
    private final String originalUrl = "http://app.iyuba.com/pay/apiPayByDate.jsp";

    public PayForAppRequest() {
    }

    public static PayForAppRequest getInstance() {
        if (instance == null) {
            instance = new PayForAppRequest();

        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        UserInfo userInfo = AccountManager.instance.getUserInfo();
                        BaseApiEntity baseApiEntity = new BaseApiEntity();
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
                                            baseApiEntity.setState(BaseApiEntity.State.SUCCESS);
                                        } else {
                                            baseApiEntity.setState(BaseApiEntity.State.FAIL);
                                        }
                                    }
                                    if ("msg".equals(nodeName)) {
                                        baseApiEntity.setMessage(xmlPullParser.nextText());
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
                                        baseApiEntity.setData(userInfo);
                                        response.response(baseApiEntity);
                                    }
                                    break;
                            }
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                        response.onServerError(RuntimeManager.getString(R.string.data_error));
                    } catch (IOException e) {
                        e.printStackTrace();
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

    public String generateUrl(String[] paras) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("userId", paras[0]);
        para.put("amount", paras[1]);
        para.put("month", 0);
        para.put("appId", ConstantManager.instance.getAppId());
        para.put("productId", 0);
        para.put("sign", MD5.getMD5ofStr(paras[1] + ConstantManager.instance.getAppId()
                + paras[0] + "00iyuba"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
