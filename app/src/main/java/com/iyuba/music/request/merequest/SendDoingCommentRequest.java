package com.iyuba.music.request.merequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.iyuba.music.R;
import com.iyuba.music.entity.doings.Doing;
import com.iyuba.music.entity.doings.DoingComment;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 10202 on 2015/9/30.
 */
public class SendDoingCommentRequest {
    public static void exeRequest(String url, final IProtocolResponse response) {
        if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
            JsonObjectRequest request = new JsonObjectRequest(
                    url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    try {
                        response.response(jsonObject.getString("result"));
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

    public static String generateUrl(DoingComment doingComment, Doing doing, String fromUid, String fromMessage) {
        String originalUrl = "http://api.iyuba.com.cn/v2/api.iyuba";
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("protocol", 30007);
        para.put("uid", doingComment.getUid());
        para.put("username", ParameterUrl.encode(doingComment.getUsername()));
        para.put("doid", doing.getDoid());
        para.put("upid", doingComment.getUpid());
        para.put("message", ParameterUrl.encode(ParameterUrl.encode(doingComment.getMessage())));
        para.put("grade", doingComment.getGrade());
        para.put("fromUid", fromUid);// 要回复的人的id
        para.put("fromMsg", ParameterUrl.encode(ParameterUrl.encode(fromMessage)));// 要回复的人的内容
        para.put("orignUid", doing.getUid());// 该doing 作者的id
        para.put("orignMsg", ParameterUrl.encode(ParameterUrl.encode(doing.getMessage())));// 该doing 的内容
        para.put("sign", MD5.getMD5ofStr(30007 + doingComment.getUid()
                + doingComment.getUsername() + doingComment.getMessage() + "iyubaV2"));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
