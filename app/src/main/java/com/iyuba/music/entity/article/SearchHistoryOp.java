package com.iyuba.music.entity.article;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.buaa.ct.core.bean.BaseEntityOp;
import com.iyuba.music.sqlite.ImportDatabase;
import com.iyuba.music.util.DateFormat;

import java.util.Calendar;
import java.util.List;

/**
 * Created by 10202 on 2015/12/2.
 */
public class SearchHistoryOp extends BaseEntityOp<SearchHistory> {
    public static final String TABLE_NAME = "searchhistory";
    public static final String ID = "id";
    public static final String CONTENT = "content";
    public static final String TIME = "time";

    public SearchHistoryOp() {
        super();
    }

    @Override
    public void getDatabase() {
        db = ImportDatabase.getInstance().getWritableDatabase();
    }

    public void saveData(String content) {
        int result = hasData(content);
        if (result == 0) {
            getDatabase();
            String StringBuilder = "insert into " + TABLE_NAME + " (" + CONTENT +
                    "," + TIME + ") values(?,?)";
            db.execSQL(StringBuilder, new Object[]{content, DateFormat.formatTime(Calendar.getInstance().getTime())});
            db.close();
        } else {
            updateData(result);
        }
    }

    @Override
    public String getSearchCondition() {
        return "select " + ID + "," + CONTENT + "," + TIME + " from " + TABLE_NAME;
    }

    @Override
    public SearchHistory fillData(@NonNull Cursor cursor) {
        SearchHistory searchHistory = new SearchHistory();
        searchHistory.setId(cursor.getInt(0));
        searchHistory.setContent(cursor.getString(1));
        searchHistory.setTime(cursor.getString(2));
        return searchHistory;
    }

    public List<SearchHistory> findDataTop() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " order by time desc limit 0, 10", new String[]{});
        return fillDatas(cursor);
    }

    public List<SearchHistory> findDataByLike(String content) {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " where " + CONTENT + " like ? order by time desc limit 0, 10", new String[]{content + "%"});
        return fillDatas(cursor);
    }

    public void deleteData(int id) {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + ID + "=? ",
                new String[]{String.valueOf(id),});
        db.close();
    }

    public void deleteAll() {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME, new String[]{,});
        db.close();
    }

    public int hasData(String content) {
        getDatabase();
        Cursor cursor = db.rawQuery("select " + ID + " from " + TABLE_NAME + " where " + CONTENT + " =?", new String[]{content});
        if (cursor.moveToNext()) {
            int result = cursor.getInt(0);
            cursor.close();
            db.close();
            return result;
        } else {
            cursor.close();
            db.close();
            return 0;
        }
    }

    private void updateData(int id) {
        getDatabase();
        db.execSQL("update " + TABLE_NAME + " set " + TIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime()) + "' where " + ID + "=? ",
                new String[]{String.valueOf(id),});
        db.close();
    }
}
