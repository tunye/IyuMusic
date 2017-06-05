package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.article.StudyRecord;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.GetMAC;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.volley.MyJsonRequest;
import com.iyuba.music.volley.MyVolley;
import com.iyuba.music.volley.VolleyErrorHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by 10202 on 2015/10/8.
 */
public class StudyRecordRequest {
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
                            if (jsonObject.getInt("jifen") == 0) {
                                apiEntity.setMessage("no");
                            } else {
                                apiEntity.setMessage("add");
                            }
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

    public static String generateUrl(String uid, StudyRecord studyRecord) {
        String originalUrl = "http://daxue.iyuba.com/ecollege/updateStudyRecordNew.jsp";
        String device = android.os.Build.BRAND + android.os.Build.MODEL
                + android.os.Build.DEVICE;
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("appId", ConstantManager.appId);
        para.put("Lesson", studyRecord.getLesson());
        para.put("LessonId", studyRecord.getId());
        para.put("BeginTime", TextAttr.encode(studyRecord.getStartTime()));
        para.put("EndTime", TextAttr.encode(studyRecord.getEndTime()));
        para.put("EndFlg", studyRecord.getFlag());
        para.put("uid", uid);
        para.put("Device", TextAttr.encode(device));
        para.put("DeviceId", GetMAC.getMAC());
        para.put("TestNumber", SettingConfigManager.getInstance().getStudyMode());
        para.put("platform", "android");
        para.put("sign", MD5.getMD5ofStr(uid + studyRecord.getStartTime() + DateFormat.formatYear(Calendar.getInstance().getTime())));
        return ParameterUrl.setRequestParameter(originalUrl, para);
    }
}
