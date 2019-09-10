package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by 10202 on 2015/10/8.
 */
public class PersonalInfoRequest extends Request<BaseApiEntity<UserInfo>> {
    private final UserInfo userInfo;

    public PersonalInfoRequest(String id, String myid, UserInfo userInfo) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 20001);
        para.put("platform", "android");
        para.put("id", id);
        para.put("myid", myid);
        para.put("format", "xml");
        para.put("sign", MD5.getMD5ofStr("20001" + id + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        returnDataType = Request.XML_DATA;
        this.userInfo = userInfo;
    }

    @Override
    public BaseApiEntity<UserInfo> parseXmlImpl(XmlPullParser xmlPullParser) {
        try {
            BaseApiEntity<UserInfo> apiEntity = new BaseApiEntity<>();
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
                        if ("icoins".equals(nodeName)) {
                            userInfo.setIcoins(xmlPullParser.nextText());
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
                        if ("vipStatus".equals(nodeName) && !userInfo.getUid().equals(AccountManager.getInstance().getUserId())) {
                            userInfo.setVipStatus(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("response".equals(nodeName)) {
                            apiEntity.setState(BaseApiEntity.SUCCESS);
                            apiEntity.setData(userInfo);
                        } else {
                            apiEntity.setState(BaseApiEntity.FAIL);
                        }
                        return apiEntity;
                }
            }
        } catch (XmlPullParserException | IOException e) {
            // do nothing
        }
        return null;
    }
}
