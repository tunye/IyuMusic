package com.iyuba.music.util;

import java.util.Random;

/**
 * Created by chentong1 on 2017/6/1.
 */

public class RandomUtil {
    public Random random;

    private static class InstanceHelper {
        private static RandomUtil instance = new RandomUtil();
    }

    private RandomUtil() {
        random = new Random();
    }

    public static RandomUtil getInstance() {
        return InstanceHelper.instance;
    }

    public static int getRandomInt(int seed) {
        return InstanceHelper.instance.random.nextInt(seed);
    }
}
