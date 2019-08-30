package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.XMLRequest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

/**
 * Created by 10202 on 2015/10/8.
 */
public class ReadCountAddRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    if (response != null) {
                                        response.response("success");
                                    }
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

    public static String generateUrl(int voaid, String app) {
        String originalUrl = "http://daxue.iyuba.cn/appApi/UnicomApi";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 70001);
        para.put("counts", Utils.getRandomInt(2) + 1);
        para.put("format", "json");
        para.put("appName", app);
        para.put("voaids", voaid);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
