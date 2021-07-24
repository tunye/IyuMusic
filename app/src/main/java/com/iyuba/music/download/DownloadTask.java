/*
 * 文件名
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.download;

import android.text.TextUtils;

import com.buaa.ct.core.okhttp.ErrorInfoWrapper;
import com.buaa.ct.core.okhttp.RequestClient;
import com.buaa.ct.core.okhttp.SimpleRequestCallBack;
import com.iyuba.music.entity.BaseListEntity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.original.LrcMaker;
import com.iyuba.music.entity.original.Original;
import com.iyuba.music.entity.original.OriginalMaker;
import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.request.newsrequest.LrcRequest;
import com.iyuba.music.request.newsrequest.OriginalRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 类名
 *
 * @author 作者 <br/>
 * 实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class DownloadTask {
    private static ThreadPoolExecutor downloadExecutor;

    static {
        downloadExecutor = new ThreadPoolExecutor(1, 3, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        downloadExecutor.allowCoreThreadTimeOut(true);
    }

    private String singPath, soundPath;
    private int downedFileLength = 0;
    private int id;
    private String app;
    private DownloadFile downloadFile;

    public DownloadTask(Article article) {
        this.app = article.getApp();
        this.singPath = DownloadUtil.getSongUrl(article.getApp(), article.getMusicUrl());
        if (article.getSimple() == 0) {
            if (app.equals("209")) {
                this.soundPath = DownloadUtil.getAnnouncerUrl(article.getId(), article.getSoundUrl());
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
    }

    public static ExecutorService getDownloadExecutor() {
        return downloadExecutor;
    }

    public static boolean checkFileExists(Article article) {
        if (article.getApp().equals("209") && article.getSimple() == 0) {
            if (ConfigManager.getInstance().getDownloadMode() == 0) {
                String path;
                if (ConfigManager.getInstance().getStudyMode() == 0) {
                    path = ConstantManager.musicFolder + File.separator + article.getId() + ".mp3";
                } else {
                    path = ConstantManager.musicFolder + File.separator + article.getId() + "s.mp3";
                }
                File file = new File(path);
                return file.exists();
            } else {
                String path = ConstantManager.musicFolder + File.separator + article.getId() + ".mp3";
                File file = new File(path);
                if (!file.exists()) {
                    return false;
                } else {
                    path = ConstantManager.musicFolder + File.separator + article.getId() + "s.mp3";
                    file = new File(path);
                    return file.exists();
                }
            }
        } else {
            String path;
            switch (article.getApp()) {
                case "209":
                    path = ConstantManager.musicFolder + File.separator + article.getId() + ".mp3";
                    break;
                case "229":
                case "217":
                case "213":
                    path = ConstantManager.musicFolder + File.separator + article.getApp() + "-" + article.getId() + ".mp4";
                    break;
                default:
                    path = ConstantManager.musicFolder + File.separator + article.getApp() + "-" + article.getId() + ".mp3";
                    break;
            }
            File file = new File(path);
            return file.exists();
        }
    }

    public static void shutDown() {
        downloadExecutor.shutdownNow();
    }

    public void start() {
        if (downloadExecutor.isShutdown()) {
            downloadExecutor = new ThreadPoolExecutor(1, 3, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
            downloadExecutor.allowCoreThreadTimeOut(true);
        }
        downloadExecutor.execute(new Runnable() {
            public void run() {
                downedFileLength = 0;
                if (app.equals("209") && !TextUtils.isEmpty(soundPath)) {
                    if (ConfigManager.getInstance().getDownloadMode() == 0) {
                        if (ConfigManager.getInstance().getStudyMode() == 0) {
                            getWebLrc(id);
                            downFile(singPath, ConstantManager.musicFolder + File.separator + id + ".mp3",
                                    new IOperationFinish() {
                                        @Override
                                        public void finish() {
                                            downedFileLength = 0;
                                            downloadFile.downloadState = "finish";
                                        }
                                    });
                        } else {
                            getWebOriginal(id);
                            downFile(soundPath, ConstantManager.musicFolder + File.separator + id + "s.mp3",
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
                        downFile(soundPath, ConstantManager.musicFolder + File.separator + id + "s.mp3", new IOperationFinish() {
                            @Override
                            public void finish() {
                                downedFileLength = 0;
                                downloadFile.downloadState = "half_finish";
                                downloadFile.downloadSize = 0;
                                getWebLrc(id);
                                downFile(singPath, ConstantManager.musicFolder + File.separator + id + ".mp3",
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
                            localPath = ConstantManager.musicFolder + File.separator + id + ".mp3";
                            break;
                        case "229":
                        case "217":
                        case "213":
                            singPath = "http://staticvip.iyuba.cn/video/voa/" + id + ".mp4";
                            localPath = ConstantManager.musicFolder + File.separator + app + "-" + id + ".mp4";
                            break;
                        default:
                            localPath = ConstantManager.musicFolder + File.separator + app + "-" + id + ".mp3";
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
            File fileTemp = new File(ConstantManager.musicFolder);
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
            FileOutputStream outputStream = null;
            try {
                InputStream inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(file);
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
                connection.disconnect();
                reNameFile(path + ".tmp", path);
                finish.finish();
            } catch (IOException e) {
                downloadFile.downloadState = "fail";
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
        RequestClient.requestAsync(new LrcRequest(id, type), new SimpleRequestCallBack<BaseListEntity<List<Original>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Original>> listEntity) {
                LrcMaker.getInstance().makeOriginal(id, listEntity.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }

    private void getWebOriginal(final int id) {
        RequestClient.requestAsync(new OriginalRequest(id), new SimpleRequestCallBack<BaseListEntity<List<Original>>>() {
            @Override
            public void onSuccess(BaseListEntity<List<Original>> listEntity) {
                OriginalMaker.getInstance().makeOriginal(id, listEntity.getData());
            }

            @Override
            public void onError(ErrorInfoWrapper errorInfoWrapper) {

            }
        });
    }
}