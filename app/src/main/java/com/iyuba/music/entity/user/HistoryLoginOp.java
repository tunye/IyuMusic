package com.iyuba.music.entity.user;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/11/23.
 */
public class HistoryLoginOp extends BaseEntityOp<HistoryLogin> {
    public static final String TABLE_NAME = "history_login";
    public static final String USERID = "userid";
    public static final String USERNAME = "username";
    public static final String USERPWD = "userpwd";
    public static final String LOGINTIME = "logintime";

    public HistoryLoginOp() {
        super();
    }


    @Override
    public void saveItemImpl(HistoryLogin historyLogin) {
        super.saveItemImpl(historyLogin);
        db.execSQL("insert or replace into " + TABLE_NAME + " (" + USERID + "," + USERNAME + ","
                + USERPWD + "," + LOGINTIME + ") values(?,?,?,?)", new Object[]{historyLogin.getUserid(),
                historyLogin.getUserName(), historyLogin.getUserPwd(), historyLogin.getLoginTime()});
    }

    @Override
    public String getSearchCondition() {
        return "select " + USERID + "," + USERNAME + "," + USERPWD + "," + LOGINTIME + " from " + TABLE_NAME;
    }

    @Override
    public HistoryLogin fillData(@NonNull Cursor cursor) {
        HistoryLogin historyLogin = new HistoryLogin();
        historyLogin.setUserid(cursor.getInt(0));
        historyLogin.setUserName(cursor.getString(1));
        historyLogin.setUserPwd(cursor.getString(2));
        historyLogin.setLoginTime(cursor.getString(3));
        return historyLogin;
    }

    /**
     * 选择数据
     */
    public ArrayList<HistoryLogin> selectData() {
        getDatabase();
        Cursor cursor = db.rawQuery(getSearchCondition() + " order by " + LOGINTIME + " DESC", new String[]{});
        return fillDatas(cursor);
    }

    public void deleteData(String username) {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + USERNAME + "=?",
                new String[]{username});
        db.close();
    }
}
