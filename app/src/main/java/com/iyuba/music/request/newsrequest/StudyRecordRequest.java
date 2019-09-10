package com.iyuba.music.request.newsrequest;

import android.support.v4.util.ArrayMap;

import com.alibaba.fastjson.JSONObject;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.article.StudyRecord;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.Request;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.MD5;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.util.Utils;

import java.util.Calendar;

/**
 * Created by 10202 on 2015/10/8.
 */
public class StudyRecordRequest extends Request<BaseApiEntity<String>> {
    public StudyRecordRequest(String uid, StudyRecord studyRecord) {
        String originalUrl = "http://daxue.iyuba.cn/ecollege/updateStudyRecordNew.jsp";
        String device = android.os.Build.BRAND + android.os.Build.MODEL
                + android.os.Build.DEVICE;
        ArrayMap<String, Object> para = new ArrayMap<>();
        para.put("appId", ConstantManager.appId);
        para.put("Lesson", studyRecord.getLesson());
        para.put("LessonId", studyRecord.getId());
        para.put("BeginTime", ParameterUrl.encode(studyRecord.getStartTime()));
        para.put("EndTime", ParameterUrl.encode(studyRecord.getEndTime()));
        para.put("EndFlg", studyRecord.getFlag());
        para.put("uid", uid);
        para.put("Device", ParameterUrl.encode(device));
        para.put("DeviceId", Utils.getMAC());
        para.put("TestNumber", ConfigManager.getInstance().getStudyMode());
        para.put("platform", "android");
        para.put("sign", MD5.getMD5ofStr(uid + studyRecord.getStartTime() + DateFormat.formatYear(Calendar.getInstance().getTime())));
        url = ParameterUrl.setRequestParameter(originalUrl, para);
    }

    @Override
    public BaseApiEntity<String> parseJsonImpl(JSONObject jsonObject) {
        BaseApiEntity<String> apiEntity = new BaseApiEntity<>();
        if (jsonObject.getIntValue("result") == 1) {
            apiEntity.setState(BaseApiEntity.SUCCESS);
            if (jsonObject.getIntValue("jifen") == 0) {
                apiEntity.setMessage("no");
            } else {
                apiEntity.setMessage("add");
            }
        } else {
            apiEntity.setState(BaseApiEntity.FAIL);
        }
        return apiEntity;
    }
}
