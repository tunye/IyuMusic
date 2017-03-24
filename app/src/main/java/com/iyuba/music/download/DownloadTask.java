/*
 * 文件名 
 * 包含类名列表
 * 版本信息，版本号
 * 创建日期
 * 版权声明
 */
package com.iyuba.music.download;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

/**
 * 类名
 *
 * @author 作者 <br/>
 *         实现的主要功能。 创建日期 修改者，修改日期，修改内容。
 */
public class DownloadTask {
    private String singPath, soundPath;
    private long downedFileLength = 0;
    private int id;
    private String app;
    private DownloadFile downloadFile;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    downloadFile.fileSize = Integer.parseInt(msg.obj.toString());
                    break;
                case 1:
                    downloadFile.downloadSize = Integer.parseInt(msg.obj.toString());
                    break;
                case 2:
                    downloadFile.downloadState = "finish";
                    break;
                case 3:
                    downloadFile.downloadState = "half_finish";
                    downloadFile.downloadSize = 0;
                    break;
                case 4:
                    downloadFile.downloadState = "fail";
                    break;
            }
            return false;
        }
    });

    public DownloadTask(Article article) {
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

    public void start() {
        Thread thread = new Thread(new Runnable() {
            public void run() {
                Looper.prepare();
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
                                            handler.sendEmptyMessage(2);
                                        }
                                    });
                        } else {
                            getWebOriginal(id);
                            downFile(soundPath, ConstantManager.getInstance().getMusicFolder() + File.separator + id + "s.mp3",
                                    new IOperationFinish() {
                                        @Override
                                        public void finish() {
                                            downedFileLength = 0;
                                            handler.sendEmptyMessage(2);
                                        }
                                    });
                        }
                        Looper.loop();
                    } else {
                        getWebOriginal(id);
                        downFile(soundPath, ConstantManager.getInstance().getMusicFolder() + File.separator + id + "s.mp3", new IOperationFinish() {
                            @Override
                            public void finish() {
                                downedFileLength = 0;
                                handler.sendEmptyMessage(3);
                                getWebLrc(id);
                                downFile(singPath, ConstantManager.getInstance().getMusicFolder() + File.separator + id + ".mp3",
                                        new IOperationFinish() {

                                            @Override
                                            public void finish() {
                                                downedFileLength = 0;
                                                handler.sendEmptyMessage(2);
                                            }
                                        });
                            }
                        });
                        Looper.loop();
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
                            handler.sendEmptyMessage(2);
                        }
                    });
                    Looper.loop();
                }
            }
        });
        thread.start();
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
                handler.sendEmptyMessage(4);
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
                handler.sendEmptyMessage(4);
                return;
            }
            try {
                InputStream inputStream = connection.getInputStream();
                FileOutputStream outputStream = new FileOutputStream(file);
                long fileLength = connection.getContentLength();
                byte[] buffer = new byte[1024 * 2];
                int length;
                Message message = new Message();
                message.what = 0;
                message.obj = fileLength;
                handler.sendMessage(message);
                while (downedFileLength < fileLength) {
                    length = inputStream.read(buffer);
                    downedFileLength += length;
                    outputStream.write(buffer, 0, length);
                    message = new Message();
                    message.what = 1;
                    message.obj = downedFileLength;
                    handler.sendMessageDelayed(message, 500);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
                connection.disconnect();
                reNameFile(path + ".tmp", path);
                finish.finish();
            } catch (IOException e) {
                handler.sendEmptyMessage(4);
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
}