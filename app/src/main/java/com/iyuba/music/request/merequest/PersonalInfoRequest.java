package com.iyuba.music.request.merequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/8.
 */
public class PersonalInfoRequest {
    public static void exeRequest(String url,final UserInfo userInfo, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        String nodeName;
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("username".equals(nodeName)) {
                                        userInfo.setUsername(xmlPullParser.nextText());
                                    }
                                    if ("doings".equals(nodeName)) {
                                        userInfo.setDoings(xmlPullParser.nextText());
                                    }
                                    if ("views".equals(nodeName)) {
                                        userInfo.setViews(xmlPullParser.nextText());
                                    }
                                    if ("gender".equals(nodeName)) {
                                        userInfo.setGender(xmlPullParser.nextText());
                                    }
                                    if ("text".equals(nodeName)) {
                                        userInfo.setText(ParameterUrl.decode(xmlPullParser.nextText()));
                                    }
                                    if ("follower".equals(nodeName)) {
                                        userInfo.setFollower(xmlPullParser.nextText());
                                    }
                                    if ("following".equals(nodeName)) {
                                        userInfo.setFollowing(xmlPullParser.nextText());
                                    }
                                    if ("notification".equals(nodeName)) {
                                        userInfo.setNotification(xmlPullParser.nextText());
                                    }
                                    if ("distance".equals(nodeName)) {
                                        userInfo.setDistance(xmlPullParser.nextText());
                                    }
                                    if ("relation".equals(nodeName)) {
                                        userInfo.setRelation(xmlPullParser.nextText());
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("response".equals(nodeName)) {
                                        apiEntity.setState(BaseApiEntity.State.SUCCESS);
                                        apiEntity.setData(userInfo);
                                    } else {
                                        apiEntity.setState(BaseApiEntity.State.FAIL);
                                    }
                                    response.response(apiEntity);
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

    public static String generateUrl(String id, String myid) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 20001);
        para.put("platform", "android");
        para.put("id", id);
        para.put("myid", myid);
        para.put("format", "xml");
        para.put("sign", MD5.getMD5ofStr("20001" + id + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
