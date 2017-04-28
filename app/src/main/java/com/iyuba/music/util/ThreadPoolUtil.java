package com.iyuba.music.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 10202 on 2017-04-13.
 */

public class ThreadPoolUtil {
    private ExecutorService fixedExecutor;

    private ThreadPoolUtil() {
        fixedExecutor = Executors.newFixedThreadPool(3);
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
