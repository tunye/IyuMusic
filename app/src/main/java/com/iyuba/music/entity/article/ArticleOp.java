package com.iyuba.music.entity.article;

import android.database.Cursor;
import android.text.TextUtils;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/2.
 */
public class ArticleOp extends BaseEntityOp {
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

    public void saveData(Article article) {
        getDatabase();
        if (article.getApp().equals("209") && article.getSimple() == 1) {
            article.setSoundUrl("");
        }
        String StringBuilder = "insert or replace into " + TABLE_NAME + " (" + ID +
                "," + TITLE + "," + TITLE_CN + "," + DESCCN + "," +
                CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND +
                "," + SONG + "," + PIC + "," + CREATETIME + "," +
                READCOUNT + "," + APP + ") values(?,?,?,?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(StringBuilder, new Object[]{article.getId(), article.getTitle(), article.getTitle_cn(), article.getContent(),
                article.getCategory(), article.getSinger(), article.getBroadcaster() + "," + article.getStar(), article.getSoundUrl(), article.getMusicUrl()
                , article.getPicUrl(), article.getTime(), article.getReadCount(), article.getApp()});
        db.close();
    }

    public void saveData(ArrayList<Article> articles) {
        getDatabase();
        if (articles != null && articles.size() != 0) {
            int size = articles.size();
            Article article;
            StringBuilder StringBuilder;
            db.beginTransaction();
            try {
                for (int i = 0; i < size; i++) {
                    StringBuilder = new StringBuilder();
                    article = articles.get(i);
                    if (article.getApp().equals("209") && article.getSimple() == 1) {
                        article.setSoundUrl("");
                    }
                    StringBuilder.append("insert or replace into ").append(TABLE_NAME).append(" (").append(ID)
                            .append(",").append(TITLE).append(",").append(TITLE_CN).append(",").append(DESCCN).append(",")
                            .append(CATEGORY).append(",").append(SINGER).append(",").append(ANNOUNCER).append(",").append(SOUND)
                            .append(",").append(SONG).append(",").append(PIC).append(",").append(CREATETIME).append(",")
                            .append(READCOUNT).append(",").append(APP).append(") values(?,?,?,?,?,?,?,?,?,?,?,?,?)");
                    db.execSQL(StringBuilder.toString(), new Object[]{article.getId(), article.getTitle(), article.getTitle_cn(), article.getContent(),
                            article.getCategory(), article.getSinger(), article.getBroadcaster() + "," + article.getStar(), article.getSoundUrl(), article.getMusicUrl()
                            , article.getPicUrl(), article.getTime(), article.getReadCount(), article.getApp()});
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // 结束事务
                db.endTransaction();
                db.close();
            }
        }
    }

    public Article findById(String app, int id) {
        getDatabase();
        Cursor cursor = db.rawQuery("select " + ID + "," + TITLE + "," + TITLE_CN + "," + DESCCN + ","
                + CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND + "," + SONG + "," + PIC + ","
                + CREATETIME + "," + READCOUNT + "," + APP + " from " + TABLE_NAME + " where app=? and id=? ORDER BY "
                + CREATETIME + " DESC", new String[]{app, String.valueOf(id)});
        Article article = new Article();
        if (cursor.moveToNext()) {
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
        } else {

        }
        cursor.close();
        db.close();
        return article;
    }

    public ArrayList<Article> findDataByAll(String app, int count, int offset) {
        getDatabase();
        ArrayList<Article> articles = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + TITLE + "," + TITLE_CN + "," + DESCCN + ","
                + CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND + "," + SONG + "," + PIC + ","
                + CREATETIME + "," + READCOUNT + "," + APP + " from " + TABLE_NAME + " where app=? and " + SOUND + "!=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{app, "", String.valueOf(count), String.valueOf(offset)});
        Article article;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            article = new Article();
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
            article.setSimple(0);
            articles.add(article);
        }
        cursor.close();
        db.close();
        return articles;
    }

    public ArrayList<Article> findDataByCategory(String app, int category, int count, int offset) {
        getDatabase();
        ArrayList<Article> articles = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + TITLE + "," + TITLE_CN + "," + DESCCN + ","
                + CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND + "," + SONG + "," + PIC + ","
                + CREATETIME + "," + READCOUNT + "," + APP + " from " + TABLE_NAME + " where app=? and category=? and " + SOUND + "!=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{app, String.valueOf(category), "",
                String.valueOf(count), String.valueOf(offset)});
        Article article;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            article = new Article();
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
            article.setSimple(0);
            articles.add(article);
        }
        cursor.close();
        db.close();
        return articles;
    }

    public ArrayList<Article> findDataByAnnouncer(int announcer, int count, int offset) {
        getDatabase();
        ArrayList<Article> articles = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + TITLE + "," + TITLE_CN + "," + DESCCN + ","
                + CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND + "," + SONG + "," + PIC + ","
                + CREATETIME + "," + READCOUNT + "," + APP + " from " + TABLE_NAME + " where app=? and announcer=? and " + SOUND + "!=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{"209", String.valueOf(announcer), "",
                String.valueOf(count), String.valueOf(offset)});
        Article article;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            article = new Article();
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
            article.setSimple(0);
            articles.add(article);
        }
        cursor.close();
        db.close();
        return articles;
    }

    public ArrayList<Article> findDataByMusic(int count, int offset) {
        getDatabase();
        ArrayList<Article> articles = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + TITLE + "," + TITLE_CN + "," + DESCCN + ","
                + CATEGORY + "," + SINGER + "," + ANNOUNCER + "," + SOUND + "," + SONG + "," + PIC + ","
                + CREATETIME + "," + READCOUNT + "," + APP + " from " + TABLE_NAME + " where app=? and " + SOUND + "=? ORDER BY "
                + CREATETIME + " DESC  Limit ?,?", new String[]{"209", "",
                String.valueOf(count), String.valueOf(offset)});
        Article article;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            article = new Article();
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
            article.setSimple(1);
            articles.add(article);
        }
        cursor.close();
        db.close();
        return articles;
    }
}
