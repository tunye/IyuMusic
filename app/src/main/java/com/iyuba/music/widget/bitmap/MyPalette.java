package com.iyuba.music.widget.bitmap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.graphics.Palette;

import com.iyuba.music.listener.IOperationFinish;
import com.iyuba.music.manager.RuntimeManager;


/**
 * Created by 10202 on 2015/10/10.
 */
public class MyPalette {
    private Palette palette;

    private int darkVibrantColor;//暗鲜艳色
    private int darkMutedColor;//暗柔和的颜色
    private int lightVibrantColor;//亮鲜艳色(淡色)
    private int lightMutedColor;//亮柔和色(淡色)
    private int mutedColor;//柔和色
    private int vibrantColor;//鲜艳色

    public void getByResource(int drawableID, final IOperationFinish operationFinish) {
        Bitmap bm = BitmapFactory.decodeResource(RuntimeManager.getInstance().getContext().getResources(), drawableID);
        Palette.from(bm).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette generatePalette) {
                palette = generatePalette;
                generateAllColor();
                operationFinish.finish();
            }
        });
    }

    public void getByDrawable(Drawable drawable, final IOperationFinish operationFinish) {
        Bitmap bm = BitmapUtils.drawableToBitmap(drawable);
        Palette.from(bm).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette generatePalette) {
                palette = generatePalette;
                generateAllColor();
                operationFinish.finish();
            }
        });
    }

    public void getByBitmap(Bitmap bm, final IOperationFinish operationFinish) {
        Palette.from(bm).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette generatePalette) {
                palette = generatePalette;
                generateAllColor();
                operationFinish.finish();
            }
        });
    }

    private void generateAllColor() {
        darkVibrantColor = palette.getDarkVibrantColor(0);
        darkMutedColor = palette.getDarkMutedColor(0);
        lightVibrantColor = palette.getLightVibrantColor(0);
        lightMutedColor = palette.getLightMutedColor(0);
        mutedColor = palette.getMutedColor(0);
        vibrantColor = palette.getVibrantColor(0);
    }

    public Palette getPalette() {
        return palette;
    }

    public int getDarkVibrantColor() {
        return darkVibrantColor;
    }

    public int getDarkMutedColor() {
        return darkMutedColor;
    }

    public int getLightVibrantColor() {
        return lightVibrantColor;
    }

    public int getLightMutedColor() {
        return lightMutedColor;
    }

    public int getMutedColor() {
        return mutedColor;
    }

    public int getVibrantColor() {
        return vibrantColor;
    }

    @Override
    public String toString() {
        return "MyPalette{" +
                "darkVibrantColor=" + darkVibrantColor +
                ", darkMutedColor=" + darkMutedColor +
                ", lightVibrantColor=" + lightVibrantColor +
                ", lightMutedColor=" + lightMutedColor +
                ", mutedColor=" + mutedColor +
                ", vibrantColor=" + vibrantColor +
                '}';
    }
}
