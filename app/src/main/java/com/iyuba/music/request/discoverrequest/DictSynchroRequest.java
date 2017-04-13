package com.iyuba.music.request.discoverrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;
import com.iyuba.music.volley.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Created by 10202 on 2015/10/8.
 */
public class DictSynchroRequest {
    private static DictSynchroRequest instance;
    private int totalPage, counts, currentPage;
    private String uid;

    private DictSynchroRequest() {
    }

    public static DictSynchroRequest getInstance() {
        if (instance == null) {
            instance = new DictSynchroRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        final String time = DateFormat.formatTime(Calendar.getInstance().getTime());
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G_3G)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        Word word = null;
                        ArrayList<Word> words = new ArrayList<>();
                        String nodeName;
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("counts".equals(nodeName)) {
                                        counts = Integer.parseInt(xmlPullParser.nextText());
                                    }
                                    if ("pageNumber".equals(nodeName)) {
                                        currentPage = Integer.parseInt(xmlPullParser.nextText());
                                    }
                                    if ("totalPage".equals(nodeName)) {
                                        totalPage = Integer.parseInt(xmlPullParser.nextText());
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
                                    if ("row".equals(nodeName)) {
                                        word.setUser(uid);
                                        word.setCreateDate(time);
                                        word.setViewCount("1");
                                        word.setIsdelete("0");
                                        words.add(word);
                                        word = new Word();
                                    }
                                    if ("response".equals(nodeName)) {
                                        response.response(words);
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

    public String generateUrl(String uid, int page) {
        String originalUrl = "http://word.iyuba.com/words/wordListService.jsp";
        this.uid = uid;
        ArrayMap<String, Object> map = new ArrayMap<>();
        map.put("u", uid);
        map.put("pageCounts", 500);
        map.put("pageNumber", page);
        return ParameterUrl.setRequestParameter(originalUrl, map);
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getCounts() {
        return counts;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
