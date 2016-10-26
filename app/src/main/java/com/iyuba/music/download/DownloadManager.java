package com.iyuba.music.download;

import java.util.ArrayList;

public class DownloadManager {
    public static DownloadManager downloadManager;
    public ArrayList<DownloadFile> fileList = new ArrayList<>();

    public DownloadManager() {
    }

    public static synchronized DownloadManager Instance() {
        if (downloadManager == null) {
            downloadManager = new DownloadManager();
        }
        return downloadManager;
    }
}
