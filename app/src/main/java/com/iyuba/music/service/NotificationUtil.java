package com.iyuba.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.iyuba.music.R;
import com.iyuba.music.activity.MainActivity;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.NotificationBeforeReceiver;
import com.iyuba.music.receiver.NotificationCloseReceiver;
import com.iyuba.music.receiver.NotificationNextReceiver;
import com.iyuba.music.receiver.NotificationPauseReceiver;
import com.iyuba.music.widget.bitmap.ReadBitmap;

public class NotificationUtil {
    static final int NOTIFICATION_ID = 209;
    public static final String PAUSE_FLAG = "pause_flag";
    public static final String PLAY_FLAG = "play_flag";
    private Notification notification;
    private NotificationCloseReceiver close;
    private NotificationBeforeReceiver before;
    private NotificationNextReceiver next;
    private NotificationPauseReceiver pause;

    private NotificationUtil() {
    }

    private static class SingleInstanceHelper {
        private static NotificationUtil instance = new NotificationUtil();
    }

    public static NotificationUtil getInstance() {
        return SingleInstanceHelper.instance;
    }

    Notification initNotification() {
        notification = new Notification();
        Context context = RuntimeManager.getContext();

        IntentFilter ifr = new IntentFilter("iyumusic.close");
        close = new NotificationCloseReceiver();
        context.registerReceiver(close, ifr);
        ifr = new IntentFilter("iyumusic.pause");
        pause = new NotificationPauseReceiver();
        context.registerReceiver(pause, ifr);
        ifr = new IntentFilter("iyumusic.next");
        next = new NotificationNextReceiver();
        context.registerReceiver(next, ifr);
        ifr = new IntentFilter("iyumusic.before");
        before = new NotificationBeforeReceiver();
        context.registerReceiver(before, ifr);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
        contentView.setOnClickPendingIntent(R.id.notify_close, receiveCloseIntent());
        contentView.setOnClickPendingIntent(R.id.notify_latter, receiveNextIntent());
        contentView.setOnClickPendingIntent(R.id.notify_play, receivePauseIntent());
        contentView.setOnClickPendingIntent(R.id.notify_formmer, receiveBeforeIntent());

        contentView.setTextViewText(R.id.notify_title, context.getString(R.string.app_name));
        contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.app_corp));
        contentView.setImageViewResource(R.id.notify_img, R.drawable.default_music);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_circle);
        notificationBuilder.setContent(contentView);
        notificationBuilder.setLargeIcon(ReadBitmap.readBitmap(RuntimeManager.getContext(), R.drawable.ic_launcher_circle));
        notificationBuilder.setAutoCancel(false);
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        notificationBuilder.setContentIntent(pendingIntent);
        notification = notificationBuilder.build();
        notification.bigContentView = contentView;
        return notification;
    }

    void updateNotification(String imgUrl) {
        Context context = RuntimeManager.getContext();
        Article curArticle = StudyManager.getInstance().getCurArticle();
        Intent intent;
        if (StudyManager.getInstance().getApp().equals("101")) {
            intent = new Intent(context, LocalMusicActivity.class);
        } else {
            intent = new Intent(context, StudyActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                contentView.setTextViewText(R.id.notify_title, curArticle.getTitle());
                contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.article_singer, curArticle.getSinger()));
                //contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.article_announcer, curArticle.getBroadcaster()));
                break;
            case "101":
                contentView.setTextViewText(R.id.notify_title, curArticle.getTitle());
                contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.article_singer, curArticle.getSinger()));
                //contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.article_duration, curArticle.getBroadcaster()));
                break;
            default:
                contentView.setTextViewText(R.id.notify_singer, curArticle.getTitle());
                contentView.setTextViewText(R.id.notify_title, curArticle.getTitle_cn());
                //contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.app_intro));
                break;
        }
        contentView.setImageViewResource(R.id.notify_img, R.drawable.default_music);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(curArticle.getTitle());
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setSmallIcon(R.drawable.ic_launcher_circle);
        notificationBuilder.setContent(contentView);
        notificationBuilder.setLargeIcon(ReadBitmap.readBitmap(RuntimeManager.getContext(), R.drawable.ic_launcher_circle));
        notificationBuilder.setOngoing(true);
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setContentIntent(pendingIntent);
        notification = notificationBuilder.build();
        notification.bigContentView = contentView;
        if (!StudyManager.getInstance().getApp().equals("101")) {
            NotificationTarget notificationTarget = new NotificationTarget(context, contentView,
                    R.id.notify_img, notification, NOTIFICATION_ID);
            Glide.with(context).load(imgUrl).asBitmap().into(notificationTarget);
        }
        ((NotificationManager) RuntimeManager.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    }

    public void updatePlayStateNotification(String cmd) {
        if (cmd.equals(PAUSE_FLAG)) {
            notification.bigContentView.setImageViewResource(R.id.notify_play, R.drawable.play);
        } else if (cmd.equals(PLAY_FLAG)) {
            notification.bigContentView.setImageViewResource(R.id.notify_play, R.drawable.pause);
        }
        ((NotificationManager) RuntimeManager.getContext().getSystemService(Context.NOTIFICATION_SERVICE)).notify(NOTIFICATION_ID, notification);
    }

    public void removeNotification() {
        Context context = RuntimeManager.getContext();
        context.unregisterReceiver(pause);
        context.unregisterReceiver(before);
        context.unregisterReceiver(next);
        context.unregisterReceiver(close);
        ((NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
    }

    private PendingIntent receivePauseIntent() {
        Intent intent = new Intent("iyumusic.pause");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, 0);
    }

    private PendingIntent receiveNextIntent() {
        Intent intent = new Intent("iyumusic.next");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, 0);
    }

    private PendingIntent receiveBeforeIntent() {
        Intent intent = new Intent("iyumusic.before");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, 0);
    }

    private PendingIntent receiveCloseIntent() {
        Intent intent = new Intent("iyumusic.close");
        return PendingIntent.getBroadcast(RuntimeManager.getContext(), 0, intent, 0);
    }
}