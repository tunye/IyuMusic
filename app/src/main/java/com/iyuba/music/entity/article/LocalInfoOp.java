package com.iyuba.music.entity.article;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;
import com.iyuba.music.util.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/2.
 */
public class LocalInfoOp extends BaseEntityOp {
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

    public void saveData(LocalInfo localInfo) {
        getDatabase();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("insert into ").append(TABLE_NAME).append(" (").append(ID)
                .append(",").append(FAVOURITE).append(",").append(DOWNLOAD).append(",").append(TIMES)
                .append(",").append(SYNCHRO).append(",").append(APP).append(",").append(FAVTIME).append(",")
                .append(DOWNTIME).append(",").append(SEETIME).append(") values(?,?,?,?,?,?,?,?,?)");
        db.execSQL(stringBuilder.toString(), new Object[]{localInfo.getId(), localInfo.getFavourite(),
                localInfo.getDownload(), localInfo.getTimes(), localInfo.getSynchro(), localInfo.getApp()
                , localInfo.getFavTime(), localInfo.getDownTime(), localInfo.getSeeTime()});
        db.close();
    }

    public void saveData(ArrayList<LocalInfo> localInfos) {
        getDatabase();
        if (localInfos != null && localInfos.size() != 0) {
            int size = localInfos.size();
            LocalInfo localInfo;
            StringBuilder StringBuilder;
            db.beginTransaction();
            try {
                for (int i = 0; i < size; i++) {
                    StringBuilder = new StringBuilder();
                    localInfo = localInfos.get(i);
                    StringBuilder.append("insert into ").append(TABLE_NAME).append(" (").append(ID)
                            .append(",").append(FAVOURITE).append(",").append(DOWNLOAD).append(",").append(TIMES)
                            .append(",").append(SYNCHRO).append(",").append(APP).append(",").append(FAVTIME).append(",")
                            .append(DOWNTIME).append(",").append(SEETIME).append(") values(?,?,?,?,?,?,?,?,?)");
                    db.execSQL(StringBuilder.toString(), new Object[]{localInfo.getId(), localInfo.getFavourite(),
                            localInfo.getDownload(), localInfo.getTimes(), localInfo.getSynchro(), localInfo.getApp()
                            , localInfo.getFavTime(), localInfo.getDownTime(), localInfo.getSeeTime()});
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

    public LocalInfo findDataById(String app, int id) {
        getDatabase();
        LocalInfo localInfo = new LocalInfo();
        Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                        + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                        " from " + TABLE_NAME + " where app=? and id = ?",
                new String[]{app, String.valueOf(id)});
        if (cursor.moveToNext()) {
            localInfo.setId(cursor.getInt(0));
            localInfo.setDownload(cursor.getInt(2));
            localInfo.setFavourite(cursor.getInt(1));
            localInfo.setTimes(cursor.getInt(3));
            localInfo.setSynchro(cursor.getInt(4));
            localInfo.setApp(cursor.getString(5));
            localInfo.setFavTime(cursor.getString(6));
            localInfo.setDownTime(cursor.getString(7));
            localInfo.setSeeTime(cursor.getString(8));
        } else {
        }
        cursor.close();
        db.close();
        return localInfo;
    }

    public ArrayList<LocalInfo> findDataByFavourite() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                        + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                        " from " + TABLE_NAME + " where " + FAVOURITE + "=1 order by " + FAVTIME + " desc limit  100",
                new String[]{});
        LocalInfo localInfo;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            localInfo = new LocalInfo();
            localInfo.setId(cursor.getInt(0));
            localInfo.setDownload(cursor.getInt(2));
            localInfo.setFavourite(cursor.getInt(1));
            localInfo.setTimes(cursor.getInt(3));
            localInfo.setSynchro(cursor.getInt(4));
            localInfo.setApp(cursor.getString(5));
            localInfo.setFavTime(cursor.getString(6));
            localInfo.setDownTime(cursor.getString(7));
            localInfo.setSeeTime(cursor.getString(8));
            localInfos.add(localInfo);
        }
        cursor.close();
        db.close();
        return localInfos;
    }

    public ArrayList<LocalInfo> findDataByDownload() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                        + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                        " from " + TABLE_NAME + " where " + DOWNLOAD + ">0 order by " + DOWNTIME + " desc",
                new String[]{});
        LocalInfo localInfo;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            localInfo = new LocalInfo();
            localInfo.setId(cursor.getInt(0));
            localInfo.setDownload(cursor.getInt(2));
            localInfo.setFavourite(cursor.getInt(1));
            localInfo.setTimes(cursor.getInt(3));
            localInfo.setSynchro(cursor.getInt(4));
            localInfo.setApp(cursor.getString(5));
            localInfo.setFavTime(cursor.getString(6));
            localInfo.setDownTime(cursor.getString(7));
            localInfo.setSeeTime(cursor.getString(8));
            localInfos.add(localInfo);
        }
        cursor.close();
        db.close();
        return localInfos;
    }

    public ArrayList<LocalInfo> findDataByListen() {
        getDatabase();
        ArrayList<LocalInfo> localInfos = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + FAVOURITE + "," + DOWNLOAD + "," + TIMES + ","
                        + SYNCHRO + "," + APP + "," + FAVTIME + "," + DOWNTIME + "," + SEETIME +
                        " from " + TABLE_NAME + " where " + TIMES + ">0 order by " + SEETIME + " desc limit  100",
                new String[]{});
        LocalInfo localInfo;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            localInfo = new LocalInfo();
            localInfo.setId(cursor.getInt(0));
            localInfo.setDownload(cursor.getInt(2));
            localInfo.setFavourite(cursor.getInt(1));
            localInfo.setTimes(cursor.getInt(3));
            localInfo.setSynchro(cursor.getInt(4));
            localInfo.setApp(cursor.getString(5));
            localInfo.setFavTime(cursor.getString(6));
            localInfo.setDownTime(cursor.getString(7));
            localInfo.setSeeTime(cursor.getString(8));
            localInfos.add(localInfo);
        }
        cursor.close();
        db.close();
        return localInfos;
    }

    public void updateSee(int id, String app) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIMES + "=" + TIMES + "+1" + ","
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
}
