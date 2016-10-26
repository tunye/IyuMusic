package com.iyuba.music.file;


import com.iyuba.music.R;

/**
 * 文件浏览器相关类
 */
public class FileInfo {
    private String name;
    private String path;
    private long size;
    private String type;
    private String lastModify;
    private boolean isDirectory = false;
    private int fileCount = 0;
    private int folderCount = 0;

    public int getIconResourceId() {
        if (isDirectory) {
            return R.drawable.folder;
        } else if (type.equals("application/vnd.android.package-archive/*")) {
            return R.drawable.apk;
        } else if (type.equals("video/*")) {
            return R.drawable.video;
        } else if (type.equals("audio/*")) {
            return R.drawable.audio;
        } else if (type.equals("image/*")) {
            return R.drawable.image;
        } else if (type.equals("text/*")) {
            return R.drawable.txt;
        } else {
            return R.drawable.doc;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public void setIsDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }

    public int getFolderCount() {
        return folderCount;
    }

    public void setFolderCount(int folderCount) {
        this.folderCount = folderCount;
    }

    public String getLastModify() {
        return lastModify;
    }

    public void setLastModify(String lastModify) {
        this.lastModify = lastModify;
    }
}