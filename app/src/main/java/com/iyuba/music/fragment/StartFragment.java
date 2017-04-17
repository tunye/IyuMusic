package com.iyuba.music.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.view.View;

import com.iyuba.music.R;
import com.iyuba.music.entity.BaseApiEntity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.entity.article.LocalInfo;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.article.StudyRecord;
import com.iyuba.music.entity.article.StudyRecordOp;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.entity.word.PersonalWordOp;
import com.iyuba.music.entity.word.Word;
import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.apprequest.UpdateRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ThreadPoolUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

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
        currentVersion = ConfigManager.getInstance().loadInt("updateVersion", currentVersion);
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
                if (BaseApiEntity.isFail(apiEntity)) {
                    result.fail(0);
                } else {
                    result.success(apiEntity.getValue());
                }
            }
        });
    }

    public static void cleanLocalData() {
        cleanStudyRecord();
        if (AccountManager.getInstance().checkUserLogin()) {
            cleanWordDelData();
            cleanWordInsertData();
        }
    }

    private static void cleanStudyRecord() {
        StudyRecordOp studyRecordOp = new StudyRecordOp();
        if (studyRecordOp.hasData()) {
            ArrayList<StudyRecord> records = studyRecordOp.selectData();
            String userid = "0";
            if (AccountManager.getInstance().checkUserLogin()) {
                userid = AccountManager.getInstance().getUserId();
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
        final String userid = AccountManager.getInstance().getUserId();
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
        final String userid = AccountManager.getInstance().getUserId();
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
        sb.append("1.[适配7.0] 程序适配安卓7.0").append("\n");
        sb.append("2.[性能优化] 听歌页面加载速度提升").append("\n");
        sb.append("3.[实用功能] 增加线控和学习界面屏幕常亮");
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

    public static void resetDownLoadData() {
        final File packageFile = new File(ConstantManager.getInstance().getMusicFolder());
        if (packageFile.exists() && packageFile.list() != null) {
            ThreadPoolUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    LocalInfoOp lOp = new LocalInfoOp();
                    final ArticleOp articleOp = new ArticleOp();
                    int id;
                    LocalInfo temp;
                    StringBuilder stringBuilder = new StringBuilder();
                    for (String fileName : packageFile.list()) {
                        if (fileName.endsWith(".mp3")) {
                            fileName = fileName.split("\\.")[0];
                            if (!fileName.contains("-")) {
                                if (fileName.endsWith("s")) {
                                    id = Integer.parseInt(fileName.substring(0, fileName.length() - 1));
                                } else {
                                    id = Integer.parseInt(fileName);
                                }
                                temp = lOp.findDataById(ConstantManager.getInstance().getAppId(), id);
                                if (temp == null || temp.getId() == 0) {
                                    temp = new LocalInfo();
                                    temp.setId(id);
                                    temp.setApp(ConstantManager.getInstance().getAppId());
                                    temp.setDownload(1);
                                    temp.setDownTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                                    lOp.saveData(temp);
                                } else {
                                    lOp.updateDownload(id, ConstantManager.getInstance().getAppId(), 1);
                                }
                                stringBuilder.append(id).append(',');
                            }
                        } else {
                            new File(ConstantManager.getInstance().getMusicFolder() + File.separator + fileName).delete();
                        }
                    }
                    NewsesRequest.exeRequest(NewsesRequest.generateUrl(stringBuilder.toString()), new IProtocolResponse() {
                        @Override
                        public void onNetError(String msg) {

                        }

                        @Override
                        public void onServerError(String msg) {

                        }

                        @Override
                        public void response(Object object) {
                            BaseListEntity listEntity = (BaseListEntity) object;
                            ArrayList<Article> netData = (ArrayList<Article>) listEntity.getData();
                            for (Article temp : netData) {
                                temp.setApp(ConstantManager.getInstance().getAppId());
                            }
                            articleOp.saveData(netData);
                        }
                    });
                }
            });
        }
    }

    public static void checkTmpFile() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                File file = new File(ConstantManager.getInstance().getMusicFolder());
                if (file.exists()) {
                    for (File fileChild : file.listFiles()) {
                        if (fileChild.getName().contains(".tmp")) {
                            fileChild.delete();
                        }
                    }
                }
            }
        });
    }
}
