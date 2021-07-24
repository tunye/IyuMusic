package com.iyuba.music.request.discoverrequest;

import com.iyuba.music.entity.word.ExampleSentence;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.ParameterUrl;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/8.
 */
public class DictRequest extends Request<Word> {

    public DictRequest(String word) {
        String originalUrl = "http://word.iyuba.cn/words/apiWord.jsp";
        url = ParameterUrl.setRequestParameter(originalUrl, "q", ParameterUrl.encode(word));
        returnDataType = Request.XML_DATA;
    }

    @Override
    public Word parseXmlImpl(XmlPullParser xmlPullParser) {
        try {
            Word word = null;
            ArrayList<ExampleSentence> sentences = new ArrayList<>();
            ExampleSentence sentence = null;
            String nodeName;
            for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("data".equals(nodeName)) {
                            word = new Word();
                        }
                        if (word != null && "key".equals(nodeName)) {
                            word.setWord(xmlPullParser.nextText());
                        }
                        if (word != null && "audio".equals(nodeName)) {
                            word.setPronMP3(xmlPullParser.nextText());
                        }
                        if (word != null && "pron".equals(nodeName)) {
                            word.setPron(xmlPullParser.nextText());
                        }
                        if (word != null && "def".equals(nodeName)) {
                            word.setDef(xmlPullParser.nextText());
                        }
                        if (word != null && "sent".equals(nodeName)) {
                            sentence = new ExampleSentence();
                        }
                        if (sentence != null && "number".equals(nodeName)) {
                            sentence.setIndex(Integer.parseInt(xmlPullParser.nextText()));
                        }
                        if (sentence != null && "orig".equals(nodeName)) {
                            sentence.setSentence(xmlPullParser.nextText());
                        }
                        if (sentence != null && "trans".equals(nodeName)) {
                            sentence.setSentence_cn(xmlPullParser.nextText());
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        nodeName = xmlPullParser.getName();
                        if ("sent".equals(nodeName)) {
                            sentences.add(sentence);
                        }
                        if (word != null && "data".equals(nodeName)) {
                            word.setSentences(sentences);
                            return word;
                        }
                        break;
                }
            }
        } catch (XmlPullParserException | IOException e) {
            return null;
        }
        return null;
    }
}
