package com.iyuba.music.request.account;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
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
public class LoginRequest {
    private static LoginRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";

    public LoginRequest() {
    }

    public static LoginRequest getInstance() {
        if (instance == null) {
            instance = new LoginRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        UserInfo userInfo = null;
                        String nodeName;
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("response".equals(nodeName)) {
                                        userInfo = new UserInfo();
                                    }
                                    if ("result".equals(nodeName)) {
                                        String result = xmlPullParser.nextText();
                                        if (result.equals("101")) {
                                            apiEntity.setState(BaseApiEntity.State.SUCCESS);
                                        } else {
                                            apiEntity.setState(BaseApiEntity.State.FAIL);
                                        }
                                    }
                                    if ("uid".equals(nodeName)) {
                                        userInfo.setUid(xmlPullParser.nextText());
                                    }
                                    if ("username".equals(nodeName)) {
                                        userInfo.setUsername(xmlPullParser.nextText());
                                    }
                                    if ("vipStatus".equals(nodeName)) {
                                        userInfo.setVipStatus(xmlPullParser.nextText());
                                    }
                                    if ("Amount".equals(nodeName)) {
                                        userInfo.setIyubi(xmlPullParser.nextText());
                                    }
                                    if ("email".equals(nodeName)) {
                                        userInfo.setUserEmail(xmlPullParser.nextText());
                                    }
                                    if ("jiFen".equals(nodeName)) {
                                        if (xmlPullParser.nextText().equals("0")) {
                                            apiEntity.setMessage("no");
                                        } else {
                                            apiEntity.setMessage("add");
                                        }
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
        para.put("protocol", 11001);
        para.put("platform", "android");
        para.put("username", paras[0]);
        para.put("password", MD5.getMD5ofStr(paras[1]));
        para.put("x", paras[2]);
        para.put("y", paras[3]);
        para.put("appid", ConstantManager.instance.getAppId());
        para.put("format", "xml");
        para.put("sign", MD5.getMD5ofStr("11001" + paras[0]
                + MD5.getMD5ofStr(paras[1]) + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
