package com.iyuba.music.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.iyuba.music.activity.main.AnnouncerNewsList;
import com.iyuba.music.activity.main.ClassifySongList;
import com.iyuba.music.activity.me.PersonalHomeActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.ArticleOp;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SocialManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.request.newsrequest.NewsesRequest;
import com.iyuba.music.util.TextAttr;
import com.iyuba.music.widget.CustomToast;

import java.util.ArrayList;

/**
 * Created by 10202 on 2017/3/3.
 */

public class NullActivity {
    public static void exePushData(Context context, String url) {
        Uri uri;
        if (TextUtils.isEmpty(url)) {
            uri = Uri.parse("http://www.iyuba.com");
        } else {
            uri = Uri.parse(url);
        }
        Intent intent;

        if (uri.getScheme().equals("iyumusic")) {
            String path = uri.getPath();
            switch (uri.getHost()) {
                case "broadcaster":
                    switch (path.charAt(1)) {
                        case '1':                                         // 进入主播列表
                            intent = new Intent(context, MainActivity.class);
                            intent.putExtra("pushIntent", "true");
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case '2':                                         // 进入某主播个人主页
                            String broadcaster = path.substring(3);
                            SocialManager.getInstance().pushFriendId(broadcaster);
                            intent = new Intent(context, PersonalHomeActivity.class);
                            intent.putExtra("needpop", true);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                        case '3':                                         // 进入某主播歌单
                            broadcaster = path.substring(3);
                            intent = new Intent(context, AnnouncerNewsList.class);
                            intent.putExtra("announcer", broadcaster);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                            break;
                    }
                    break;
                case "dailyNew":                                          // 每日最新欧美单曲
                    intent = new Intent(context, ClassifySongList.class);
                    intent.putExtra("classify", 100);
                    intent.putExtra("classifyName", "每日最新欧美单曲");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
                case "song":                                              // 进入某首歌曲
                    Article tempArticle = new ArticleOp().findById(ConstantManager.getInstance().getAppId(), Integer.parseInt(path.substring(1)));
                    if (tempArticle.getId() == 0) {
                        getAppointArticle(context, path.substring(1));
                    } else {
                        StudyManager.getInstance().setStartPlaying(true);
                        StudyManager.getInstance().setListFragmentPos("NullActivity.class");
                        StudyManager.getInstance().setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.getInstance().getAppName())));
                        ArrayList<Article> articles = new ArrayList<>();
                        articles.add(tempArticle);
                        StudyManager.getInstance().setSourceArticleList(articles);
                        StudyManager.getInstance().setCurArticle(tempArticle);
                        intent = new Intent(context, StudyActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    break;
                case "account":                                           // 关注公众号
                    intent = new Intent(context, WxOfficialAccountActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    break;
                case "downloadApk":                                       // 下载听歌
                    try {
                        uri = Uri.parse("market://details?id=com.iyuba.music");
                        intent = new Intent(Intent.ACTION_VIEW, uri);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    } catch (Exception e) {
                        intent = new Intent();
                        intent.setClass(context, WebViewActivity.class);
                        intent.putExtra("url", "http://www.wandoujia.com/apps/com.iyuba.music");
                        intent.putExtra("title", "下载应用");
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                    break;
            }
        } else {
            CustomToast.getInstance().showToast("无法解析的scheme");
            intent = new Intent(context, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private static void getAppointArticle(final Context context, String id) {
        NewsesRequest.exeRequest(NewsesRequest.generateUrl(id), new IProtocolResponse() {
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
                new ArticleOp().saveData(netData);
                StudyManager.getInstance().setStartPlaying(true);
                StudyManager.getInstance().setListFragmentPos("NullActivity.class");
                StudyManager.getInstance().setLesson(TextAttr.encode(TextAttr.encode(ConstantManager.getInstance().getAppName())));
                StudyManager.getInstance().setSourceArticleList(netData);
                StudyManager.getInstance().setCurArticle(netData.get(0));
                Intent intent = new Intent(context, StudyActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });
    }
}
