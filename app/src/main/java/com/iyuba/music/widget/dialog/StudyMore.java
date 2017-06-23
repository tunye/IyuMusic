package com.iyuba.music.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.mainpanel.Announcer;
import com.iyuba.music.entity.mainpanel.AnnouncerOp;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.AccountManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.ChangePropertyBroadcast;
import com.iyuba.music.request.mainpanelrequest.AnnouncerRequest;
import com.iyuba.music.request.newsrequest.FavorRequest;
import com.iyuba.music.util.ChangePropery;
import com.iyuba.music.widget.CustomToast;

import java.util.ArrayList;

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
    private IyubaDialog iyubaDialog;
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
        app = StudyManager.getInstance().getApp();
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
        final Article curArticle = StudyManager.getInstance().getCurArticle();
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
            if (ConfigManager.getInstance().isNight()) {
                menuDrawable[6] = R.drawable.night_true;
            } else {
                menuDrawable[6] = R.drawable.night;
            }
        }
        studyMenuAdapter.setDataSet(menuText, menuDrawable);
        moreGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;
                switch (position) {
                    case 0:
                        dismiss();
                        ShareDialog shareDialog = new ShareDialog(activity, StudyManager.getInstance().getCurArticle());
                        shareDialog.show();
                        break;
                    case 1:
                        dismiss();
                        if (localInfoOp.findDataById(app, curArticle.getId()).getFavourite() == 1) {
                            FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), curArticle.getId(), "del"), new IProtocolResponse() {
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
                                        CustomToast.getInstance().showToast(R.string.article_favor_cancel);
                                        menuDrawable[1] = R.drawable.favor;
                                        studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                    }
                                }
                            });
                        } else {
                            FavorRequest.exeRequest(FavorRequest.generateUrl(AccountManager.getInstance().getUserId(), curArticle.getId(), "insert"), new IProtocolResponse() {
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
                                        CustomToast.getInstance().showToast(R.string.article_favor);
                                        menuDrawable[1] = R.drawable.favor_true;
                                        studyMenuAdapter.setDataSet(menuText, menuDrawable);
                                    }
                                }
                            });
                        }
                        break;
                    case 2:
                        dismiss();
                        int downloadState = localInfoOp.findDataById(app, curArticle.getId()).getDownload();
                        if (downloadState == 1) {
                            CustomToast.getInstance().showToast(R.string.article_download_over);
                        } else if (downloadState == 0) {
                            localInfoOp.updateDownload(curArticle.getId(), app, 2);
                            menuDrawable[2] = R.drawable.downloading;
                            studyMenuAdapter.setDataSet(menuText, menuDrawable);
                            DownloadFile downloadFile = new DownloadFile();
                            downloadFile.id = curArticle.getId();
                            downloadFile.downloadState = "start";
                            DownloadManager.getInstance().fileList.add(downloadFile);
                            new DownloadTask(curArticle).start();
                        } else {
                            CustomToast.getInstance().showToast(R.string.article_downloading);
                        }
                        break;
                    case 3:
                        dismiss();
                        if (app.equals("209")) {
                            context.startActivity(new Intent(context, ReadActivity.class));
                        } else {
                            context.startActivity(new Intent(context, StudySetActivity.class));
                        }
                        break;
                    case 4:
                        dismiss();
                        context.startActivity(new Intent(context, RecommendSongActivity.class));
                        break;
                    case 5:
                        dismiss();
                        if (AccountManager.getInstance().checkUserLogin()) {
                            goAnnouncerHomePage(curArticle);
                        } else {
                            CustomDialog.showLoginDialog(context, new IOperationFinish() {
                                @Override
                                public void finish() {
                                    goAnnouncerHomePage(curArticle);
                                }
                            });
                        }
                        break;
                    case 6:
                        ConfigManager.getInstance().setNight(!ConfigManager.getInstance().isNight());
                        ChangePropery.updateNightMode(ConfigManager.getInstance().isNight());
                        intent = new Intent(ChangePropertyBroadcast.FLAG);
                        intent.putExtra(ChangePropertyBroadcast.SOURCE, "StudyActivity.class");
                        context.sendBroadcast(intent);
                        break;
                    case 7:
                        dismiss();
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
        iyubaDialog = new IyubaDialog(context, root, true, 0);
        iyubaDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                shown = false;
            }
        });
    }

    private void goAnnouncerHomePage(Article curArticle) {
        Intent intent;
        if (curArticle.getSimple() == 0) {
            Announcer announcer = new AnnouncerOp().findById(StudyManager.getInstance().getCurArticle().getStar());
            if (announcer == null || announcer.getId() == 0) {
                getAnnounceList();
            } else {
                SocialManager.getInstance().pushFriendId(announcer.getUid());
                intent = new Intent(context, PersonalHomeActivity.class);
                intent.putExtra("needpop", true);
                context.startActivity(intent);
            }
        } else {
            SocialManager.getInstance().pushFriendId("928");
            intent = new Intent(context, PersonalHomeActivity.class);
            intent.putExtra("needpop", true);
            context.startActivity(intent);
        }
    }

    private void getAnnounceList() {
        AnnouncerRequest.exeRequest(new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void onServerError(String msg) {
                CustomToast.getInstance().showToast(msg);
            }

            @Override
            public void response(Object object) {
                ArrayList<Announcer> announcerList = (ArrayList<Announcer>) ((BaseListEntity) object).getData();
                new AnnouncerOp().saveData(announcerList);
                final Announcer item = new Announcer();
                item.setUid("-1");
                for (Announcer announcer : announcerList) {
                    if (announcer.getName().equals(StudyManager.getInstance().getCurArticle().getStar())) {
                        item.setUid(announcer.getUid());
                        break;
                    }
                }
                if (!item.getUid().equals("-1")) {
                    if (AccountManager.getInstance().checkUserLogin()) {
                        SocialManager.getInstance().pushFriendId(item.getUid());
                        Intent intent = new Intent(context, PersonalHomeActivity.class);
                        intent.putExtra("needpop", true);
                        context.startActivity(intent);
                    } else {
                        CustomDialog.showLoginDialog(context, new IOperationFinish() {
                            @Override
                            public void finish() {
                                SocialManager.getInstance().pushFriendId(item.getUid());
                                Intent intent = new Intent(context, PersonalHomeActivity.class);
                                intent.putExtra("needpop", true);
                                context.startActivity(intent);
                            }
                        });
                    }
                }
            }
        });
    }

    public void show() {
        iyubaDialog.showAnim(R.anim.bottom_in);
        shown = true;
    }

    public void dismiss() {
        iyubaDialog.dismissAnim(R.anim.bottom_out);
    }

    public boolean isShown() {
        return shown;
    }
}
