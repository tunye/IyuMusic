package com.iyuba.music.entity.word;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/2.
 */
public class WordSetOp extends BaseEntityOp {
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

    public ArrayList<Word> findDataByFuzzy(String word) {
        getDatabase();
        ArrayList<Word> words = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + KEY + "," + AUDIOURL + "," + PRON + "," + DEF +
                "," + VIEWCOUNT + " from " + TABLE_NAME + " WHERE " + KEY + " LIKE '" + word
                + "%' limit 0,30;", new String[]{});
        Word temp;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            temp = new Word();
            temp.setWord(cursor.getString(1));
            temp.setPronMP3(cursor.getString(2));
            temp.setPron(cursor.getString(3));
            temp.setDef(cursor.getString(4));
            words.add(temp);
        }
        cursor.close();
        db.close();
        return words;
    }

    /**
     * @return
     */
    public ArrayList<Word> findDataByView() {
        getDatabase();
        ArrayList<Word> words = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + KEY + "," + AUDIOURL + "," + PRON + ","
                + DEF + "," + VIEWCOUNT + " from " + TABLE_NAME + " WHERE " + VIEWCOUNT
                + " > ? limit 0,30", new String[]{"0"});
        Word temp;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            temp = new Word();
            temp.setWord(cursor.getString(1));
            temp.setPronMP3(cursor.getString(2));
            temp.setPron(cursor.getString(3));
            temp.setDef(cursor.getString(4));
            words.add(temp);
        }
        cursor.close();
        db.close();
        return words;
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
        Cursor cursor = db.rawQuery("select " + ID + "," + KEY + "," + AUDIOURL + "," + PRON + "," + DEF + ","
                + VIEWCOUNT + " from " + TABLE_NAME + " WHERE " + KEY + " = ?", new String[]{key});
        if (cursor.moveToFirst()) {
            Word temp = new Word();
            temp.setWord(cursor.getString(1));
            temp.setPronMP3(cursor.getString(2));
            temp.setPron(cursor.getString(3));
            temp.setDef(cursor.getString(4));
            cursor.close();
            db.close();
            return temp;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }
}
