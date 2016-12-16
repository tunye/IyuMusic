package com.iyuba.music.request.discoverrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.word.ExampleSentence;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;
import com.iyuba.music.volley.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/10/8.
 */
public class DictRequest {
    private static DictRequest instance;
    private final String originalUrl = "http://word.iyuba.com/words/apiWord.jsp";

    public DictRequest() {
    }

    public static DictRequest getInstance() {
        if (instance == null) {
            instance = new DictRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
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
                                    if ("key".equals(nodeName)) {
                                        word.setWord(xmlPullParser.nextText());
                                    }
                                    if ("audio".equals(nodeName)) {
                                        word.setPronMP3(xmlPullParser.nextText());
                                    }
                                    if ("pron".equals(nodeName)) {
                                        word.setPron(xmlPullParser.nextText());
                                    }
                                    if ("def".equals(nodeName)) {
                                        word.setDef(xmlPullParser.nextText());
                                    }
                                    if ("sent".equals(nodeName)) {
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
                                    if ("data".equals(nodeName)) {
                                        word.setSentences(sentences);
                                        response.response(word);
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

    public String generateUrl(String word) {
        return ParameterUrl.setRequestParameter(originalUrl, "q", ParameterUrl.encode(word));
    }
}
