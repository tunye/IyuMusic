package com.iyuba.music.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.iyuba.music.R;
import com.iyuba.music.activity.study.StudyActivity;
import com.iyuba.music.entity.artical.Article;
import com.iyuba.music.local_music.LocalMusicActivity;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.NotificationBeforeReceiver;
import com.iyuba.music.receiver.NotificationCloseReceiver;
import com.iyuba.music.receiver.NotificationNextReceiver;
import com.iyuba.music.receiver.NotificationPauseReceiver;
import com.iyuba.music.widget.bitmap.ReadBitmap;

public enum BigNotificationService {
    INSTANCE;
    public static final String NOTIFICATION_SERVICE = "notification_service";
    public static final String COMMAND = "cmd";
    public static final String COMMAND_SHOW = "show";
    public static final String COMMAND_REMOVE = "remove";
    public static final String COMMAND_CHANGE_STATE = "command_change_state";
    public static final String NOTIFICATION_PIC = "notification_pic";
    public static final int NOTIFICATION_ID = 209;
    public boolean isAlive;
    private NotificationCloseReceiver close;
    private NotificationBeforeReceiver before;
    private NotificationNextReceiver next;
    private NotificationPauseReceiver pause;
    private Notification notification;
    private NotificationManager notificationManager;

    BigNotificationService() {
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
    }

    public void setNotificationCommand(Intent intent) {
        String action = intent.getAction();
        if (NOTIFICATION_SERVICE.equals(action)) {
            String cmd = intent.getStringExtra(COMMAND);
            if (COMMAND_SHOW.equals(cmd)) {
                createNotification(intent.getStringExtra(NOTIFICATION_PIC));
                isAlive = true;
            } else if (COMMAND_REMOVE.equals(cmd)) {
                removeNotification();
                isAlive = false;
            } else if (COMMAND_CHANGE_STATE.equals(cmd)) {
                updatePlayStateNotification(intent.getStringExtra("state"));
                isAlive = true;
            }
        }
    }

    public void removeNotification() {
        Context context = RuntimeManager.getContext();
        context.unregisterReceiver(pause);
        context.unregisterReceiver(before);
        context.unregisterReceiver(next);
        context.unregisterReceiver(close);
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        notificationManager.cancel(NOTIFICATION_ID);
    }

    public void createNotification(String imgUrl) {
        if (notification == null) {
            notification = new Notification();
        }
        Context context = RuntimeManager.getContext();
        Article curArticle = StudyManager.instance.getCurArticle();
        Intent intent;
        if (StudyManager.instance.getApp().equals("101")) {
            intent = new Intent(context, LocalMusicActivity.class);
        } else {
            intent = new Intent(context, StudyActivity.class);
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        RemoteViews contentView = new RemoteViews(context.getPackageName(), R.layout.notification);


        contentView.setOnClickPendingIntent(R.id.notify_close, receiveCloseIntent());
        contentView.setOnClickPendingIntent(R.id.notify_latter, receiveNextIntent());
        contentView.setOnClickPendingIntent(R.id.notify_play, receivePauseIntent());
        contentView.setOnClickPendingIntent(R.id.notify_formmer, receiveBeforeIntent());
        if (StudyManager.instance.getApp().equals("209")) {
            contentView.setTextViewText(R.id.notify_title, curArticle.getTitle());
            contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.artical_singer, curArticle.getSinger()));
            contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.artical_announcer, curArticle.getBroadcaster()));
        } else if (StudyManager.instance.getApp().equals("101")) {
            contentView.setTextViewText(R.id.notify_title, curArticle.getTitle());
            contentView.setTextViewText(R.id.notify_singer, context.getString(R.string.artical_singer, curArticle.getSinger()));
            contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.artical_duration, curArticle.getBroadcaster()));
        } else {
            contentView.setTextViewText(R.id.notify_singer, curArticle.getTitle());
            contentView.setTextViewText(R.id.notify_title, curArticle.getTitle_cn());
            contentView.setTextViewText(R.id.notify_announcer, context.getString(R.string.app_intro));
        }
        contentView.setImageViewResource(R.id.notify_img, R.drawable.default_music);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentTitle(context.getString(R.string.app_name));
        notificationBuilder.setContentText(curArticle.getTitle());
        notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContent(contentView);
        notificationBuilder.setLargeIcon(ReadBitmap.readBitmap(RuntimeManager.getContext(),R.mipmap.ic_launcher));
        notificationBuilder.setOngoing(true);   //Create OnGoing Status Bar
        notificationBuilder.setAutoCancel(false);
        notificationBuilder.setContentIntent(pendingIntent);
        notification = notificationBuilder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notification.bigContentView = contentView;
        } else {
            notification.contentView = contentView;
        }
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, notification);
        if (!StudyManager.instance.getApp().equals("101")) {
            NotificationTarget notificationTarget = new NotificationTarget(context, contentView,
                    R.id.notify_img, notification, NOTIFICATION_ID);
            Glide.with(context).load(imgUrl).asBitmap().animate(R.anim.fade_in).into(notificationTarget);
        }
    }

    private void updatePlayStateNotification(String cmd) {
        if (cmd.equals("pause")) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification.bigContentView.setImageViewResource(R.id.notify_play, R.drawable.play);
            } else {
                notification.contentView.setImageViewResource(R.id.notify_play, R.drawable.play);
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                notification.bigContentView.setImageViewResource(R.id.notify_play, R.drawable.pause);
            } else {
                notification.contentView.setImageViewResource(R.id.notify_play, R.drawable.pause);
            }
        }
        notificationManager.notify(NOTIFICATION_ID, notification);
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