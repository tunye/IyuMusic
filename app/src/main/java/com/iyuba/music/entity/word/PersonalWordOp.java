package com.iyuba.music.entity.word;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.buaa.ct.core.bean.BaseEntityOp;
import com.iyuba.music.sqlite.ImportDatabase;

import java.util.List;

/**
 * Created by 10202 on 2015/12/2.
 */
public class PersonalWordOp extends BaseEntityOp<Word> {
    public static final String TABLE_NAME = "word";
    public static final String USER = "user";
    public static final String COLUMN_KEY = "key";
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

    @Override
    public void getDatabase() {
        db = ImportDatabase.getInstance().getWritableDatabase();
    }

    @Override
    public void saveItemImpl(Word word) {
        super.saveItemImpl(word);
        String StringBuilder = "insert or replace into " + TABLE_NAME + " (" + USER +
                "," + COLUMN_KEY + "," + LANG + "," + AUDIOURL + "," +
                PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE +
                "," + ISDELETE + "," + VIEWCOUNT + ") values(?,?,?,?,?,?,?,?,?,?)";
        db.execSQL(StringBuilder, new Object[]{word.getUser(), word.getWord(), "ENGLISH",
                word.getPronMP3(), word.getPron(), word.getDef(),
                word.getExampleSentence(), word.getCreateDate(), word.getIsdelete(), word.getViewCount()});
    }

    @Override
    public String getSearchCondition() {
        return "select " + USER + "," + COLUMN_KEY + "," + VIEWCOUNT + "," + AUDIOURL + ","
                + PRON + "," + DEF + "," + EXAMPLES + "," + CREATEDATE + "," + ISDELETE + " from " +
                TABLE_NAME;
    }

    @Override
    public Word fillData(@NonNull Cursor cursor) {
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
        return word;
    }

    /**
     * @return
     */
    public List<Word> findDataByAll(String userid) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where user=? AND " + ISDELETE + "<?" + " ORDER BY " + USER + " DESC", new String[]{userid, "1"});
        return fillDatas(cursor);
    }

    public Word findDataByName(String wordKey, String userid) {
        if (TextUtils.isEmpty(userid)) {
            userid = "0";
        }
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where key=? AND " + ISDELETE + "<? AND " + USER + "=?", new String[]{wordKey, "1", userid});
        Word word = null;
        if (cursor.moveToFirst()) {
            word = fillData(cursor);
        } else {
            word = new Word();
        }
        cursor.close();
        db.close();
        return word;
    }

    /**
     * @return
     */
    public List<Word> findDataByDelete(String userid) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where user=? AND " + ISDELETE + "=?", new String[]{userid, "1"});
        return fillDatas(cursor);
    }


    public List<Word> findDataByInsert(String userid) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where user=? AND " + ISDELETE + "=?", new String[]{userid, "-1"});
        return fillDatas(cursor);
    }

    public void updateWord(String key, String sentence) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + VIEWCOUNT + " = " + VIEWCOUNT + "+1 , "
                + EXAMPLES + "=? where " + COLUMN_KEY + "=?", new String[]{sentence, key});
        db.close();
    }

    //单词添加同步操作
    public void insertWord(String key, String userid) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + ISDELETE + " = 0 where " + COLUMN_KEY + "=? and " + USER + "=?", new String[]{key, userid});
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
        db.execSQL("delete from " + TABLE_NAME + " where " + USER + "=? AND " + COLUMN_KEY + "=?", new String[]{userid, key});
        db.close();
    }

    //单词删除操作
    public void tryToDeleteWord(String key, String userid) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " SET " + ISDELETE + " = 1 where " + COLUMN_KEY + "=? and " + USER + "=?", new String[]{key, userid});
        db.close();
    }
}
