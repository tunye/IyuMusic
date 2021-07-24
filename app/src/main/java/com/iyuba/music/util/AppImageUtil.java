package com.iyuba.music.util;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.buaa.ct.core.util.ImageUtil;
import com.buaa.ct.core.util.ThreadPoolUtil;
import com.bumptech.glide.Glide;
import com.iyuba.music.R;
import com.iyuba.music.manager.ConfigManager;

/**
 * Created by 102 on 2016/10/10.
 */

public class AppImageUtil {

    public static void loadAvatar(String userid, ImageView imageView) {
        String photoStamp = ConfigManager.getInstance().getUserPhotoTimeStamp();
        StringBuilder avatarUrl = new StringBuilder();
        avatarUrl.append("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=").append(userid);
        if (!TextUtils.isEmpty(photoStamp)) {
            avatarUrl.append('&').append(photoStamp);
        }
        ImageUtil.loadImage(avatarUrl.toString(), imageView, ImageUtil.getRequestOptions(R.drawable.default_photo, 0));
    }

    public static void loadAvatar(ImageView imageView, String userid, String size) {
        String photoStamp = ConfigManager.getInstance().getUserPhotoTimeStamp();
        StringBuilder avatarUrl = new StringBuilder();
        avatarUrl.append("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=").append(userid).append("&size=").append(size);
        if (!TextUtils.isEmpty(photoStamp)) {
            avatarUrl.append('&').append(photoStamp);
        }
        ImageUtil.loadImage(avatarUrl.toString(), imageView, ImageUtil.getRequestOptions(R.drawable.default_photo, 0));
    }

    public static void loadImage(String imageUrl, ImageView imageView) {
        ImageUtil.loadImage(imageUrl, imageView);
    }

    public static void loadImage(String imageUrl, ImageView imageView, int placeholderDrawableId) {
        ImageUtil.loadImage(imageUrl, imageView, ImageUtil.getRequestOptions(placeholderDrawableId, 0));
    }

    public static void loadImage(String imageUrl, ImageView imageView, int placeholderDrawableId, int roundCorners) {
        ImageUtil.loadImage(imageUrl, imageView, ImageUtil.getRequestOptions(placeholderDrawableId, roundCorners));
    }

    /**
     * 清空内存缓存
     * 内存缓存必须得在主线程
     *
     * @param activity Activity的Context
     */
    public static void clearMemoryCache(final Context activity) {
        Glide.get(activity).clearMemory();
    }

    /**
     * 清空磁盘缓存
     * 必须得在子线程运行
     *
     * @param context context
     */
    public static void clearDiskCache(final Context context) {
        ThreadPoolUtil.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Glide.get(context).clearDiskCache();
            }
        });
    }

    /**
     * 双清空缓存
     */
    public static void clearImageAllCache(Context context) {
        clearDiskCache(context);
        clearMemoryCache(context);
    }

    public static void destroy(Application application) {
        clearMemoryCache(application);
        Glide.with(application).onDestroy();
    }
}
