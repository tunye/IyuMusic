package com.iyuba.music.entity;

import android.database.sqlite.SQLiteDatabase;

import com.iyuba.music.sqlite.ImportDatabase;

/**
 * Created by 10202 on 2015/11/18.
 */
public class BaseEntityOp {
    protected SQLiteDatabase db;

    protected void getDatabase() {
        db = ImportDatabase.getInstance().getWritableDatabase();
    }
}
