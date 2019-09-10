package com.iyuba.music.entity.word;

import android.database.Cursor;

import com.buaa.ct.core.bean.BaseEntityOp;

/**
 * Created by 10202 on 2015/12/2.
 */
public class SayingOp extends BaseEntityOp {
    public static final String TABLE_NAME = "sayings";
    public static final String ID = "id";
    public static final String ENGLISH = "english";
    public static final String CHINESE = "chinese";

    public SayingOp() {
        super();
    }

    public Saying findDataById(int id) {
        Saying sayings = new Saying();
        getDatabase();
        Cursor cursor = db.rawQuery("select " + ID + "," + ENGLISH + ", " + CHINESE + " from "
                + TABLE_NAME + " where " + ID + "=?", new String[]{String.valueOf(id)});
        if (cursor.moveToNext()) {
            sayings.setId(cursor.getInt(0));
            sayings.setEnglish(cursor.getString(1));
            sayings.setChinese(cursor.getString(2));
            cursor.close();
            db.close();
            return sayings;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }
}
