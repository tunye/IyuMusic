package com.iyuba.music.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.article.StudyRecord;
import com.iyuba.music.entity.article.StudyRecordOp;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.entity.word.PersonalWordOp;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.request.apprequest.UpdateRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;

import java.util.ArrayList;

/**
 * Created by 10202 on 2016/2/13.
 */
public class StartFragment {

    public static void checkUpdate(Context context, final IOperationResult result) {
        final UpdateRequest updateRequest = UpdateRequest.getInstance();
        int currentVersion = 0;
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        currentVersion = ConfigManager.instance.loadInt("updateVersion", currentVersion);
        updateRequest.exeRequest(updateRequest.generateUrl(currentVersion), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                result.fail(-1);
            }

            @Override
            public void onServerError(String msg) {
                result.fail(-1);
            }

            @Override
            public void response(Object object) {
                BaseApiEntity apiEntity = (BaseApiEntity) object;
                if (apiEntity.getState().equals(BaseApiEntity.State.FAIL)) {
                    result.fail(0);
                } else {
                    result.success(apiEntity.getValue());
                }
            }
        });
    }

    public static void cleanLocalData() {
        cleanStudyRecord();
        if (AccountManager.instance.checkUserLogin()) {
            cleanWordDelData();
            cleanWordInsertData();
        }
    }

    private static void cleanStudyRecord() {
        StudyRecordOp studyRecordOp = new StudyRecordOp();
        if (studyRecordOp.hasData()) {
            ArrayList<StudyRecord> records = studyRecordOp.selectData();
            Message message;
            Bundle bundle;
            String userid = "0";
            if (AccountManager.instance.checkUserLogin()) {
                userid = AccountManager.instance.getUserId();
            }
            Handler handler = new Handler();
            for (StudyRecord record : records) {
                final StudyRecord exePos = record;
                final String user = userid;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StudyRecordUtil.sendToNet(exePos, user, true);
                    }
                }, 500);
            }
        }
    }

    private static void cleanWordDelData() {
        final PersonalWordOp personalWordOp = new PersonalWordOp();
        final String userid = AccountManager.instance.getUserId();
        ArrayList<Word> delWords = personalWordOp.findDataByDelete(userid);
        if (delWords.size() != 0) {
            StringBuilder sb = new StringBuilder();
            for (Word temp : delWords) {
                sb.append(temp.getWord()).append(',');
            }
            DictUpdateRequest.getInstance().exeRequest(DictUpdateRequest.getInstance().generateUrl
                            (userid, "delete", sb.toString()),
                    new IProtocolResponse() {
                        @Override
                        public void onNetError(String msg) {
                        }

                        @Override
                        public void onServerError(String msg) {
                        }

                        @Override
                        public void response(Object object) {
                            personalWordOp.deleteWord(userid);
                        }
                    });
        }
    }

    private static void cleanWordInsertData() {
        final PersonalWordOp personalWordOp = new PersonalWordOp();
        final String userid = AccountManager.instance.getUserId();
        ArrayList<Word> insertWords = personalWordOp.findDataByInsert(userid);
        if (insertWords.size() == 0) {
        } else {
            StringBuilder sb = new StringBuilder();
            for (Word temp : insertWords) {
                sb.append(temp.getWord()).append(',');
            }
            DictUpdateRequest.getInstance().exeRequest(DictUpdateRequest.getInstance().generateUrl
                            (userid, "insert", sb.toString()),
                    new IProtocolResponse() {
                        @Override
                        public void onNetError(String msg) {
                        }

                        @Override
                        public void onServerError(String msg) {
                        }

                        @Override
                        public void response(Object object) {
                            personalWordOp.insertWord(userid);
                        }
                    });
        }
    }
}
