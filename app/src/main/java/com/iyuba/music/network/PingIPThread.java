package com.iyuba.music.network;

import com.iyuba.music.listener.IOperationResult;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 10202 on 2015/10/13.
 */
class PingIPThread extends Thread {
    private static final String url = "http://www.baidu.com";//百度
    private IOperationResult resultListner;

    public PingIPThread(IOperationResult resultListner) {
        this.resultListner = resultListner;
    }

    @Override
    public void run() {
        int intTimeout = 2;
        try {
            HttpURLConnection urlConnection = null;
            URL url = new URL(PingIPThread.url);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);
            urlConnection.setDoInput(true);
            urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0"
                    + " (compatible; MSIE 6.0; Windows 2000)");
            urlConnection.setRequestProperty("Content-type",
                    "text/html; charset=UTF-8");
            urlConnection.setConnectTimeout(1000 * intTimeout);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                resultListner.success("可以连接");
            } else {
                resultListner.success("不可以连接");
            }
        } catch (Exception e) {
            e.printStackTrace();
            resultListner.success("连接异常");
        }
    }
}
