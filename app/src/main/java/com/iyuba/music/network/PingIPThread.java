package com.iyuba.music.network;

import com.iyuba.music.listener.IOperationResult;
import com.iyuba.music.util.ThreadPoolUtil;

import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 10202 on 2015/10/13.
 */
public class PingIPThread {
    private static final String url = "https://www.baidu.com";                          //百度
    private SoftReference<IOperationResult> resultListner;

    public PingIPThread(IOperationResult resultListener) {
        this.resultListner = new SoftReference<>(resultListener);
    }

    public void start() {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int intTimeout = 2;
                try {
                    URL url = new URL(PingIPThread.url);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("GET");
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestProperty("User-Agent", "Mozilla/4.0(compatible; MSIE 6.0; Windows 2000)");
                    urlConnection.setRequestProperty("Content-type", "text/html; charset=UTF-8");
                    urlConnection.setConnectTimeout(1000 * intTimeout);
                    urlConnection.connect();
                    if (urlConnection.getResponseCode() == 200) {
                        if (resultListner.get() != null) {
                            resultListner.get().success("可以连接");
                        }
                    } else {
                        if (resultListner.get() != null) {
                            resultListner.get().fail("不可以连接");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (resultListner.get() != null) {
                        resultListner.get().fail("连接异常");
                    }
                }
            }
        });
    }
}
