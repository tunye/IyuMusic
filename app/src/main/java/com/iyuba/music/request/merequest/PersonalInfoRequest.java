package com.iyuba.music.request.merequest;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.listener.IProtocolResponse;
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
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/8.
 */
public class PersonalInfoRequest {
    private static PersonalInfoRequest instance;
    private final String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";

    public PersonalInfoRequest() {

    }

    public static PersonalInfoRequest getInstance() {
        if (instance == null) {
            instance = new PersonalInfoRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final UserInfo user, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        UserInfo userInfo = user;
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        String nodeName;
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("result".equals(nodeName)) {
                                        if ("201".equals(xmlPullParser.nextText())) {
                                            apiEntity.setState(BaseApiEntity.State.SUCCESS);
                                        } else {
                                            apiEntity.setState(BaseApiEntity.State.FAIL);
                                        }
                                    }
                                    if ("username".equals(nodeName)) {
                                        userInfo.setUsername(xmlPullParser.nextText());
                                    }
                                    if ("icoins".equals(nodeName)) {
                                        userInfo.setIcoins(xmlPullParser.nextText());
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
                                    if ("amount".equals(nodeName)) {
                                        userInfo.setIyubi(xmlPullParser.nextText());
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

    public String generateUrl(String id, String myid) {
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
