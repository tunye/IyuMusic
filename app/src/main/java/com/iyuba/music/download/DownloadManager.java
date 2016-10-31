package com.iyuba.music.download;

import java.util.ArrayList;

public enum DownloadManager {
    sInstance;
    public ArrayList<DownloadFile> fileList;

    DownloadManager() {
        fileList = new ArrayList<>();
    }
}
