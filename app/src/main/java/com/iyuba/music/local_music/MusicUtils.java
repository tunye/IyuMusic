package com.iyuba.music.local_music;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.text.TextUtils;

import com.iyuba.music.entity.article.Article;
import com.iyuba.music.util.DateFormat;

import java.util.ArrayList;

public class MusicUtils {
    public static ArrayList<Article> getAllSongs(Context context, String path) {
        Cursor c = query(context,
                new String[]{MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                        MediaStore.Audio.Media.ARTIST, MediaColumns.DATA,
                        MediaStore.Audio.Media.DURATION,}
        );

        if (c == null || c.getCount() == 0) {
            return new ArrayList<>();
        }
        ArrayList<Article> musics = new ArrayList<>();
        int id = c.getColumnIndexOrThrow(MediaStore.Audio.Media._ID);
        int name = c.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE);
        int singerName = c.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST);
        int url = c.getColumnIndex(MediaColumns.DATA);
        int duration = c.getColumnIndex(MediaStore.Audio.Media.DURATION);
        Article article;
        while (c.moveToNext()) {
            if (!TextUtils.isEmpty(path) && c.getString(url).toLowerCase().contains(path.toLowerCase())) {
                article = new Article();
                article.setId(c.getInt(id));
                article.setTitle(c.getString(name));
                article.setSinger(c.getString(singerName));
                article.setMusicUrl(c.getString(url));
                article.setApp("101");
                if (c.getInt(duration) > 1000 * 60) {
                    article.setBroadcaster(DateFormat.formatTime(c.getInt(duration) / 1000));
                    musics.add(article);
                }
            }
        }
        c.close();
        return musics;
    }

    private static Cursor query(Context context, String[] projection) {
        ContentResolver resolver = context.getContentResolver();
        return resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, "is_music=1", null,
                MediaColumns.TITLE);
    }
}
