package com.iyuba.music.sqlite;

import android.database.sqlite.SQLiteDatabase;

import com.iyuba.music.R;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.util.ThreadPoolUtil;

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
    private DBOpenHelper mDBHelper;
    private String dbPath;
    private int lastVersion, currentVersion;

    private ImportDatabase() {
        mDBHelper = new DBOpenHelper(RuntimeManager.getContext(), DB_NAME, null, 1);
        dbPath = RuntimeManager.getContext().getDatabasePath(DB_NAME).getAbsolutePath();
    }

    public static ImportDatabase getInstance() {
        return InstanceHelper.instance;
    }

    public void setVersion(int lastVersion, int curVersion) {
        this.lastVersion = lastVersion;
        this.currentVersion = curVersion;
    }

    public SQLiteDatabase getWritableDatabase() {
        return mDBHelper.getWritableDatabase();
    }

    public void closeDatabase() {
        mDBHelper.close();
    }

    public void openDatabase() {
        lastVersion = ConfigManager.getInstance().loadInt("database_version");
        File database = new File(dbPath);
        if (currentVersion > lastVersion) {
            if (database.exists()) {
                database.delete();
            }
            ConfigManager.getInstance().putInt("database_version", currentVersion);
            ThreadPoolUtil.getInstance().execute(new Runnable() {
                @Override
                public void run() {
                    loadDataBase();
                }
            });
        }
    }

    private void loadDataBase() {
        try {
            BufferedInputStream bis = new BufferedInputStream(RuntimeManager.getContext()
                    .getResources().openRawResource(R.raw.music));
            File file = new File(dbPath);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            BufferedOutputStream bfos = new BufferedOutputStream(new FileOutputStream(dbPath));
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

    private static class InstanceHelper {
        private static ImportDatabase instance = new ImportDatabase();
    }
}
