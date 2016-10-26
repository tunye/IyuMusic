package com.iyuba.music.request.newsrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import java.util.HashMap;

/**
 * Created by 10202 on 2015/10/8.
 */
public class FavorRequest {
    private static FavorRequest instance;
    private final String originalUrl = "http://apps.iyuba.com/afterclass/updateCollect.jsp";
    private String result;

    public FavorRequest() {
    }

    public static synchronized FavorRequest getInstance() {
        if (instance == null) {
            instance = new FavorRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            StringRequest request = new StringRequest(url, new Response.Listener<String>() {
                @Override
                public void onResponse(String message) {
                    if (message.contains("del")) {
                        response.response("del");
                    } else if (message.contains("insert")) {
                        response.response("insert");
                    } else {
                        response.response("");
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

    public String generateUrl(String userid, int voaid, String type) {
        HashMap<String, Object> para = new HashMap<>();
        para.put("userId", userid);
        para.put("voaId", voaid);
        para.put("type", type);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
