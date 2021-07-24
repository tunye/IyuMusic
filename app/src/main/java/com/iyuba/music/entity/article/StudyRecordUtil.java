package com.iyuba.music.entity.article;

import android.text.TextUtils;

import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.StudyRecordRequest;
import com.iyuba.music.util.DateFormat;

import java.text.ParseException;
import java.util.Calendar;

public class StudyRecordUtil {
    public static void recordStop(String lesson, int flag) {
        recordStop(lesson, flag, true);
    }

    public static void recordStop(String lesson, int flag, boolean sendSync) {
        if (StudyManager.getInstance().getApp().equals("101") || TextUtils.isEmpty(StudyManager.getInstance().getStartTime())) {
            return;
        }
        StudyRecord studyRecord = new StudyRecord();
        studyRecord.setEndTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
        studyRecord.setFlag(flag);
        UserInfoOp userInfoOp = new UserInfoOp();
        String userid = AccountManager.getInstance().getUserId();
        studyRecord.setLesson(lesson);
        studyRecord.setId(StudyManager.getInstance().getCurArticle().getId());
        studyRecord.setStartTime(StudyManager.getInstance().getStartTime());
        int originalTime = userInfoOp.selectStudyTime(userid);
        int addTime = 0;
        try {
            addTime = (int) (DateFormat.parseTime(studyRecord.getEndTime()).getTime() / 1000 -
                    DateFormat.parseTime(studyRecord.getStartTime()).getTime() / 1000);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (addTime >= 600) {
            addTime = 600;
            studyRecord.setEndTime(studyRecord.getStartTime() + 600000);
        }
        userInfoOp.updataByStudyTime(userid, originalTime + addTime);
        new StudyRecordOp().saveData(studyRecord);
        if (sendSync) {
            sendToNet(studyRecord, userid, false);
        }
    }

    public static void sendToNet(final StudyRecord temp, String userid, final boolean mustDel) {
        RequestClient.requestAsync(new StudyRecordRequest(userid, temp), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> apiEntity) {
                if (BaseApiEntity.isSuccess(apiEntity)) {
                    new StudyRecordOp().deleteData(temp);
                    if (!apiEntity.getMessage().equals("no")) {
                        CustomToast.getInstance().showToast(R.string.study_listen_finish);
                    }
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfo) {
                if (mustDel && errorInfo.type == ErrorInfoWrapper.DATA_ERROR) {
                    new StudyRecordOp().deleteData(temp);
                }
            }
        });
    }
}
