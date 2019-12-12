package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;

import com.buaa.ct.core.manager.RuntimeManager;
import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.buaa.ct.core.view.CustomToast;
import com.iyuba.music.activity.main.AnnouncerNewsList;
import com.iyuba.music.activity.main.ClassifyNewsList;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.ground.AppGroundActivity;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.service.PlayerService;
import com.iyuba.music.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 10202 on 2017/3/3.
 */

public class NullActivity {
    public static final String appScheme="iyumusic";
    public static final String webScheme="http";
    public static void exePushData(Context context, String url) {
        Uri uri;
        if (TextUtils.isEmpty(url)) {
            uri = Uri.parse("http://www.iyuba.cn");
        } else {
            uri = Uri.parse(url);
        }
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("pushIntent", true);
        if (appScheme.equals(uri.getScheme())) {
            String path = uri.getPath();
            switch (uri.getHost()) {
                case "zhubo":
                    switch (path.charAt(1)) {
                        case '1':                                         // 进入主播列表
                            intent.setClass(context, MainActivity.class);
                            context.startActivity(intent);
                            break;
                        case '2':                                         // 进入某主播个人主页
                            String broadcaster = path.substring(3);
                            SocialManager.getInstance().pushFriendId(broadcaster);
                            intent.setClass(context, PersonalHomeActivity.class);
                            context.startActivity(intent);
                            break;
                        case '3':                                         // 进入某主播歌单
                            broadcaster = path.substring(3);
                            intent.setClass(context, AnnouncerNewsList.class);
                            intent.putExtra(AnnouncerNewsList.ANNOUNCER, broadcaster);
                            context.startActivity(intent);
                            break;
                    }
                    break;
                case "dailyNew":                                          // 每日最新欧美单曲
                    intent.setClass(context, ClassifyNewsList.class);
                    intent.putExtra("classify", 100);
                    intent.putExtra("classifyName", "每日最新欧美单曲");
                    context.startActivity(intent);
                    break;
                case "song":                                              // 进入某首歌曲
                    Article tempArticle = new ArticleOp().findById(ConstantManager.appId, Integer.parseInt(path.substring(1)));
                    if (Utils.getMusicApplication().getPlayerService() == null) {
                        Utils.getMusicApplication().startService(new Intent(RuntimeManager.getInstance().getContext(), PlayerService.class));
                    }
                    if (tempArticle.getId() == 0) {
                        getAppointArticle(context, path.substring(1));
                    } else {
                        StudyManager.getInstance().setStartPlaying(true);
                        StudyManager.getInstance().setListFragmentPos("NullActivity");
                        StudyManager.getInstance().setLesson("music");
                        ArrayList<Article> articles = new ArrayList<>();
                        articles.add(tempArticle);
                        StudyManager.getInstance().setSourceArticleList(articles);
                        StudyManager.getInstance().setCurArticle(tempArticle);
                        intent.setClass(context, StudyActivity.class);
                        context.startActivity(intent);
                    }
                    break;
                case "wx":                                                // 关注公众号
                    intent.setClass(context, WxOfficialAccountActivity.class);
                    context.startActivity(intent);
                    break;
                case "appGround":                                         // 应用广场
                    intent.setClass(context, AppGroundActivity.class);
                    context.startActivity(intent);
                    break;
                case "localMusic":                                        // 本地播放器
                    intent.setClass(context, LocalMusicActivity.class);
                    context.startActivity(intent);
                    break;
                case "apk":                                               // 下载听歌
                    if (TextUtils.isEmpty(path) || path.length() < 2) {
                        return;
                    } else {
                        String packageName = path.substring(1);
                        if (!packageName.equals("com.iyuba.music") && checkApkExist(context, packageName)) {
                            intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
                            context.startActivity(intent);
                        } else {
                            try {
                                uri = Uri.parse("market://details?id=" + packageName);
                                intent = new Intent(Intent.ACTION_VIEW, uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } catch (Exception e) {
                                intent.setClass(context, WebViewActivity.class);
                                intent.putExtra("url", "http://www.wandoujia.com/apps/" + packageName);
                                intent.putExtra("title", "下载应用");
                                context.startActivity(intent);
                            }
                        }
                    }
                    break;
                default:
                    CustomToast.getInstance().showToast("无法解析的scheme");
                    intent.setClass(context, WelcomeActivity.class);
                    context.startActivity(intent);
                    break;
            }
        } else {
            CustomToast.getInstance().showToast("无法解析的scheme");
            intent.setClass(context, WelcomeActivity.class);
            context.startActivity(intent);
        }
    }

    private static void getAppointArticle(final Context context, String id) {
        RequestClient.requestAsync(new NewsesRequest(id), new SimpleRequestCallBack<BaseListEntity<List<Article>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Article>> listEntity) {
                List<Article> netData = listEntity.getData();
                for (Article temp : netData) {
                    temp.setApp(ConstantManager.appId);
                }
                new ArticleOp().saveData(netData);
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos("NullActivity");
                StudyManager.getInstance().setLesson("music");
                StudyManager.getInstance().setSourceArticleList(netData);
                StudyManager.getInstance().setCurArticle(netData.get(0));
                Intent intent = new Intent(context, StudyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("pushIntent", true);
                context.startActivity(intent);
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }

    public static boolean checkApkExist(Context context, String packageName) {
        if (TextUtils.isEmpty(packageName))
            return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
