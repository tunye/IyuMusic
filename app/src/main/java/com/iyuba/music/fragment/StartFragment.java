package com.iyuba.music.fragment;

import android.content.Context;
import android.content.pm.PackageManager;
import android.view.View;

import com.buaa.ct.core.listener.INoDoubleClick;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.util.SPUtils;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.buaa.ct.core.util.ThreadUtils;
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
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.apprequest.QunRequest;
import com.iyuba.music.request.apprequest.UpdateRequest;
import com.iyuba.music.request.discoverrequest.DictUpdateRequest;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.ParameterUrl;
import com.iyuba.music.widget.dialog.MyMaterialDialog;

import java.io.File;
import java.util.Calendar;
import java.util.List;

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
        currentVersion = SPUtils.loadInt(ConfigManager.getInstance().getPreferences(), "updateVersion", currentVersion);
        RequestClient.requestAsync(new UpdateRequest(currentVersion), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(BaseApiEntity<String> apiEntity) {
                if (BaseApiEntity.isFail(apiEntity)) {
                    result.fail(0);
                } else {
                    result.success(apiEntity.getValue());
                }
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {
                result.fail(-1);
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
            List<StudyRecord> records = studyRecordOp.selectData();
            String userid = AccountManager.getInstance().getUserId();
            for (StudyRecord record : records) {
                final StudyRecord exePos = record;
                final String user = userid;
                ThreadUtils.postOnUiThreadDelay(new Runnable() {
                    @Override
                    public void run() {
                        StudyRecordUtil.sendToNet(exePos, user, true);
                    }
                }, 200);
            }
        }
    }

    private static void cleanWordDelData() {
        final PersonalWordOp personalWordOp = new PersonalWordOp();
        final String userid = AccountManager.getInstance().getUserId();
        List<Word> delWords = personalWordOp.findDataByDelete(userid);
        if (delWords.size() != 0) {
            StringBuilder sb = new StringBuilder();
            for (Word temp : delWords) {
                sb.append(temp.getWord()).append(',');
            }
            RequestClient.requestAsync(new DictUpdateRequest(userid, "delete", sb.toString()), new SimpleRequestCallBack<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    personalWordOp.deleteWord(userid);
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {

                }
            });
        }
    }

    private static void cleanWordInsertData() {
        final PersonalWordOp personalWordOp = new PersonalWordOp();
        final String userid = AccountManager.getInstance().getUserId();
        List<Word> insertWords = personalWordOp.findDataByInsert(userid);
        if (insertWords.size() != 0) {
            StringBuilder sb = new StringBuilder();
            for (Word temp : insertWords) {
                sb.append(temp.getWord()).append(',');
            }
            RequestClient.requestAsync(new DictUpdateRequest(userid, "insert", sb.toString()), new SimpleRequestCallBack<Integer>() {
                @Override
                public void onSuccess(Integer integer) {
                    personalWordOp.insertWord(userid);
                }

                @Override
                public void onError(ErrorInfoWrapper errorInfoWrapper) {

                }
            });
        }
    }

    public static void showVersionFeature(final Context context) {
        String brand = "iyu360";
        if (!android.os.Build.MANUFACTURER.contains("360")) {
            brand = android.os.Build.MANUFACTURER;
        }
        RequestClient.requestAsync(new QunRequest(brand), new SimpleRequestCallBack<BaseApiEntity<String>>() {
            @Override
            public void onSuccess(final BaseApiEntity<String> apiEntity) {
                final MyMaterialDialog materialDialog = new MyMaterialDialog(context);
                materialDialog.setTitle(R.string.new_version_features);
                StringBuilder sb = new StringBuilder();
                sb.append("1.[功能体验] 新增临时账号功能，在无账号的时候也能体验").append("\n");
                sb.append("2.[燃爆MTV] 新增MTV模块，视听效果更出色").append("\n");
                sb.append("3.[每日打卡] good good study, day day up!");
                sb.append("\n\n爱语吧QQ用户群重磅来袭\n")
                        .append("在这里可以交流产品使用心得，互相切磋交流交朋友\n")
                        .append("用户群会不定期发放福利：全站会员、电子书、现金红包、积分\n")
                        .append("群号：").append(apiEntity.getData());
                materialDialog.setMessage(sb.toString());
                materialDialog.setNegativeButton(R.string.app_know, new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setPositiveButton(R.string.app_qun, new INoDoubleClick() {
                    @Override
                    public void activeClick(View view) {
                        ParameterUrl.joinQQGroup(context, apiEntity.getValue());
                        materialDialog.dismiss();
                    }
                });
                materialDialog.setCanceledOnTouchOutside(false);
                materialDialog.show();
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }

    public static void resetDownLoadData() {
        final File packageFile = new File(ConstantManager.musicFolder);
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
                                temp = lOp.findDataById(ConstantManager.appId, id);
                                if (temp == null || temp.getId() == 0) {
                                    temp = new LocalInfo();
                                    temp.setId(id);
                                    temp.setApp(ConstantManager.appId);
                                    temp.setDownload(1);
                                    temp.setDownTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                                    lOp.saveData(temp);
                                } else {
                                    lOp.updateDownload(id, ConstantManager.appId, 1);
                                }
                                stringBuilder.append(id).append(',');
                            }
                        } else {
                            new File(ConstantManager.musicFolder + File.separator + fileName).delete();
                        }
                    }
                    RequestClient.requestAsync(new NewsesRequest(stringBuilder.toString()), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
                        @Override
                        public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                            List<Article> netData = listEntity.getData();
                            for (Article temp : netData) {
                                temp.setApp(ConstantManager.appId);
                            }
                            articleOp.saveData(netData);
                        }

                        @Override
                        public void onError(ErrorInfoWrapper errorInfoWrapper) {

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
                File file = new File(ConstantManager.musicFolder);
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
