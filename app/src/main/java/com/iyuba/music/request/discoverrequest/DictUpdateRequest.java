package com.iyuba.music.request.discoverrequest;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by 10202 on 2015/12/5.
 */
public class DictUpdateRequest extends Request<Integer> {
    public DictUpdateRequest(String... paras) {
        String originalUrl = "http://word.iyuba.cn/words/updateWord.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("userId", paras[0]);
        para.put("mod", paras[1]);
        para.put("word", ParameterUrl.encode(paras[2]));
        para.put("groupName", "Iyuba");
        url = ParameterUrl.setRequestParameter(originalUrl, para);
        returnDataType = Request.XML_DATA;
    }

    @Override
    public Integer parseXmlImpl(XmlPullParser xmlPullParser) {
        try {
            String nodeName;
            for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("result".equals(nodeName)) {
                            return Integer.parseInt(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
            }
        } catch (XmlPullParserException | IOException e) {
            // do nothing
        }
        return null;
    }
}
