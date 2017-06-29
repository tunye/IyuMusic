package com.iyuba.music.entity.word;

import android.database.Cursor;
import android.text.TextUtils;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/2.
 */
public class PersonalWordOp extends BaseEntityOp {
    public static final String TABLE_NAME = "word";
    public static final String USER = "user";
    public static final String KEY = "key";
    public static final String LANG = "lang";
    public static final String AUDIOURL = "audiourl";
    public static final String PRON = "pron";
    public static final String DEF = "def";
    public static final String EXAMPLES = "examples";
    public static final String CREATEDATE = "createdate";
    public static final String VIEWCOUNT = "viewcount";
    public static final String ISDELETE = "isdelete";

    public PersonalWordOp() {
        super();
    }

    public void saveData(Word word) {
        getDatabase();
        String StringBuilder = "insert or replace into " + TABLE_NAME + " (" + USER +
                "," + KEY + "," + LANG + "," + AUDIOURL + "," +
                PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE +
                "," + ISDELETE + "," + VIEWCOUNT + ") values(?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(StringBuilder, new Object[]{word.getUser(), word.getWord(), "ENGLISH",
                word.getPronMP3(), word.getPron(), word.getDef(),
                word.getExampleSentence(), word.getCreateDate(), word.getIsdelete(), word.getViewCount()});
        db.close();
    }

    public void saveData(ArrayList<Word> words) {
        getDatabase();
        if (words != null && words.size() != 0) {
            int size = words.size();
            Word word;
            StringBuilder StringBuilder;
            db.beginTransaction();
            try {
                for (int i = 0; i < size; i++) {
                    StringBuilder = new StringBuilder();
                    word = words.get(i);
                    StringBuilder.append("insert or replace into ").append(TABLE_NAME).append(" (").append(USER)
                            .append(",").append(KEY).append(",").append(LANG).append(",").append(AUDIOURL).append(",")
                            .append(PRON).append(",").append(DEF).append(",").append(EXAMPLES).append(",").append(CREATEDATE)
                            .append(",").append(ISDELETE).append(",").append(VIEWCOUNT).append(") values(?,?,?,?,?,?,?,?,?,?)");
                    db.execSQL(StringBuilder.toString(), new Object[]{word.getUser(), word.getWord(), "ENGLISH",
                            word.getPronMP3(), word.getPron(), word.getDef(), word.getExampleSentence(),
                            word.getCreateDate(), word.getIsdelete(), word.getViewCount()});
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

    /**
     * @return
     */
    public ArrayList<Word> findDataByAll(String userid) {
        getDatabase();
        ArrayList<Word> words = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + USER + "," + KEY + "," + VIEWCOUNT + "," + AUDIOURL + ","
                + PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE + "," + ISDELETE + " from " +
                TABLE_NAME + " where user=? AND " + ISDELETE + "<?" + " ORDER BY " + USER + " DESC", new String[]{userid, "1"});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            word = new Word();
            word.setUser(cursor.getString(0));
            word.setWord(cursor.getString(1));
            word.setViewCount(cursor.getString(2));
            word.setPronMP3(cursor.getString(3));
            word.setPron(cursor.getString(4));
            word.setDef(cursor.getString(5));
            word.setExampleSentence(cursor.getString(6));
            word.setCreateDate(cursor.getString(7));
            word.setIsdelete(cursor.getString(8));
            words.add(word);
        }
        cursor.close();
        db.close();
        return words;
    }

    public Word findDataByName(String wordKey, String userid) {
        if (TextUtils.isEmpty(userid)) {
            userid = "0";
        }
        getDatabase();
        Cursor cursor = db.rawQuery("select " + USER + "," + KEY + "," + VIEWCOUNT + "," + AUDIOURL + ","
                + PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE + "," + ISDELETE + " from "
                + TABLE_NAME + " where key=? AND " + ISDELETE + "<? AND " + USER + "=?", new String[]{wordKey, "1", userid});
        if (cursor.moveToFirst()) {
            Word word = new Word();
            word.setUser(cursor.getString(0));
            word.setWord(cursor.getString(1));
            word.setViewCount(cursor.getString(2));
            word.setPronMP3(cursor.getString(3));
            word.setPron(cursor.getString(4));
            word.setDef(cursor.getString(5));
            word.setExampleSentence(cursor.getString(6));
            word.setCreateDate(cursor.getString(7));
            word.setIsdelete(cursor.getString(8));
            cursor.close();
            db.close();
            return word;
        } else {
            cursor.close();
            db.close();
            return null;
        }
    }

    /**
     * @return
     */
    public ArrayList<Word> findDataByDelete(String userid) {
        getDatabase();
        ArrayList<Word> words = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + USER + "," + KEY + "," + VIEWCOUNT + "," + AUDIOURL + ","
                + PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE + "," + ISDELETE + " from " + TABLE_NAME
                + " where user=? AND " + ISDELETE + "=?", new String[]{userid, "1"});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            word = new Word();
            word.setUser(cursor.getString(0));
            word.setWord(cursor.getString(1));
            word.setViewCount(cursor.getString(2));
            word.setPronMP3(cursor.getString(3));
            word.setPron(cursor.getString(4));
            word.setDef(cursor.getString(5));
            word.setExampleSentence(cursor.getString(6));
            word.setCreateDate(cursor.getString(7));
            word.setIsdelete(cursor.getString(8));
            words.add(word);
        }
        cursor.close();
        db.close();
        return words;
    }


    public ArrayList<Word> findDataByInsert(String userid) {
        getDatabase();
        ArrayList<Word> words = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + USER + "," + KEY + "," + VIEWCOUNT + "," + AUDIOURL + ","
                + PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE + "," + ISDELETE + " from " + TABLE_NAME
                + " where user=? AND " + ISDELETE + "=?", new String[]{userid, "-1"});
        Word word;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            word = new Word();
            word.setUser(cursor.getString(0));
            word.setWord(cursor.getString(1));
            word.setViewCount(cursor.getString(2));
            word.setPronMP3(cursor.getString(3));
            word.setPron(cursor.getString(4));
            word.setDef(cursor.getString(5));
            word.setExampleSentence(cursor.getString(6));
            word.setCreateDate(cursor.getString(7));
            word.setIsdelete(cursor.getString(8));
            words.add(word);
        }
        cursor.close();
        db.close();
        return words;
    }

    public void updateWord(String key, String sentence) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + VIEWCOUNT + " = " + VIEWCOUNT + "+1 , "
                + EXAMPLES + "=? where " + KEY + "=?", new String[]{sentence, key});
        db.close();
    }

    //单词添加同步操作
    public void insertWord(String key, String userid) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + ISDELETE + " = 0 where " + KEY + "=? and " + USER + "=?", new String[]{key, userid});
        db.close();
    }

    //列表添加同步操作
    public void insertWord(String userid) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + ISDELETE + " = 0 where " + USER + "='" + userid + "'");
        db.close();
    }

    //列表同步删除操作
    public void deleteWord(String userid) {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + USER + "='" + userid + "' AND " + ISDELETE + "='1'");
        db.close();
    }

    //单词同步删除操作
    public void deleteWord(String key, String userid) {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + USER + "=? AND " + KEY + "=?", new String[]{userid, key});
        db.close();
    }

    //单词删除操作
    public void tryToDeleteWord(String key, String userid) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + ISDELETE + " = 1 where " + KEY + "=? and " + USER + "=?", new String[]{key, userid});
        db.close();
    }
}
