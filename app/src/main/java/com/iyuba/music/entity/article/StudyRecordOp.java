/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.entity.article;

import android.database.Cursor;

import com.buaa.ct.core.bean.BaseEntityOp;
import com.iyuba.music.sqlite.ImportDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class StudyRecordOp extends BaseEntityOp<StudyRecord> {
    public static final String TABLE_NAME = "studyrecord";
    public static final String VOAID = "id";
    public static final String STARTTIME = "starttime";
    public static final String ENDTIME = "endtime";
    public static final String FLAG = "flag";
    public static final String LESSON = "lesson";

    public StudyRecordOp() {
        super();
    }

    @Override
    public void getDatabase() {
        db = ImportDatabase.getInstance().getWritableDatabase();
    }

    /**
     * 插入数据
     */
    public void saveData(StudyRecord studyRecord) {
        getDatabase();

        db.close();
    }

    @Override
    public void saveItemImpl(StudyRecord studyRecord) {
        super.saveItemImpl(studyRecord);
        String StringBuilder = "insert or replace into " + TABLE_NAME + " (" + VOAID +
                "," + STARTTIME + "," + ENDTIME + "," + FLAG +
                "," + LESSON + ") values(?,?,?,?,?)";
        db.execSQL(StringBuilder, new Object[]{studyRecord.getId(), studyRecord.getStartTime(),
                studyRecord.getEndTime(), studyRecord.getFlag(), studyRecord.getLesson()});
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

    public List<StudyRecord> selectData() {
        getDatabase();
        List<StudyRecord> records = new ArrayList<>();
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
        cursor.close();
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
