package com.iyuba.music.widget.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

/**
 * 读取大图片
 *
 * @author 陈彤
 */
public class ReadBitmap {
    /*
     * 按照资源id读取图片
     */
    public static Bitmap readBitmap(Context context, int id) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位图 565代表对应三原色占的位数
        opt.inInputShareable = true;
        opt.inPurgeable = true;// 设置图片可以被回收
        InputStream is = context.getResources().openRawResource(id);
        return BitmapFactory.decodeStream(is, null, opt);
    }

    /*
     * 按照文件流读取图片
     */
    public static Bitmap readBitmap(Context context, InputStream is) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;// 表示16位位图 565代表对应三原色占的位数
        opt.inInputShareable = true;
        opt.inPurgeable = true;// 设置图片可以被回收
        return BitmapFactory.decodeStream(is, null, opt);
    }
}
