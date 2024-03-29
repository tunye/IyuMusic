package com.iyuba.music.download;

import com.buaa.ct.core.util.ThreadPoolUtil;
import com.iyuba.music.listener.IOperationFinish;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppUpdateThread {
    private long downedFileLength = 0;
    private DownloadFile downloadFile;

    public AppUpdateThread() {
        int size = DownloadManager.getInstance().fileList.size();
        DownloadFile file;
        for (int i = 0; i < size; i++) {
            file = DownloadManager.getInstance().fileList.get(i);
            if (file.id == -1) {
                downloadFile = file;
            }
        }
    }

    public void start() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                downedFileLength = 0;
                downFile(new IOperationFinish() {

                    @Override
                    public void finish() {
                        downedFileLength = 0;
                        downloadFile.downloadState = "finish";
                    }
                });
            }
        });
    }

    private void downFile(IOperationFinish downloadFinish) {
        String fileFolder = downloadFile.filePath;
        String fileFullPath = fileFolder + downloadFile.fileName + downloadFile.fileAppend;
        File fileTemp = new File(fileFullPath);
        if (fileTemp.exists()) {
            downloadFinish.finish();
        } else {
            File file = new File(fileFolder);
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(fileFullPath + ".tmp");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            FileOutputStream outputStream = null;
            try {
                URL url = new URL(downloadFile.downLoadAddress);
                HttpURLConnection connection = (HttpURLConnection) url
                        .openConnection();
                connection.setConnectTimeout(10000);
                connection.setReadTimeout(10000);
                InputStream inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(file);
                long fileLength = connection.getContentLength();
                byte[] buffer = new byte[1024 * 8];
                int length;
                downloadFile.fileSize = (int) fileLength;
                while (downedFileLength < fileLength) {
                    length = inputStream.read(buffer);
                    downedFileLength += length;
                    outputStream.write(buffer, 0, length);
                    downloadFile.downloadSize = (int) downedFileLength;
                }
                inputStream.close();
                outputStream.flush();
                connection.disconnect();
                reNameFile(fileFullPath + ".tmp", fileFullPath);
                downloadFinish.finish();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private boolean reNameFile(String oldFilePath, String newFilePath) {
        File source = new File(oldFilePath);
        File dest = new File(newFilePath);
        return source.renameTo(dest);
    }
}