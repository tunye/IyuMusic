/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.entity.artical;

import android.database.Cursor;

import com.iyuba.music.entity.BaseEntityOp;

import java.util.ArrayList;

/**
 * 类名
 *
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class StudyRecordOp extends BaseEntityOp {
    public static final String TABLE_NAME = "studyrecord";
    public static final String VOAID = "id";
    public static final String STARTTIME = "starttime";
    public static final String ENDTIME = "endtime";
    public static final String FLAG = "flag";
    public static final String LESSON = "lesson";

    public StudyRecordOp() {
        super();
    }

    /**
     * 插入数据
     */
    public void saveData(StudyRecord studyRecord) {
        getDatabase();
        StringBuilder StringBuilder = new StringBuilder();
        StringBuilder.append("insert or replace into ").append(TABLE_NAME).append(" (").append(VOAID)
                .append(",").append(STARTTIME).append(",").append(ENDTIME).append(",").append(FLAG)
                .append(",").append(LESSON).append(") values(?,?,?,?,?)");
        db.execSQL(StringBuilder.toString(), new Object[]{studyRecord.getId(), studyRecord.getStartTime(),
                studyRecord.getEndTime(), studyRecord.getFlag(), studyRecord.getLesson()});
        db.close();
    }

    /**
     * 选择数据
     */
    public void deleteData(StudyRecord studyRecord) {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME + " where " + VOAID + "=? and " + STARTTIME + "=?",
                new String[]{String.valueOf(studyRecord.getId()), studyRecord.getStartTime()});
        db.close();
    }

    /**
     * 选择数据
     */
    public void delete() {
        getDatabase();
        db.execSQL("delete from " + TABLE_NAME);
        db.close();
    }

    public ArrayList<StudyRecord> selectData() {
        getDatabase();
        ArrayList<StudyRecord> records = new ArrayList<StudyRecord>();
        Cursor cursor = db.rawQuery("select " + VOAID + "," + STARTTIME + "," + ENDTIME + ","
                + FLAG + "," + LESSON + " from " + TABLE_NAME, new String[]{});
        StudyRecord studyRecord;
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            studyRecord = new StudyRecord();
            studyRecord.setId(cursor.getInt(0));
            studyRecord.setStartTime(cursor.getString(1));
            studyRecord.setEndTime(cursor.getString(2));
            studyRecord.setFlag(cursor.getInt(3));
            studyRecord.setLesson(cursor.getString(4));
            records.add(studyRecord);
        }
        db.close();
        if (records.size() != 0) {
            return records;
        }
        return null;
    }

    public boolean hasData() {
        getDatabase();
        Cursor cursor = db.rawQuery(
                "select " + VOAID + " from " + TABLE_NAME, new String[]{});
        if (cursor.getCount() != 0) {
            cursor.close();
            db.close();
            return true;
        } else {
            cursor.close();
            db.close();
            return false;
        }
    }
}
