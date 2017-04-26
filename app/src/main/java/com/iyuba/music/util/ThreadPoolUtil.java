package com.iyuba.music.util;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by 10202 on 2017-04-13.
 */

public class ThreadPoolUtil {
    private ThreadPoolExecutor fixedExecutor;

    private ThreadPoolUtil() {
        fixedExecutor = new ThreadPoolExecutor(1, 3, 500, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        fixedExecutor.allowCoreThreadTimeOut(true);
    }

    private static class InstanceHelper {
        private static ThreadPoolUtil instance = new ThreadPoolUtil();
    }

    public static ThreadPoolUtil getInstance() {
        return InstanceHelper.instance;
    }

    public void execute(Runnable runnable) {
        fixedExecutor.execute(runnable);
    }

    public void shutdown() {
        fixedExecutor.shutdownNow();
    }
}
