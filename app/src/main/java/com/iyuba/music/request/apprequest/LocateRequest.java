package com.iyuba.music.request.apprequest;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class LocateRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        JSONArray JsonArrayData = jsonObject.getJSONArray("results");
                        String type;
                        StringBuilder region = new StringBuilder();
                        if (JsonArrayData != null) {
                            JSONObject jsonLocationData = JsonArrayData.getJSONObject(0);
                            JSONArray JsonArrayDataInner = jsonLocationData
                                    .getJSONArray("address_components");
                            if (JsonArrayDataInner != null) {
                                int size = JsonArrayDataInner.length();
                                JSONObject jsonPositionData;
                                JSONArray JsonArrayDataType;
                                for (int i = 0; i < size; i++) {
                                    jsonPositionData = JsonArrayDataInner.getJSONObject(i);
                                    JsonArrayDataType = jsonPositionData
                                            .getJSONArray("types");
                                    type = JsonArrayDataType.get(0).toString();
                                    if (type.equals("administrative_area_level_1")) {
                                        region.append(jsonPositionData.getString("short_name")).append(' ');
                                    }
                                    if (type.equals("locality")) {
                                        region.append(jsonPositionData.getString("short_name")).append(' ');
                                    }
                                    if (type.equals("sublocality")) {
                                        region.append(jsonPositionData.getString("short_name")).append(' ');
                                    }
                                }
                            }
                        }
                        response.response(region.toString());
                    } catch (JSONException e) {
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

    public static String generateUrl(double latitude, double longitude) {
        return "http://maps.google.cn/maps/api/geocode/json?latlng=" + latitude + "," + longitude + "&sensor=true&language=zh-CN";
    }
}
