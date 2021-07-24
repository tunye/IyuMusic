package com.iyuba.music.request.account;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfo;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by 10202 on 2015/10/8.
 */
public class PayRequest extends Request<BaseApiEntity<UserInfo>> {

    public PayRequest(String[] paras) {
        String originalUrl = "http://app.iyuba.cn/pay/payVipApi.jsp";
        Map<String, Object> para = new ArrayMap<>();
        para.put("userId", paras[0]);
        para.put("amount", paras[1]);
        para.put("appId", ConstantManager.appId);
        para.put("productId", paras[2]);
        para.put("sign", MD5.getMD5ofStr(paras[1] + ConstantManager.appId
                + paras[0] + paras[2] + "iyuba"));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        returnDataType = Request.XML_DATA;
    }

    @Override
    public BaseApiEntity<UserInfo> parseXmlImpl(XmlPullParser xmlPullParser) {
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
