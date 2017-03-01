package com.iyuba.music.download;

import java.util.ArrayList;

public class DownloadManager {
    private static class SingleInstanceHelper {
        private static DownloadManager sInstance = new DownloadManager();
    }

    public ArrayList<DownloadFile> fileList;

    private DownloadManager() {
        fileList = new ArrayList<>();
    }

    public static DownloadManager getInstance() {
        return SingleInstanceHelper.sInstance;
    }
}
