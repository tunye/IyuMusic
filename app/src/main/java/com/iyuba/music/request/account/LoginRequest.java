package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Map;

/**
 * Created by 10202 on 2015/10/8.
 */
public class LoginRequest extends Request<BaseApiEntity<UserInfo>> {

    public LoginRequest(String[] paras) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        Map<String, Object> para = new ArrayMap<>();
        para.put("protocol", 11001);
        para.put("platform", "android");
        para.put("username", ParameterUrl.encode(ParameterUrl.encode(paras[0])));
        para.put("password", MD5.getMD5ofStr(paras[1]));
        para.put("x", paras[2]);
        para.put("y", paras[3]);
        para.put("appid", ConstantManager.appId);
        para.put("format", "xml");
        para.put("sign", MD5.getMD5ofStr("11001" + paras[0]
                + MD5.getMD5ofStr(paras[1]) + "iyubaV2"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        returnDataType = Request.XML_DATA;
    }

    @Override
    public BaseApiEntity<UserInfo> parseXmlImpl(XmlPullParser xmlPullParser) {
        try {
            UserInfo userInfo = new UserInfo();
            String nodeName;
            BaseApiEntity<UserInfo> apiEntity = new BaseApiEntity<>();
            for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("result".equals(nodeName)) {
                            String result = xmlPullParser.nextText();
                            if (result.equals("101")) {
                                apiEntity.setState(BaseApiEntity.SUCCESS);
                            } else {
                                apiEntity.setState(BaseApiEntity.FAIL);
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
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
                            long time = Long.parseLong(xmlPullParser.nextText()) * 1000;
                            long allLife = System.currentTimeMillis();
                            try {
                                allLife = sdf.parse("2099-12-31").getTime();
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (time > allLife) {
                                userInfo.setDeadline("终身VIP");
                            } else {
                                userInfo.setDeadline(sdf.format(new Timestamp(time)));
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("response".equals(nodeName)) {
                            if (!TextUtils.isEmpty(userInfo.getUid())) {
                                apiEntity.setData(userInfo);
                            } else if (apiEntity.getState() == BaseApiEntity.SUCCESS) {
                                apiEntity.setState(BaseApiEntity.ERROR);
                            }
                            return apiEntity;
                        }
                        break;
                }
            }
        } catch (XmlPullParserException | IOException e) {
            // do nothing
        }
        return null;
    }
}
