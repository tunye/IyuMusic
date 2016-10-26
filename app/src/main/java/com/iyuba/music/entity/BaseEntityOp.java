package com.iyuba.music.entity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.iyuba.music.sqlite.ImportDatabase;

/**
 * Created by 10202 on 2015/11/18.
 */
public class BaseEntityOp {
    protected static ImportDatabase importDatabase;
    protected SQLiteDatabase db;

    protected BaseEntityOp() {
        importDatabase = ImportDatabase.getInstance();
    }

    protected void getDatabase() {
        db = importDatabase.getWritableDatabase();
    }

    public long getDataCount(String tableName) {
        getDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from " + tableName, null);
        cursor.moveToFirst();
        long count = cursor.getLong(0);
        cursor.close();
        db.close();
        return count;
    }
}
