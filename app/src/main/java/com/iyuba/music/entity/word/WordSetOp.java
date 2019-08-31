package com.iyuba.music.entity.word;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/2.
 */
public class WordSetOp extends BaseEntityOp<Word> {
    public static final String TABLE_NAME = "wordset";
    public static final String ID = "id";
    public static final String KEY = "word";
    public static final String AUDIOURL = "audio";
    public static final String PRON = "pron";
    public static final String DEF = "def";
    public static final String VIEWCOUNT = "viewcount";

    public WordSetOp() {
        super();
    }

    @Override
    public String getSearchCondition() {
        return "select " + ID + "," + KEY + "," + AUDIOURL + "," + PRON + "," + DEF +
                "," + VIEWCOUNT + " from " + TABLE_NAME;
    }

    @Override
    public Word fillData(@NonNull Cursor cursor) {
        Word data = new Word();
        data.setWord(cursor.getString(1));
        data.setPronMP3(cursor.getString(2));
        data.setPron(cursor.getString(3));
        data.setDef(cursor.getString(4));
        return data;
    }

    public ArrayList<Word> findDataByFuzzy(String word) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " WHERE " + KEY + " LIKE '" + word
                + "%' limit 0,30;", new String[]{});
        return fillDatas(cursor);
    }

    /**
     * @return
     */
    public ArrayList<Word> findDataByView() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " WHERE " + VIEWCOUNT
                + " > ? limit 0,30", new String[]{"0"});
        return fillDatas(cursor);
    }

    public boolean updateWord(String key) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + VIEWCOUNT + " = " + VIEWCOUNT + "+1 where "
                + KEY + "=?", new String[]{key});
        db.close();
        return true;
    }

    public Word findDataByKey(String key) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " WHERE " + KEY + " = ?", new String[]{key});
        Word temp = null;
        if (cursor.moveToFirst()) {
            temp = fillData(cursor);
        } else {
            temp = new Word();
        }
        cursor.close();
        db.close();
        return temp;
    }
}
