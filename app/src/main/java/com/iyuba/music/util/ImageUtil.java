package com.iyuba.music.util;

import android.content.Context;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.iyuba.music.R;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.SettingConfigManager;

/**
 * Created by 102 on 2016/10/10.
 */

public class ImageUtil {
    public static void loadAvatar(String userid, ImageView imageView) {
        String photoStamp = SettingConfigManager.getInstance().getUserPhotoTimeStamp();
        StringBuilder avatarUrl = new StringBuilder();
        avatarUrl.append("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&size=big&uid=").append(userid);
        if (!TextUtils.isEmpty(photoStamp)) {
            avatarUrl.append('&').append(photoStamp);
        }
        loadImage(avatarUrl.toString(), imageView, R.drawable.default_photo);
    }

    public static void loadAvatar(ImageView imageView, String userid, String size) {
        String photoStamp = SettingConfigManager.getInstance().getUserPhotoTimeStamp();
        StringBuilder avatarUrl = new StringBuilder();
        avatarUrl.append("http://api.iyuba.com.cn/v2/api.iyuba?protocol=10005&uid=").append(userid).append("&size=").append(size);
        if (!TextUtils.isEmpty(photoStamp)) {
            avatarUrl.append('&').append(photoStamp);
        }
        loadImage(avatarUrl.toString(), imageView, R.drawable.default_photo);
    }

    public static void loadImage(String imageUrl, ImageView imageView) {
        loadImage(imageUrl, imageView, 0);
    }

    public static void loadImage(String imageUrl, ImageView imageView, int placeholderDrawableId) {
        if (TextUtils.isEmpty(imageUrl)) {
            imageView.setImageResource(placeholderDrawableId);
            return;
        }
        Context context;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            context = imageView.getContext();
        } else {
            context = RuntimeManager.getContext();
        }
        Glide.with(context).load(imageUrl).placeholder(placeholderDrawableId)
                .error(placeholderDrawableId).animate(R.anim.fade_in).centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE).into(imageView);
    }

    public static void loadImage(ImageView imageView, String imageUrl, int placeholderDrawableId, OnDrawableLoadListener listener) {
        loadImage(imageView, imageUrl, placeholderDrawableId, listener, true, true);
    }

    public static void loadImage(ImageView imageView, String imageUrl, int placeholderDrawableId,
                                 final OnDrawableLoadListener listener, boolean centerCrop, boolean cache) {
        if (imageView == null) {
            return;
        } else if (TextUtils.isEmpty(imageUrl)) {
            imageView.setImageResource(placeholderDrawableId);
            return;
        }
        Context context;
        if (Looper.myLooper() == Looper.getMainLooper()) {
            context = imageView.getContext();
        } else {
            context = RuntimeManager.getContext();
        }
        DrawableRequestBuilder<String> builder = Glide.with(context)
                .load(imageUrl)
                .placeholder(placeholderDrawableId)
                .error(placeholderDrawableId)
                .animate(R.anim.fade_in)
                .skipMemoryCache(!cache)
                .diskCacheStrategy(cache ? DiskCacheStrategy.SOURCE : DiskCacheStrategy.NONE)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onResourceReady(GlideDrawable resource,
                                                   String model,
                                                   Target<GlideDrawable> target,
                                                   boolean isFromMemoryCache,
                                                   boolean isFirstResource) {
                        if (listener != null) {
                            listener.onSuccess(resource);
                        }
                        return false;
                    }

                    @Override
                    public boolean onException(Exception e,
                                               String model,
                                               Target<GlideDrawable> target,
                                               boolean isFirstResource) {
                        if (listener != null) {
                            listener.onFail(e);
                        }
                        return false;
                    }
                });
        (centerCrop ? builder.centerCrop() : builder.fitCenter()).into(imageView);
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

    public interface OnDrawableLoadListener {
        void onSuccess(GlideDrawable drawable);

        void onFail(Exception e);
    }
}
