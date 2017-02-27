package com.iyuba.music.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;

import com.iyuba.music.R;
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

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by 10202 on 2016/2/13.
 */
public class StartFragment {

    public static void checkUpdate(Context context, final IOperationResult result) {
        int currentVersion = 0;
        try {
            currentVersion = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        currentVersion = ConfigManager.instance.loadInt("updateVersion", currentVersion);
        UpdateRequest.exeRequest(UpdateRequest.generateUrl(currentVersion), new IProtocolResponse() {
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
        if (AccountManager.INSTANCE.checkUserLogin()) {
            cleanWordDelData();
            cleanWordInsertData();
        }
    }

    private static void cleanStudyRecord() {
        StudyRecordOp studyRecordOp = new StudyRecordOp();
        if (studyRecordOp.hasData()) {
            ArrayList<StudyRecord> records = studyRecordOp.selectData();
            String userid = "0";
            if (AccountManager.INSTANCE.checkUserLogin()) {
                userid = AccountManager.INSTANCE.getUserId();
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
        final String userid = AccountManager.INSTANCE.getUserId();
        ArrayList<Word> delWords = personalWordOp.findDataByDelete(userid);
        if (delWords.size() != 0) {
            StringBuilder sb = new StringBuilder();
            for (Word temp : delWords) {
                sb.append(temp.getWord()).append(',');
            }
            DictUpdateRequest.exeRequest(DictUpdateRequest.generateUrl(userid, "delete", sb.toString()),
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
        final String userid = AccountManager.INSTANCE.getUserId();
        ArrayList<Word> insertWords = personalWordOp.findDataByInsert(userid);
        if (insertWords.size() != 0) {
            StringBuilder sb = new StringBuilder();
            for (Word temp : insertWords) {
                sb.append(temp.getWord()).append(',');
            }
            DictUpdateRequest.exeRequest(DictUpdateRequest.generateUrl(userid, "insert", sb.toString()),
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

    public static void showVersionFeature(Context context) {
        final MaterialDialog materialDialog = new MaterialDialog(context);
        materialDialog.setTitle(R.string.new_version_features);
        StringBuilder sb = new StringBuilder();
        sb.append("1.[软件维护] 修复烦人的bug").append("\n");
        sb.append("2.[性能优化] 提高软件使用的稳定性").append("\n");
        sb.append("3.[UI调整] 更美观的界面期待您的使用").append("\n");
        sb.append("4.[在线支付] 会员购买可以使用在线支付方式了！");
        materialDialog.setMessage(sb.toString());
        materialDialog.setPositiveButton(R.string.app_know, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.setCanceledOnTouchOutside(false);
        materialDialog.show();
    }
}
