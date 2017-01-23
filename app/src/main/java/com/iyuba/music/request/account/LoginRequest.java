package com.iyuba.music.request.account;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;
import com.iyuba.music.volley.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/8.
 */
public class LoginRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        Log.e("aaa", url);
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        UserInfo userInfo = new UserInfo();
                        String nodeName;
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        Log.e("aaa", new Gson().toJson(xmlPullParser));
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    nodeName = xmlPullParser.getName();
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
                                        if ("0".equals(xmlPullParser.nextText())) {
                                            apiEntity.setMessage("no");
                                        } else {
                                            apiEntity.setMessage("add");
                                        }
                                    }
                                    if ("credits".equals(nodeName)) {
                                        userInfo.setIcoins(xmlPullParser.nextText());
                                    }
                                    if ("expireTime".equals(nodeName)) {
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                                        long time = Long.parseLong(xmlPullParser.nextText());
                                        long allLife = Calendar.getInstance().SECOND;
                                        try {
                                            allLife = sdf.parse("2099-01-01").getTime() / 1000;
                                        } catch (ParseException e) {

                                        }
                                        if (time > allLife) {
                                            userInfo.setDeadline("终身VIP");
                                        } else {
                                            userInfo.setDeadline(sdf.format(new Date(time * 1000)));
                                        }
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("response".equals(nodeName)) {
                                        if (!TextUtils.isEmpty(userInfo.getUid())) {
                                            apiEntity.setData(userInfo);
                                        } else {
                                            apiEntity.setState(BaseApiEntity.State.ERROR);
                                        }
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

    public static String generateUrl(String[] paras) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 11001);
        para.put("platform", "android");
        para.put("username", TextAttr.encode(TextAttr.encode(paras[0])));
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
