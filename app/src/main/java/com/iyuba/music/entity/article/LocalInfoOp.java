package com.iyuba.music.entity.article;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.buaa.ct.core.bean.BaseEntityOp;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.DateFormat;

import java.util.Calendar;
import java.util.List;

/**
 * Created by 10202 on 2015/12/2.
 */
public class LocalInfoOp extends BaseEntityOp<LocalInfo> {
    public static final String TABLE_NAME = "news_local";
    public static final String ID = "id";
    public static final String FAVOURITE = "favourite";
    public static final String DOWNLOAD = "download";
    public static final String TIMES = "times";
    public static final String SYNCHRO = "synchro";
    public static final String APP = "app";
    public static final String FAVTIME = "fav_time";
    public static final String DOWNTIME = "down_time";
    public static final String SEETIME = "see_time";

    public LocalInfoOp() {
        super();
    }

    @Override
    public void getDatabase() {
        db = ImportDatabase.getInstance().getWritableDatabase();
    }

    @Override
    public void saveItemImpl(LocalInfo localInfo) {
        super.saveItemImpl(localInfo);
        String stringBuilder = "insert into " + TABLE_NAME + " (" + ID +
                "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES +
                "," + SYNCHRO + "," + APP + "," + FAVTIME + "," +
                DOWNTIME + "," + SEETIME + ") values(?,?,?,?,?,?,?,?,?)";
        db.execSQL(stringBuilder, new Object[]{localInfo.getId(), localInfo.getFavourite(),
                localInfo.getDownload(), localInfo.getTimes(), localInfo.getSynchro(), localInfo.getApp()
                , localInfo.getFavTime(), localInfo.getDownTime(), localInfo.getSeeTime()});
    }

    public void changeDownloadToStop() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=3  where " + DOWNLOAD + "=2",
                new String[]{});
        db.close();
    }

    @Override
    public String getSearchCondition() {
        return "select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                " from " + TABLE_NAME;
    }

    public LocalInfo findDataById(String app, int id) {
        getDatabase();
        LocalInfo localInfo = null;
        Cursor cursor = db.rawQuery(getSearchCondition() + " where app=? and id = ?",
                new String[]{app, String.valueOf(id)});
        if (cursor.moveToNext()) {
            localInfo = fillData(cursor);
        } else {
            localInfo = new LocalInfo();
        }
        cursor.close();
        db.close();
        return localInfo;
    }

    public List<LocalInfo> findDataByFavourite() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + FAVOURITE + "=1 order by " + FAVTIME + " desc limit  100", new String[]{});
        return fillDatas(cursor);
    }

    public List<LocalInfo> findDataByDownloaded() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + DOWNLOAD + "=1 order by " + DOWNTIME + " desc", new String[]{});
        return fillDatas(cursor);
    }

    public List<LocalInfo> findDataByDownloading() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + DOWNLOAD + ">1 order by " + DOWNTIME + " desc", new String[]{});
        return fillDatas(cursor);
    }

    public List<LocalInfo> findDataByShouldContinue() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + DOWNLOAD + "=3 order by " + DOWNTIME + " desc", new String[]{});
        return fillDatas(cursor);
    }

    public List<LocalInfo> findDataByListen() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + TIMES + ">0 order by " + SEETIME + " desc limit  100", new String[]{});
        return fillDatas(cursor);
    }

    public void updateSee(int id, String app) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIMES + "=" + TIMES + "+1" + ","
                        + SEETIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(id), app});
        db.close();
    }

    public void deleteSee(int id, String app) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIMES + "=0,"
                        + SEETIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(id), app});
        db.close();
    }

    public void updateFavor(int id, String app, int state) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + FAVOURITE + "=? ,"
                        + FAVTIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(state), String.valueOf(id), app});
        db.close();
    }

    public void updateDownload(int id, String app, int state) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=? ,"
                        + DOWNTIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where id=? and app = ?",
                new String[]{String.valueOf(state), String.valueOf(id), app});
        db.close();
    }

    public void clearSee() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIMES + "=0,"
                + SEETIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                + "' where " + TIMES + ">0", new String[]{});
        db.close();
    }

    public void clearDownloading() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=0,"
                + DOWNTIME + "='' where " + DOWNLOAD + ">1", new String[]{});
        db.close();
    }

    public void clearDownloaded() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=0,"
                + DOWNTIME + "='' where " + DOWNLOAD + "=1", new String[]{});
        db.close();
    }

    public void clearAllDownload() {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + DOWNLOAD + "=0,"
                + DOWNTIME + "='' where " + DOWNLOAD + ">0", new String[]{});
        db.close();
    }

    @Override
    public LocalInfo fillData(@NonNull Cursor cursor) {
        LocalInfo localInfo = new LocalInfo();
        localInfo.setId(cursor.getInt(0));
        localInfo.setDownload(cursor.getInt(2));
        localInfo.setFavourite(cursor.getInt(1));
        localInfo.setTimes(cursor.getInt(3));
        localInfo.setSynchro(cursor.getInt(4));
        localInfo.setApp(cursor.getString(5));
        localInfo.setFavTime(cursor.getString(6));
        localInfo.setDownTime(cursor.getString(7));
        localInfo.setSeeTime(cursor.getString(8));
        return localInfo;
    }
}
