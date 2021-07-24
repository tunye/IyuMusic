package com.iyuba.music.entity.word;

import android.database.Cursor;

import com.buaa.ct.core.bean.BaseEntityOp;
import com.iyuba.music.sqlite.ImportDatabase;

import java.util.ArrayList;

/**
 * Created by 10202 on 2015/12/2.
 */
public class ExampleSentenceOp extends BaseEntityOp {
    public static final String TABLE_NAME = "word_example";
    public static final String WORD = "word";
    public static final String ENGLISH = "english";
    public static final String CHINESE = "chinese";

    public ExampleSentenceOp() {
        super();
    }

    @Override
    public void getDatabase() {
        db = ImportDatabase.getInstance().getWritableDatabase();
    }

    public ArrayList<ExampleSentence> findData(String word) {
        getDatabase();
        Cursor cursor = db.rawQuery("select " + WORD + "," + ENGLISH + "," + CHINESE + " from "
                + TABLE_NAME + " WHERE " + WORD + "=?", new String[]{word});
        ExampleSentence exampleSentence;
        ArrayList<ExampleSentence> exampleSentences = new ArrayList<>();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            exampleSentence = new ExampleSentence();
            exampleSentence.setIndex(cursor.getPosition() + 1);
            exampleSentence.setSentence(cursor.getString(1));
            exampleSentence.setSentence_cn(cursor.getString(2));
            exampleSentences.add(exampleSentence);
        }
        cursor.close();
        db.close();
        return exampleSentences;
    }
}
