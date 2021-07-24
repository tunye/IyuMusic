package com.iyuba.music.entity.mainpanel;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.buaa.ct.core.bean.BaseEntityOp;
import com.iyuba.music.sqlite.ImportDatabase;

import java.util.List;

/**
 * Created by 10202 on 2015/12/2.
 */
public class AnnouncerOp extends BaseEntityOp<Announcer> {
    public static final String TABLE_NAME = "announcer";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMG = "img";
    public static final String UID = "uid";

    public AnnouncerOp() {
        super();
    }

    @Override
    public void getDatabase() {
        db = ImportDatabase.getInstance().getWritableDatabase();
    }

    @Override
    public void saveItemImpl(Announcer announcer) {
        super.saveItemImpl(announcer);
        String StringBuilder = "insert or replace into " + TABLE_NAME + " (" + ID +
                "," + NAME + "," + IMG + "," + UID + ") values(?,?,?,?)";
        db.execSQL(StringBuilder, new Object[]{announcer.getId(), announcer.getName()
                , announcer.getImgUrl(), announcer.getUid()});
    }

    @Override
    public String getSearchCondition() {
        return "select " + ID + "," + NAME + "," + IMG + "," + UID + " from " + TABLE_NAME;
    }

    @Override
    public Announcer fillData(@NonNull Cursor cursor) {
        Announcer announcer = new Announcer();
        announcer.setId(cursor.getInt(0));
        announcer.setName(cursor.getString(1));
        announcer.setImgUrl(cursor.getString(2));
        announcer.setUid(cursor.getString(3));
        return announcer;
    }

    public List<Announcer> findAll() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition(), new String[]{});
        return fillDatas(cursor);
    }

    public Announcer findByName(String name) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + NAME + " =?", new String[]{name});
        Announcer announcer;
        if (cursor.moveToNext()) {
            announcer = fillData(cursor);
        } else {
            announcer = new Announcer();
        }
        cursor.close();
        db.close();
        return announcer;
    }

    public Announcer findById(String id) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + ID + " =?", new String[]{id});
        Announcer announcer;
        if (cursor.moveToNext()) {
            announcer = fillData(cursor);
        } else {
            announcer = new Announcer();
        }
        cursor.close();
        db.close();
        return announcer;
    }
}
