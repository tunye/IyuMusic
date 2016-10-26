package com.iyuba.music.entity.mainpanel;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/2.
 */
public class AnnouncerOp extends BaseEntityOp {
    public static final String TABLE_NAME = "announcer";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMG = "img";
    public static final String UID = "uid";

    public AnnouncerOp() {
        super();
    }

    public void saveData(Announcer announcer) {
        getDatabase();
        StringBuilder StringBuilder = new StringBuilder();
        StringBuilder.append("insert or replace into ").append(TABLE_NAME).append(" (").append(ID)
                .append(",").append(NAME).append(",").append(IMG).append(",").append(UID).append(") values(?,?,?,?)");
        db.execSQL(StringBuilder.toString(), new Object[]{announcer.getId(), announcer.getName()
                , announcer.getImgUrl(), announcer.getUid()});
        db.close();
    }

    public void saveData(ArrayList<Announcer> announcers) {
        getDatabase();
        if (announcers != null && announcers.size() != 0) {
            int size = announcers.size();
            Announcer announcer;
            StringBuilder StringBuilder;
            db.beginTransaction();
            try {
                for (int i = 0; i < size; i++) {
                    StringBuilder = new StringBuilder();
                    announcer = announcers.get(i);
                    StringBuilder.append("insert or replace into ").append(TABLE_NAME).append(" (").append(ID)
                            .append(",").append(NAME).append(",").append(IMG).append(",").append(UID).append(") values(?,?,?,?)");
                    db.execSQL(StringBuilder.toString(), new Object[]{announcer.getId(), announcer.getName()
                            , announcer.getImgUrl(), announcer.getUid()});
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

    public ArrayList<Announcer> findAll() {
        getDatabase();
        Cursor cursor = db.rawQuery("select " + ID + "," + NAME + "," + IMG + "," + UID
                + " from " + TABLE_NAME, new String[]{});
        Announcer announcer;
        ArrayList<Announcer> announcers = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            announcer = new Announcer();
            announcer.setId(cursor.getInt(0));
            announcer.setName(cursor.getString(1));
            announcer.setImgUrl(cursor.getString(2));
            announcer.setUid(cursor.getString(3));
            announcers.add(announcer);
        }
        cursor.close();
        db.close();
        return announcers;
    }

    public Announcer findByName(String name) {
        getDatabase();
        Cursor cursor = db.rawQuery("select " + ID + "," + NAME + "," + IMG + "," + UID
                + " from " + TABLE_NAME + " where " + NAME + " =?", new String[]{name});
        Announcer announcer = new Announcer();
        if (cursor.moveToNext()) {
            announcer.setId(cursor.getInt(0));
            announcer.setName(cursor.getString(1));
            announcer.setImgUrl(cursor.getString(2));
            announcer.setUid(cursor.getString(3));
        } else {

        }
        cursor.close();
        db.close();
        return announcer;
    }

    public Announcer findById(String id) {
        getDatabase();
        Cursor cursor = db.rawQuery("select " + ID + "," + NAME + "," + IMG + "," + UID
                + " from " + TABLE_NAME + " where " + ID + " =?", new String[]{id});
        Announcer announcer = new Announcer();
        if (cursor.moveToNext()) {
            announcer.setId(cursor.getInt(0));
            announcer.setName(cursor.getString(1));
            announcer.setImgUrl(cursor.getString(2));
            announcer.setUid(cursor.getString(3));
        } else {

        }
        cursor.close();
        db.close();
        return announcer;
    }
}
