package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by 10202 on 2015/10/8.
 */
public class StudyTimeRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            MyJsonRequest request = new MyJsonRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        BaseApiEntity apiEntity = new BaseApiEntity();
                        if (jsonObject.getInt("result") == 1) {
                            apiEntity.setState(BaseApiEntity.SUCCESS);
                            apiEntity.setData(jsonObject.optInt("totalTime", 0));
                        } else {
                            apiEntity.setState(BaseApiEntity.FAIL);
                        }
                        response.response(apiEntity);
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

    public static String generateUrl() {
        String originalUrl = "http://daxue.iyuba.com/ecollege/getMyTime.jsp";
        ArrayMap<String, Object> para = new ArrayMap<>();
        Calendar init = Calendar.getInstance();
        init.set(1970, 0, 1, 0, 0, 0);
        Calendar now = Calendar.getInstance();
        long intervalMilli = now.getTimeInMillis() - init.getTimeInMillis();
        int days = (int) (intervalMilli / (24 * 60 * 60 * 1000));
        para.put("uid", AccountManager.getInstance().getUserId());
        para.put("day", days);
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
