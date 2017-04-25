/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.download;

import android.text.TextUtils;

import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.LrcMaker;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.entity.original.OriginalMaker;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.listener.IProtocolResponse;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.SettingConfigManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.request.newsrequest.OriginalRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 类名
 *
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class DownloadTask {
    private String singPath, soundPath;
    private int downedFileLength = 0;
    private int id;
    private String app;
    private DownloadFile downloadFile;
    private ExecutorService downloadExecutor;

    private static class InstanceHelper {
        private static DownloadTask instance = new DownloadTask();
    }

    public static DownloadTask getInstance() {
        return InstanceHelper.instance;
    }

    public DownloadTask() {
        downloadExecutor = Executors.newFixedThreadPool(3);
    }

    public void setTask(Article article) {
        this.app = article.getApp();
        this.singPath = DownloadService.getSongUrl(article.getApp(), article.getMusicUrl());
        if (article.getSimple() == 0) {
            if (app.equals("209")) {
                this.soundPath = DownloadService.getAnnouncerUrl(article.getId(), article.getSoundUrl());
            }
        } else if (app.equals("209")) {
            this.soundPath = "";
        }
        this.id = article.getId();
        int size = DownloadManager.getInstance().fileList.size();
        DownloadFile file;
        for (int i = 0; i < size; i++) {
            file = DownloadManager.getInstance().fileList.get(i);
            if (file.id == id) {
                downloadFile = file;
            }
        }
        start();
    }

    public static boolean checkFileExists(Article article) {
        if (article.getApp().equals("209") && article.getSimple() == 0) {
            if (SettingConfigManager.getInstance().getDownloadMode() == 0) {
                String path;
                if (SettingConfigManager.getInstance().getStudyMode() == 0) {
                    path = ConstantManager.getInstance().getMusicFolder() + File.separator + article.getId() + ".mp3";
                } else {
                    path = ConstantManager.getInstance().getMusicFolder() + File.separator + article.getId() + "s.mp3";
                }
                File file = new File(path);
                return file.exists();
            } else {
                String path = ConstantManager.getInstance().getMusicFolder() + File.separator + article.getId() + ".mp3";
                File file = new File(path);
                if (!file.exists()) {
                    return false;
                } else {
                    path = ConstantManager.getInstance().getMusicFolder() + File.separator + article.getId() + "s.mp3";
                    file = new File(path);
                    return file.exists();
                }
            }
        } else {
            String path;
            switch (article.getApp()) {
                case "209":
                    path = ConstantManager.getInstance().getMusicFolder() + File.separator + article.getId() + ".mp3";
                    break;
                case "229":
                case "217":
                case "213":
                    path = ConstantManager.getInstance().getMusicFolder() + File.separator + article.getApp() + "-" + article.getId() + ".mp4";
                    break;
                default:
                    path = ConstantManager.getInstance().getMusicFolder() + File.separator + article.getApp() + "-" + article.getId() + ".mp3";
                    break;
            }
            File file = new File(path);
            return file.exists();
        }
    }

    private void start() {
        downloadExecutor.execute(new Runnable() {
            public void run() {
                downedFileLength = 0;
                if (app.equals("209") && !TextUtils.isEmpty(soundPath)) {
                    if (SettingConfigManager.getInstance().getDownloadMode() == 0) {
                        if (SettingConfigManager.getInstance().getStudyMode() == 0) {
                            getWebLrc(id);
                            downFile(singPath, ConstantManager.getInstance().getMusicFolder() + File.separator + id + ".mp3",
                                    new IOperationFinish() {
                                        @Override
                                        public void finish() {
                                            downedFileLength = 0;
                                            downloadFile.downloadState = "finish";
                                        }
                                    });
                        } else {
                            getWebOriginal(id);
                            downFile(soundPath, ConstantManager.getInstance().getMusicFolder() + File.separator + id + "s.mp3",
                                    new IOperationFinish() {
                                        @Override
                                        public void finish() {
                                            downedFileLength = 0;
                                            downloadFile.downloadState = "finish";
                                        }
                                    });
                        }
                    } else {
                        getWebOriginal(id);
                        downFile(soundPath, ConstantManager.getInstance().getMusicFolder() + File.separator + id + "s.mp3", new IOperationFinish() {
                            @Override
                            public void finish() {
                                downedFileLength = 0;
                                downloadFile.downloadState = "half_finish";
                                downloadFile.downloadSize = 0;
                                getWebLrc(id);
                                downFile(singPath, ConstantManager.getInstance().getMusicFolder() + File.separator + id + ".mp3",
                                        new IOperationFinish() {

                                            @Override
                                            public void finish() {
                                                downedFileLength = 0;
                                                downloadFile.downloadState = "finish";
                                            }
                                        });
                            }
                        });
                    }
                } else {
                    String localPath;
                    switch (app) {
                        case "209":
                            localPath = ConstantManager.getInstance().getMusicFolder() + File.separator + id + ".mp3";
                            break;
                        case "229":
                        case "217":
                        case "213":
                            singPath = "http://staticvip.iyuba.com/video/voa/" + id + ".mp4";
                            localPath = ConstantManager.getInstance().getMusicFolder() + File.separator + app + "-" + id + ".mp4";
                            break;
                        default:
                            localPath = ConstantManager.getInstance().getMusicFolder() + File.separator + app + "-" + id + ".mp3";
                            break;
                    }
                    getWebLrc(id);
                    downFile(singPath, localPath, new IOperationFinish() {

                        @Override
                        public void finish() {
                            downedFileLength = 0;
                            downloadFile.downloadState = "finish";
                        }
                    });
                }
            }
        });
    }

    private void downFile(String fileUrl, String path, IOperationFinish finish) {
        File file = new File(path);
        if (file.exists()) {
            finish.finish();
        } else {
            File fileTemp = new File(ConstantManager.getInstance().getMusicFolder());
            if (!fileTemp.exists()) {
                fileTemp.mkdirs();
            }
            file = new File(path + ".tmp");
            try {
                file.createNewFile();
            } catch (IOException e) {
                downloadFile.downloadState = "fail";
                return;
            }
            URL url = null;
            HttpURLConnection connection = null;
            try {
                url = new URL(fileUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(3000);
                connection.setReadTimeout(10000);
            } catch (IOException e) {
                downloadFile.downloadState = "fail";
                return;
            }
            try {
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(file);
                int fileLength = connection.getContentLength();
                byte[] buffer = new byte[1024];
                int length;
                downloadFile.fileSize = fileLength;
                while (downedFileLength < fileLength) {
                    length = inputStream.read(buffer);
                    downedFileLength += length;
                    outputStream.write(buffer, 0, length);
                    downloadFile.downloadSize = downedFileLength;
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                connection.disconnect();
                reNameFile(path + ".tmp", path);
                finish.finish();
            } catch (IOException e) {
                downloadFile.downloadState = "fail";
                e.printStackTrace();
            }
        }
    }

    private boolean reNameFile(String oldFilePath, String newFilePath) {
        File source = new File(oldFilePath);
        File dest = new File(newFilePath);
        return source.renameTo(dest);
    }

    private void getWebLrc(final int id) {
        int type;
        switch (app) {
            case "215":
            case "221":
            case "231":
                type = 1;
                break;
            case "209":
                type = 2;
                break;
            default:
                type = 0;
                break;
        }
        LrcRequest.exeRequest(LrcRequest.generateUrl(id, type), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {

            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                LrcMaker.getInstance().makeOriginal(id, (ArrayList<Original>) listEntity.getData());
            }
        });
    }

    private void getWebOriginal(final int id) {
        OriginalRequest.exeRequest(OriginalRequest.generateUrl(id), new IProtocolResponse() {
            @Override
            public void onNetError(String msg) {
            }

            @Override
            public void onServerError(String msg) {

            }

            @Override
            public void response(Object object) {
                BaseListEntity listEntity = (BaseListEntity) object;
                OriginalMaker.getInstance().makeOriginal(id, (ArrayList<Original>) listEntity.getData());
            }
        });
    }

    public void shutDown() {
        downloadExecutor.shutdownNow();
    }
}