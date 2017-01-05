package com.iyuba.music.entity.article;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;
import com.iyuba.music.util.DateFormat;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/2.
 */
public class SearchHistoryOp extends BaseEntityOp {
    public static final String TABLE_NAME = "searchhistory";
    public static final String ID = "id";
    public static final String CONTENT = "content";
    public static final String TIME = "time";

    public SearchHistoryOp() {
        super();
    }

    public void saveData(String content) {
        int result = hasData(content);
        if (result == 0) {
            getDatabase();
            StringBuilder StringBuilder = new StringBuilder();
            StringBuilder.append("insert into ").append(TABLE_NAME).append(" (").append(CONTENT)
                    .append(",").append(TIME).append(") values(?,?)");
            db.execSQL(StringBuilder.toString(), new Object[]{content, DateFormat.formatTime(Calendar.getInstance().getTime())});
            db.close();
        } else {
            updateData(result);
        }
    }

    public ArrayList<SearchHistory> findDataTop() {
        getDatabase();
        SearchHistory searchHistory;
        ArrayList<SearchHistory> searchHistories = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + CONTENT + "," + TIME + " from " + TABLE_NAME
                        + " order by time desc limit 0, 10",
                new String[]{});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            searchHistory = new SearchHistory();
            searchHistory.setId(cursor.getInt(0));
            searchHistory.setContent(cursor.getString(1));
            searchHistory.setTime(cursor.getString(2));
            searchHistories.add(searchHistory);
        }
        cursor.close();
        db.close();
        return searchHistories;
    }

    public ArrayList<SearchHistory> findDataByLike(String content) {
        getDatabase();
        SearchHistory searchHistory;
        ArrayList<SearchHistory> searchHistories = new ArrayList<>();
        Cursor cursor = db.rawQuery("select " + ID + "," + CONTENT + "," + TIME + " from " + TABLE_NAME
                        + " where " + CONTENT + " like ? order by time desc limit 0, 10",
                new String[]{content + "%"});
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            searchHistory = new SearchHistory();
            searchHistory.setId(cursor.getInt(0));
            searchHistory.setContent(cursor.getString(1));
            searchHistory.setTime(cursor.getString(2));
            searchHistories.add(searchHistory);
        }
        cursor.close();
        db.close();
        return searchHistories;
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
        db.execSQL("update " + TABLE_NAME + " set " + TIME + "='" + DateFormat.formatTime(Calendar.getInstance().getTime())
                        + "' where " + ID + "=? ",
                new String[]{String.valueOf(id),});
        db.close();
    }
}
