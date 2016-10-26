package com.iyuba.music.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库更新表
 *
 * @author chentong
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "music.sqlite";

    public DBOpenHelper(Context context) {
        super(context, DB_NAME, null, 0);
    }

    public DBOpenHelper(Context context, String name, CursorFactory factory,
                        int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
