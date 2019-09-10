package com.iyuba.music.entity.article;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.buaa.ct.core.bean.BaseEntityOp;

import java.util.List;

/**
 * Created by 10202 on 2015/12/2.
 */
public class ArticleOp extends BaseEntityOp<Article> {
    public static final String TABLE_NAME = "news";
    public static final String ID = "id";
    public static final String TITLE = "title";
    public static final String TITLE_CN = "title_cn";
    public static final String DESCCN = "desccn";
    public static final String CATEGORY = "category";
    public static final String SINGER = "singer";
    public static final String ANNOUNCER = "announcer";
    public static final String SOUND = "sound";
    public static final String SONG = "song";
    public static final String PIC = "pic";
    public static final String CREATETIME = "createtime";
    public static final String READCOUNT = "readcount";
    public static final String APP = "app";

    public ArticleOp() {
        super();
    }

    public void saveItemImpl(Article article) {
        if (article.getApp().equals("209") && article.getSimple() == 1) {
            article.setSoundUrl("");
        }
        String s = "insert or replace into " + TABLE_NAME + " (" + ID +
                "," + TITLE + "," + TITLE_CN + "," + DESCCN + "," +
                CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND +
                "," + SONG + "," + PIC + "," + CREATETIME + "," +
                READCOUNT + "," + APP + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(s, new Object[]{article.getId(), article.getTitle(), article.getTitle_cn(), article.getContent(),
                article.getCategory(), article.getSinger(), article.getBroadcaster() + "," + article.getStar(), article.getSoundUrl(), article.getMusicUrl()
                , article.getPicUrl(), article.getTime(), article.getReadCount(), article.getApp()});
    }

    @Override
    public Article fillData(@NonNull Cursor cursor) {
        Article article = new Article();
        article.setId(Integer.parseInt(cursor.getString(0)));
        article.setTitle(cursor.getString(1));
        article.setTitle_cn(cursor.getString(2));
        article.setContent(cursor.getString(3));
        article.setCategory(cursor.getString(4));
        article.setSinger(cursor.getString(5));
        if (cursor.getString(6).contains(",")) {
            String[] broadcaster = cursor.getString(6).split(",");
            article.setBroadcaster(broadcaster[0]);
            article.setStar(broadcaster[1]);
        } else {
            article.setBroadcaster(cursor.getString(6));
        }
        article.setSoundUrl(cursor.getString(7));
        article.setMusicUrl(cursor.getString(8));
        article.setPicUrl(cursor.getString(9));
        article.setTime(cursor.getString(10));
        article.setReadCount(cursor.getString(11));
        article.setApp(cursor.getString(12));
        if (article.getApp().equals("209") && TextUtils.isEmpty(article.getSoundUrl())) {
            article.setSimple(1);
        } else {
            article.setSimple(0);
        }
        return article;
    }

    @Override
    public String getSearchCondition() {
        return "select " + ID + "," + TITLE + "," + TITLE_CN + "," + DESCCN + ","
                + CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND + "," + SONG + "," + PIC + ","
                + CREATETIME + "," + READCOUNT + "," + APP + " from " + TABLE_NAME;
    }

    public Article findById(String app, int id) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where app=? and id=? ORDER BY "
                + CREATETIME + " DESC", new String[]{app, String.valueOf(id)});
        Article article = null;
        if (cursor.moveToNext()) {
            article = fillData(cursor);
        } else {
            article = new Article();
        }
        cursor.close();
        db.close();
        return article;
    }

    public List<Article> findDataByAll(String app, int count, int offset) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where app=? and " + SOUND + "!=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{app, "", String.valueOf(count), String.valueOf(offset)});
        return fillDatas(cursor);
    }

    public List<Article> findDataByCategory(String app, int category, int count, int offset) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where app=? and category=? and " + SOUND + "!=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{app, String.valueOf(category), "",
                String.valueOf(count), String.valueOf(offset)});
        return fillDatas(cursor);
    }

    public List<Article> findDataByAnnouncer(int announcer, int count, int offset) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where app=? and announcer=? and " + SOUND + "!=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{"209", String.valueOf(announcer), "",
                String.valueOf(count), String.valueOf(offset)});
        return fillDatas(cursor);
    }

    public List<Article> findDataByMusic(int count, int offset) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where app=? and " + SOUND + "=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{"209", "",
                String.valueOf(count), String.valueOf(offset)});
        return fillDatas(cursor);
    }
}
