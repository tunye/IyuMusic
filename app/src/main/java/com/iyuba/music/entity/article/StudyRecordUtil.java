/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.entity.article;

import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.user.UserInfoOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.StudyRecordRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.CustomToast;

import java.text.ParseException;
import java.util.Calendar;

public class StudyRecordUtil {
    private static StudyRecordOp studyRecordOp = new StudyRecordOp();

    public static void recordStop(String lesson, int flag) {
        if (StudyManager.instance.getApp().equals("101")) {

        } else {
            StudyRecord studyRecord = new StudyRecord();
            studyRecord.setEndTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
            studyRecord.setFlag(flag);
            UserInfoOp userInfoOp = new UserInfoOp();
            String userid = "0";
            if (AccountManager.INSTANCE.checkUserLogin()) {
                userid = AccountManager.INSTANCE.getUserId();
            }
            studyRecord.setLesson(lesson);
            studyRecord.setId(StudyManager.instance.getCurArticle().getId());
            studyRecord.setStartTime(StudyManager.instance.getStartTime());
            int originalTime = userInfoOp.selectStudyTime(userid);
            int addTime = 0;
            try {
                addTime = (int) (DateFormat.parseTime(studyRecord.getEndTime()).getTime() / 1000 -
                        DateFormat.parseTime(studyRecord.getStartTime()).getTime() / 1000);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (addTime < 600) {
            } else {
                addTime = 600;
                studyRecord.setEndTime(studyRecord.getStartTime() + 600000);
            }
            userInfoOp.updataByStudyTime(userid, originalTime + addTime);
            sendToNet(studyRecord, userid, false);
        }
    }

    public static void sendToNet(final StudyRecord temp, String userid, final boolean mustDel) {
        studyRecordOp.saveData(temp);
        StudyRecordRequest.exeRequest(StudyRecordRequest.generateUrl(userid, temp), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
            }

            @Override
            public void onServerError(String msg) {
                if (mustDel) {
                    studyRecordOp.deleteData(temp);
                }
            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                if (apiEntity.getState().equals(BaseApiEntity.State.SUCCESS)) {
                    studyRecordOp.deleteData(temp);
                    if (apiEntity.getMessage().equals("no")) {

                    } else {
                        CustomToast.INSTANCE.showToast(R.string.study_listen_finish);
                    }
                } else {

                }
            }
        });
    }
}
