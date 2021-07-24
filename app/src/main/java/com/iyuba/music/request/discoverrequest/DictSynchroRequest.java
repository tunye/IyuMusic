package com.iyuba.music.request.discoverrequest;

import android.support.v4.util.ArrayMap;

import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ParameterUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by 10202 on 2015/10/8.
 */
public class DictSynchroRequest extends Request<BaseListEntity<List<Word>>> {
    private static DictSynchroRequest instance;
    private String uid;

    public DictSynchroRequest(String uid, int page) {
        String originalUrl = "http://word.iyuba.cn/words/wordListService.jsp";
        this.uid = uid;
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("u", uid);
        map.put("pageCounts", 500);
        map.put("pageNumber", page);
        url = ParameterUrl.setRequestParameter(originalUrl, map);
        returnDataType = Request.XML_DATA;
    }

    @Override
    public BaseListEntity<List<Word>> parseXmlImpl(XmlPullParser xmlPullParser) {
        try {
            Word word = null;
            BaseListEntity<List<Word>> result = new BaseListEntity<>();
            List<Word> words = new ArrayList<>();
            String nodeName;
            final String time = DateFormat.formatTime(Calendar.getInstance().getTime());
            for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("counts".equals(nodeName)) {
                            result.setTotalCount(Integer.parseInt(xmlPullParser.nextText()));
                        }
                        if ("pageNumber".equals(nodeName)) {
                            result.setCurPage(Integer.parseInt(xmlPullParser.nextText()));
                        }
                        if ("totalPage".equals(nodeName)) {
                            result.setTotalPage(Integer.parseInt(xmlPullParser.nextText()));
                        }
                        if ("row".equals(nodeName)) {
                            word = new Word();
                        }
                        if (word != null && "Word".equals(nodeName)) {
                            word.setWord(xmlPullParser.nextText());
                        }
                        if (word != null && "Audio".equals(nodeName)) {
                            word.setPronMP3(xmlPullParser.nextText());
                        }
                        if (word != null && "Pron".equals(nodeName)) {
                            word.setPron(xmlPullParser.nextText());
                        }
                        if (word != null && "Def".equals(nodeName)) {
                            word.setDef(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("row".equals(nodeName) && word != null) {
                            word.setUser(uid);
                            word.setCreateDate(time);
                            word.setViewCount("1");
                            word.setIsdelete("0");
                            words.add(word);
                            word = new Word();
                        }
                        if ("response".equals(nodeName)) {
                            result.setData(words);
                            result.setIsLastPage(result.getCurPage() == result.getTotalPage());
                            return result;
                        }
                        break;
                }
            }
        } catch (XmlPullParserException | IOException e) {
        }
        return null;
    }
}
