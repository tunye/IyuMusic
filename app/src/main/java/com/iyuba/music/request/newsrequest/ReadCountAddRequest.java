package com.iyuba.music.request.newsrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/8.
 */
public class ReadCountAddRequest {
    private static ReadCountAddRequest instance;
    private final String originalUrl = "http://daxue.iyuba.com/appApi/UnicomApi";
    private boolean result;

    public ReadCountAddRequest() {
    }

    public static synchronized ReadCountAddRequest getInstance() {
        if (instance == null) {
            instance = new ReadCountAddRequest();
        }
        return instance;
    }

    public void exeRequest(String url) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        result = false;
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    result = true;
                                    break;
                                case XmlPullParser.END_TAG:
                                    break;
                            }
                        }
                    } catch (XmlPullParserException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                }
            });
            MyVolley.getInstance().addToRequestQueue(request);
        } else {
        }
    }

    public String generateUrl(int voaid, String app) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("protocol", 70001);
        para.put("counts", 1);
        para.put("format", "json");
        para.put("appName", app);
        para.put("voaids", voaid);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
