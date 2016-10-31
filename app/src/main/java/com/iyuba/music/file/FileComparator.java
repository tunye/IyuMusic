package com.iyuba.music.file;

import java.io.Serializable;
import java.util.Comparator;


/**
 * auther ct
 * <p/>
 * 文件浏览器相关类
 */
public class FileComparator implements Comparator<FileInfo>, Serializable {

    public int compare(FileInfo file1, FileInfo file2) {
        if (file1.isDirectory() && !file2.isDirectory()) {
            return -1000;
        } else if (!file1.isDirectory() && file2.isDirectory()) {
            return 1000;
        } else {
            return file1.getName().toLowerCase().compareTo(file2.getName().toLowerCase());
        }
    }
}