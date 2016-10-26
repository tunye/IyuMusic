package com.iyuba.music.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.iyuba.music.R;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author chentong
 */
public class ImportDatabase {
    private static final String DB_NAME = "music.sqlite"; // 保存的数据库文件名
    private static final int BUFFER_SIZE = 400000;
    private static ImportDatabase instance;
    private DBOpenHelper mdbhelper;
    private String PACKAGE_NAME;
    private String DB_PATH;
    private int lastVersion, currentVersion;

    public ImportDatabase() {
        mdbhelper = new DBOpenHelper(RuntimeManager.getContext(), DB_NAME, null, 1);
    }

    public static ImportDatabase getInstance() {
        if (instance == null) {
            instance = new ImportDatabase();
        }
        return instance;
    }

    public String getDBPath() {
        return DB_PATH + "/" + DB_NAME;
    }

    public void setPackageName(String packageName) {
        PACKAGE_NAME = packageName;
        DB_PATH = "/data" + Environment.getDataDirectory().getAbsolutePath()
                + "/" + PACKAGE_NAME + "/" + "databases";
    }

    public void setVersion(int lastVersion, int curVersion) {
        this.lastVersion = lastVersion;
        this.currentVersion = curVersion;
    }

    public SQLiteDatabase getWritableDatabase() {
        return mdbhelper.getWritableDatabase();
    }

    public void openDatabase(final Context context, final String dbFile) {
        lastVersion = ConfigManager.instance.loadInt("database_version");
        File database = new File(dbFile);
        if (currentVersion > lastVersion) {
            if (database.exists()) {
                database.delete();
            }
            Thread t = new Thread(new Runnable() {
                public void run() {
                    loadDataBase(context, dbFile);
                }
            });
            t.start();
            ConfigManager.instance.putInt("database_version", currentVersion);
        }
    }

    private void loadDataBase(Context context, String dbFile) {
        try {
            InputStream is = context.getResources().openRawResource(
                    R.raw.music);
            BufferedInputStream bis = new BufferedInputStream(is);
            if (!(new File(DB_PATH).exists())) {
                new File(DB_PATH).mkdir();
            }
            FileOutputStream fos = new FileOutputStream(dbFile);
            BufferedOutputStream bfos = new BufferedOutputStream(fos);
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
