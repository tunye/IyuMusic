package com.iyuba.music.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.iyuba.music.R;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.study.ReadActivity;
import com.iyuba.music.activity.study.RecommendSongActivity;
import com.iyuba.music.activity.study.StudySetActivity;
import com.iyuba.music.adapter.study.StudyMenuAdapter;
import com.iyuba.music.download.DownloadFile;
import com.iyuba.music.download.DownloadManager;
import com.iyuba.music.download.DownloadTask;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.entity.artical.LocalInfoOp;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.mainpanelrequest.AnnouncerRequest;
import com.iyuba.music.request.newsrequest.FavorRequest;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.widget.CustomToast;

import java.util.ArrayList;

import static com.iyuba.music.manager.RuntimeManager.getApplication;

/**
 * Created by 10202 on 2015/10/28.
 */
public class StudyMore {
    private View root;
    private Activity activity;
    private Context context;
    private boolean shown;
    private int[] menuDrawable;
    private String[] menuText;
    private StudyMenuAdapter studyMenuAdapter;
    private Dialog dialog;
    private String app;

    public StudyMore(Activity activity) {
        this.context = activity;
        this.activity = activity;
        LayoutInflater vi = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = vi.inflate(R.layout.study_more, null);
        init();
    }

    private void init() {
        app = StudyManager.instance.getApp();
        GridView moreGrid = (GridView) root.findViewById(R.id.study_menu);
        if (app.equals("209")) {
            menuDrawable = new int[]{R.drawable.share, R.drawable.favor,
                    R.drawable.download, R.drawable.read,
                    R.drawable.recommend, R.drawable.chat,
                    R.drawable.night, R.drawable.play_set,};
            menuText = context.getResources().getStringArray(R.array.study_menu);
        } else {
            menuDrawable = new int[]{R.drawable.share, R.drawable.favor,
                    R.drawable.download, R.drawable.play_set,};
            menuText = context.getResources().getStringArray(R.array.study_menu_simple);
        }
        studyMenuAdapter = new StudyMenuAdapter(context);
        final LocalInfoOp localInfoOp = new LocalInfoOp();
        final Article curArticle = StudyManager.instance.getCurArticle();
        if (localInfoOp.findDataById(app, curArticle.getId()).getFavourite() == 1) {
            menuDrawable[1] = R.drawable.favor_true;
        } else {
            menuDrawable[1] = R.drawable.favor;
        }
        int downloadState = localInfoOp.findDataById(app, curArticle.getId()).getDownload();
        if (downloadState == 1) {
            menuDrawable[2] = R.drawable.down_true;
        } else if (downloadState == 2) {
            menuDrawable[2] = R.drawable.downloading;
        } else {
            menuDrawable[2] = R.drawable.download;
        }
        if (app.equals("209")) {
            if (SettingConfigManager.instance.isNight()) {
                menuDrawable[6] = R.drawable.night_true;
            } else {
                menuDrawable[6] = R.drawable.night;
            }
        }
        studyMenuAdapter.setDataSet(menuText, menuDrawable);
        moreGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                dismiss();
                Intent intent;
                switch (position) {
                    case 0:
                        ShareDialog shareDialog = new ShareDialog(activity, StudyManager.instance.getCurArticle());
                        shareDialog.show();
                        break;
                    case 1:
                        if (localInfoOp.findDataById(app, curArticle.getId()).getFavourite() == 1) {
                            FavorRequest.getInstance().exeRequest(FavorRequest.getInstance().generateUrl(AccountManager.instance.getUserId(), curArticle.getId(), "del"), new IProtocolResponse() {
                                @Override
                                public void onNetError(String msg) {

                                }

                                @Override
                                public void onServerError(String msg) {

                                }

                                @Override
                                public void response(Object object) {
                                    if (object.toString().equals("del")) {
                                        localInfoOp.updateFavor(curArticle.getId(), app, 0);
                                        CustomToast.INSTANCE.showToast(R.string.artical_favor_cancel);
                                        menuDrawable[1] = R.drawable.favor;
                                        studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                    }
                                }
                            });
                        } else {
                            FavorRequest.getInstance().exeRequest(FavorRequest.getInstance().generateUrl(AccountManager.instance.getUserId(), curArticle.getId(), "insert"), new IProtocolResponse() {
                                @Override
                                public void onNetError(String msg) {

                                }

                                @Override
                                public void onServerError(String msg) {

                                }

                                @Override
                                public void response(Object object) {
                                    if (object.toString().equals("insert")) {
                                        localInfoOp.updateFavor(curArticle.getId(), app, 1);
                                        CustomToast.INSTANCE.showToast(R.string.artical_favor);
                                        menuDrawable[1] = R.drawable.favor_true;
                                        studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                    }
                                }
                            });
                        }
                        break;
                    case 2:
                        int downloadState = localInfoOp.findDataById(app, curArticle.getId()).getDownload();
                        if (downloadState == 1) {
                            CustomToast.INSTANCE.showToast(R.string.artical_download_over);
                        } else if (downloadState == 0) {
                            localInfoOp.updateDownload(curArticle.getId(), app, 2);
                            menuDrawable[2] = R.drawable.downloading;
                            studyMenuAdapter.setDataSet(menuText, menuDrawable);
                            DownloadFile downloadFile = new DownloadFile();
                            downloadFile.id = curArticle.getId();
                            downloadFile.downloadState = "start";
                            DownloadManager.Instance().fileList.add(downloadFile);
                            new DownloadTask(curArticle).start();
                        } else {
                            CustomToast.INSTANCE.showToast(R.string.artical_downloading);
                        }
                        break;
                    case 3:
                        if (app.equals("209")) {
                            context.startActivity(new Intent(context, ReadActivity.class));
                        } else {
                            context.startActivity(new Intent(context, StudySetActivity.class));
                        }
                        break;
                    case 4:
                        context.startActivity(new Intent(context, RecommendSongActivity.class));
                        break;
                    case 5:
                        if (curArticle.getSimple() == 0) {
                            Announcer announcer = new AnnouncerOp().findById(StudyManager.instance.getCurArticle().getStar());
                            if (announcer == null || announcer.getId() == 0) {
                                getAnnounceList();
                            } else {
                                SocialManager.instance.pushFriendId(announcer.getUid());
                                intent = new Intent(context, PersonalHomeActivity.class);
                                intent.putExtra("needpop", true);
                                context.startActivity(intent);
                            }
                        } else {
                            SocialManager.instance.pushFriendId("928");
                            intent = new Intent(context, PersonalHomeActivity.class);
                            intent.putExtra("needpop", true);
                            context.startActivity(intent);
                        }
                        break;
                    case 6:
                        SettingConfigManager.instance.setNight(!SettingConfigManager.instance.isNight());
                        ChangePropery.updateNightMode(SettingConfigManager.instance.isNight());
                        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(new Intent("changeProperty"));
                        break;
                    case 7:
                        context.startActivity(new Intent(context, StudySetActivity.class));
                        break;
                    default:
                        break;
                }

            }
        });
        moreGrid.setAdapter(studyMenuAdapter);
        root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        dialog = new Dialog(context, root, true, 0);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    private void getAnnounceList() {
        AnnouncerRequest.getInstance().exeRequest(new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.INSTANCE.showToast(msg);
            }

            @Override
            public void response(Object object) {
                ArrayList<Announcer> announcerList = (ArrayList<Announcer>) ((BaseListEntity) object).getData();
                new AnnouncerOp().saveData(announcerList);
                for (Announcer announcer : announcerList) {
                    if (announcer.getName().equals(StudyManager.instance.getCurArticle().getStar())) {
                        SocialManager.instance.pushFriendId(announcer.getUid());
                        Intent intent = new Intent(context, PersonalHomeActivity.class);
                        intent.putExtra("needpop", true);
                        context.startActivity(intent);
                        break;
                    }
                }
            }
        });
    }

    public void show() {
        dialog.show(R.anim.bottom_in);
        shown = true;
    }

    public void dismiss() {
        dialog.dismiss(R.anim.bottom_out);
    }

    public boolean isShown() {
        return shown;
    }
}
