package com.iyuba.music.entity.user;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/23.
 */
public class HistoryLoginOp extends BaseEntityOp {
    public static final String TABLE_NAME = "history_login";
    public static final String USERID = "userid";
    public static final String USERNAME = "username";
    public static final String USERPWD = "userpwd";
    public static final String LOGINTIME = "logintime";

    public HistoryLoginOp() {
        super();
    }

    /**
     * 插入数据
     */
    public void saveData(HistoryLogin historyLogin) {
        getDatabase();
        db.execSQL("insert or replace into " + TABLE_NAME + " (" + USERID + "," + USERNAME + ","
                + USERPWD + "," + LOGINTIME + ") values(?,?,?,?)", new Object[]{historyLogin.getUserid(),
                historyLogin.getUserName(), historyLogin.getUserPwd(), historyLogin.getLoginTime()});
        db.close();
    }

    /**
     * 选择数据
     */
    public ArrayList<HistoryLogin> selectData() {
        getDatabase();
        Cursor cursor = db.rawQuery(
                "select " + USERID + "," + USERNAME + "," + USERPWD + "," + LOGINTIME + " from "
                        + TABLE_NAME + " order by " + LOGINTIME + " DESC", new String[]{});
        ArrayList<HistoryLogin> historyLogins = new ArrayList<>();
        HistoryLogin historyLogin;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            historyLogin = new HistoryLogin();
            historyLogin.setUserid(cursor.getInt(0));
            historyLogin.setUserName(cursor.getString(1));
            historyLogin.setUserPwd(cursor.getString(2));
            historyLogin.setLoginTime(cursor.getString(3));
            historyLogins.add(historyLogin);
        }
        cursor.close();
        db.close();
        return historyLogins;
    }

    public void deleteData(String username) {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + USERNAME + "=?",
                new String[]{username});
        db.close();
    }
}
