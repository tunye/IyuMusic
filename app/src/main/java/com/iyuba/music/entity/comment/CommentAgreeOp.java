package com.iyuba.music.entity.comment;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;


public class CommentAgreeOp extends BaseEntityOp {

    public static final String TABLE_NAME = "commentagree";
    public static final String UID = "uid";
    public static final String COMMENTID = "commentid";
    public static final String AGREE = "agree";

    public CommentAgreeOp() {
        super();
    }

    public synchronized int findDataByAll(String commentid, String uid) {
        getDatabase();
        Cursor cursor = db.rawQuery("select " + COMMENTID + " , " + UID + "," + AGREE + " from "
                + TABLE_NAME + " where " + COMMENTID + " = ? AND " + UID + " = ?", new String[]{commentid, uid});
        if (cursor != null && cursor.getCount() == 0) {
            cursor.close();
            db.close();
            return 0;
        } else {
            int temp = 0;
            cursor.moveToFirst();
            if (cursor.getString(2).equals("against")) {
                temp = 2;
            } else {
                temp = 1;
            }
            cursor.close();
            db.close();
            return temp;
        }
    }

    public synchronized void saveData(String commentid, String uid, String agree) {
        if (commentid != null && uid != null) {
            getDatabase();
            db.execSQL("insert or replace into " + TABLE_NAME + " (" + COMMENTID + "," + UID + ","
                    + AGREE + ") values(?,?,?)", new Object[]{commentid, uid, agree});
            db.close();
        }
    }
}
