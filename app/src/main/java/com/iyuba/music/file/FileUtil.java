package com.iyuba.music.file;

import android.content.Context;
import android.os.Environment;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;


/****
 * 文件浏览器相关类
 */
public class FileUtil {

    /**
     * 获取SD卡路径
     **/
    public static String getSDPath() {
        return Environment.getExternalStorageDirectory().toString();
    }

    /**
     * 获取文件信息
     **/
    public static FileInfo getFileInfo(File file) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setName(file.getName());
        fileInfo.setIsDirectory(file.isDirectory());
        fileInfo.setPath(file.getPath());
        fileInfo.setLastModify(DateFormat.getDateTimeInstance().format(new Date(file.lastModified())));
        if (!fileInfo.isDirectory()) {
            fileInfo.setType(FileUtil.getMIMEType(fileInfo.getPath()));
        }
        if (file.isDirectory()) {
        } else {
            fileInfo.setSize(getFileSize(file));
        }
        return fileInfo;
    }

    public static FileInfo getCalcFileInfo(File file) {
        FileInfo info = getFileInfo(file);
        fastCalcFileContent(info, file);
        return info;
    }

    /**  **/
    private static void fastCalcFileContent(FileInfo info, File f) {
        if (f.isFile()) {
            info.setSize(getFileSize(f));
        }
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (File tmp : files) {
                    if (tmp.isDirectory()) {
                        info.setFolderCount(info.getFolderCount() + 1);
                    } else if (tmp.isFile()) {
                        info.setFileCount(info.getFileCount() + 1);
                    }
                }
            }
        }
    }

    public static String getFormatFileSize(File f) {
        try {
            return formetFileSize(getFileSize(f));
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    public static String getFormatFolderSize(File f) {
        try {
            return formetFileSize(getFolderSize(f));
        } catch (Exception e) {

            e.printStackTrace();
        }
        return null;
    }

    /***
     * 获取文件大小
     ***/
    public static long getFileSize(File f) {
        return f.length();
    }

    /***
     * 获取文件夹大小
     ***/
    public static long getFolderSize(File f) {
        long size = 0;
        File flist[] = f.listFiles();
        if (flist != null) {
            for (int i = 0; i < flist.length; i++) {
                if (flist[i].isDirectory()) {
                    size = size + getFolderSize(flist[i]);
                } else {
                    size = size + flist[i].length();
                }
            }
        }
        return size;
    }

    /***
     * 转换文件大小单位(b/kb/mb/gb)
     ***/
    public static String formetFileSize(long fileS) {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS == 0) {
            fileSizeString = "0B";
        } else if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }


    /**
     * 拼接路径名和文件名
     **/
    public static String combinPath(String path, String fileName) {
        return path + (path.endsWith(File.separator) ? "" : File.separator)
                + fileName;
    }

    /**
     * 复制文件
     **/
    public static boolean copyFile(File src, File tar) {
        if (src.isFile()) {
            try {
                InputStream is = new FileInputStream(src);
                OutputStream op = new FileOutputStream(tar);
                BufferedInputStream bis = new BufferedInputStream(is);
                BufferedOutputStream bos = new BufferedOutputStream(op);
                byte[] bt = new byte[1024 * 8];
                int len = bis.read(bt);
                while (len != -1) {
                    bos.write(bt, 0, len);
                    len = bis.read(bt);
                }
                bis.close();
                bos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (src.isDirectory()) {
            File[] f = src.listFiles();
            tar.mkdir();
            for (int i = 0; i < f.length; i++) {
                copyFile(f[i].getAbsoluteFile(), new File(tar.getAbsoluteFile()
                        + File.separator + f[i].getName()));
            }
        }
        return true;
    }

    /**
     * 删除文件或文件夹
     **/
    public static void deleteFile(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (File file : files) {
                    deleteFile(file);
                }
            }
        }
        f.delete();
    }

    /**
     * 删除文件夹下的所有内容
     **/
    public static void clearFileDir(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0; i < files.length; ++i) {
                    deleteFile(files[i]);
                }
            }
            f.delete();
        } else {
            f.delete();
        }
    }

    /**
     * 获取文件MIME类型
     **/
    public static String getMIMEType(String name) {
        String type;
        String end = name.substring(name.lastIndexOf(".") + 1, name.length())
                .toLowerCase();
        switch (end) {
            case "apk":
                return "application/vnd.android.package-archive";
            case "mp4":
            case "avi":
            case "3gp":
            case "rmvb":
            case "mkv":
            case "flv":
            case "f4v":
            case "swf":
                type = "video";
                break;
            case "m4a":
            case "mp3":
            case "aac":
            case "amr":
            case "ogg":
            case "wav":
            case "wma":
                type = "audio";
                break;
            case "jpg":
            case "gif":
            case "png":
            case "jpeg":
            case "bmp":
                type = "image";
                break;
            case "txt":
            case "log":
                type = "text";
                break;
            default:
                type = "*";
                break;
        }
        type += "/*";
        return type;
    }

    public static long getTotalCacheSize(Context context) {
        long cacheSize = getFolderSize(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            cacheSize += getFolderSize(context.getExternalCacheDir());
        }
        return cacheSize;
    }

    public static void clearAllCache(Context context) {
        deleteDir(context.getCacheDir());
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            deleteDir(context.getExternalCacheDir());
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir.delete();
    }
}
