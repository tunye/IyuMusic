package com.iyuba.music.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.iyuba.music.R;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author chentong
 */
public class ImportDatabase {
    private static final String DB_NAME = "music.sqlite"; // 保存的数据库文件名
    private static final int BUFFER_SIZE = 8192;
    private static ImportDatabase instance;
    private DBOpenHelper mdbhelper;
    private String DB_PATH;
    private int lastVersion, currentVersion;

    private ImportDatabase() {
        mdbhelper = new DBOpenHelper(RuntimeManager.getContext(), DB_NAME, null, 1);
        DB_PATH = RuntimeManager.getContext().getDatabasePath(DB_NAME).getAbsolutePath();
    }

    public static ImportDatabase getInstance() {
        if (instance == null) {
            instance = new ImportDatabase();
        }
        return instance;
    }

    public void setVersion(int lastVersion, int curVersion) {
        this.lastVersion = lastVersion;
        this.currentVersion = curVersion;
    }

    public SQLiteDatabase getWritableDatabase() {
        return mdbhelper.getWritableDatabase();
    }

    public void openDatabase() {
        lastVersion = ConfigManager.instance.loadInt("database_version");
        File database = new File(DB_PATH);
        if (currentVersion > lastVersion) {
            if (database.exists()) {
                database.delete();
            }
            ConfigManager.instance.putInt("database_version", currentVersion);
            new Thread(new Runnable() {
                public void run() {
                    loadDataBase();
                }
            }).start();
        }
    }

    private void loadDataBase() {
        try {
            BufferedInputStream bis = new BufferedInputStream(RuntimeManager.getContext()
                    .getResources().openRawResource(R.raw.music));
            File file = new File(DB_PATH);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            BufferedOutputStream bfos = new BufferedOutputStream(new FileOutputStream(DB_PATH));
            byte[] buffer = new byte[BUFFER_SIZE];
            int count;
            while ((count = bis.read(buffer)) > 0) {
                bfos.write(buffer, 0, count);
            }
            bis.close();
            bfos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
