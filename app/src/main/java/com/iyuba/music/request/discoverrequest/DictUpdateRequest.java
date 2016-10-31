package com.iyuba.music.request.discoverrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
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
import java.util.HashMap;

/**
 * Created by 10202 on 2015/12/5.
 */
public class DictUpdateRequest {
    private static DictUpdateRequest instance;
    private final String originalUrl = "http://word.iyuba.com/words/updateWord.jsp";

    public DictUpdateRequest() {
    }

    public static  DictUpdateRequest getInstance() {
        if (instance == null) {
            instance = new DictUpdateRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            XMLRequest request = new XMLRequest(url, new Response.Listener<XmlPullParser>() {
                @Override
                public void onResponse(XmlPullParser xmlPullParser) {
                    try {
                        String nodeName;
                        for (int eventType = xmlPullParser.getEventType(); eventType != XmlPullParser.END_DOCUMENT; eventType = xmlPullParser.next()) {
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    nodeName = xmlPullParser.getName();
                                    if ("result".equals(nodeName)) {
                                        response.response(Integer.parseInt(xmlPullParser.nextText()));
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
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

    public String generateUrl(String... paras) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("userId", paras[0]);
        para.put("mod", paras[1]);
        para.put("word", ParameterUrl.encode(paras[2]));
        para.put("groupName", "Iyuba");
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
