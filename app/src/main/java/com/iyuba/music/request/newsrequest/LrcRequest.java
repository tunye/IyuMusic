package com.iyuba.music.request.newsrequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by 10202 on 2015/9/30.
 */
public class LrcRequest {
    private static LrcRequest instance;
    private final String musicOriginalUrl = "http://apps.iyuba.com/afterclass/getLyrics.jsp";
    private final String bbcOriginalUrl = "http://apps.iyuba.com/minutes/textApi.jsp";
    private final String voaOriginalUrl = "http://apps.iyuba.com/voa/textNewApi.jsp";

    public LrcRequest() {
    }

    public static LrcRequest getInstance() {
        if (instance == null) {
            instance = new LrcRequest();
        }
        return instance;
    }

    public void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    BaseListEntity baseListEntity = new BaseListEntity();
                    Type listType = new TypeToken<ArrayList<Original>>() {
                    }.getType();
                    try {
                        baseListEntity.setTotalCount(jsonObject.getInt("total"));
                        ArrayList<Original> list = new Gson().fromJson(jsonObject.getString("data"), listType);
                        baseListEntity.setData(list);
                        response.response(baseListEntity);
                    } catch (JSONException e) {
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

    public String generateUrl(int id, int type) {
        String url;
        switch (type) {
            case 0:
                url = ParameterUrl.setRequestParameter(voaOriginalUrl, "voaid", id);
                break;
            case 1:
                url = ParameterUrl.setRequestParameter(bbcOriginalUrl, "bbcid", id);
                break;
            case 2:
                url = ParameterUrl.setRequestParameter(musicOriginalUrl, "SongId", id);
                break;
            default:
                url = "";
                break;
        }
        return url;

    }
}
