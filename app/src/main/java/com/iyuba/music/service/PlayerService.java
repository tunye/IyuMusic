package com.iyuba.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.buaa.ct.videocachelibrary.HttpProxyCacheServer;
import com.iyuba.music.MusicApplication;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.listener.OnHeadSetListener;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.HeadsetPlugReceiver;
import com.iyuba.music.receiver.NotificationBeforeReceiver;
import com.iyuba.music.receiver.NotificationCloseReceiver;
import com.iyuba.music.receiver.NotificationNextReceiver;
import com.iyuba.music.receiver.NotificationPauseReceiver;
import com.iyuba.music.request.newsrequest.ReadCountAddRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.util.HeadSetUtil;
import com.iyuba.music.widget.player.StandardPlayer;

import java.io.File;
import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/22.
 */
public class PlayerService extends Service implements OnHeadSetListener {
    private HttpProxyCacheServer proxy;
    private StandardPlayer player;
    private int curArticleId;
    private PhoneStateListener phoneStateListener;
    private HeadsetPlugReceiver headsetPlugReceiver;

    private NotificationCloseReceiver close;
    private NotificationBeforeReceiver before;
    private NotificationNextReceiver next;
    private NotificationPauseReceiver pause;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
        registerNotificationBroadcastReceiver();
        HeadSetUtil.getInstance().open(getApplicationContext(), this);
        init();
        ((MusicApplication) getApplication()).setPlayerService(this);
    }

    private void registerNotificationBroadcastReceiver() {
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

    private void unRegisterNotificationBroadcaster() {
        Context context = RuntimeManager.getContext();
        context.unregisterReceiver(pause);
        context.unregisterReceiver(before);
        context.unregisterReceiver(next);
        context.unregisterReceiver(close);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationUtil.NOTIFICATION_ID, NotificationUtil.getInstance().initNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterNotificationBroadcaster();
        unRegisterBroadcastReceiver();
        NotificationUtil.getInstance().removeNotification();
        stopForeground(true);
        stopSelf();
        player.stopPlayback();
        player = null;
    }

    public void init() {
        player = new StandardPlayer(RuntimeManager.getContext());
        proxy = RuntimeManager.getProxy();
        curArticleId = 0;
    }

    public void setListener(final IPlayerListener playerListener) {
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                if (playerListener != null) {
                    playerListener.onPrepare();
                }
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (ConfigManager.getInstance().getStudyPlayMode() != 0) {
                    next(true);
                    if (RuntimeManager.getApplication().isAppointForeground("MainActivity")) {
                        Intent i = new Intent("com.iyuba.music.main");
                        i.putExtra("message", "change");
                        sendBroadcast(i);
                    } else if (RuntimeManager.getApplication().isAppointForeground("LocalMusicActivity")) {
                        Intent i = new Intent("com.iyuba.music.localmusic");
                        i.putExtra("message", "change");
                        sendBroadcast(i);
                    }
                }
                if (playerListener != null) {
                    playerListener.onFinish();
                }
            }
        });
        player.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                if (playerListener != null) {
                    playerListener.onBufferChange(percent);
                }
            }
        });

        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                if (playerListener != null) {
                    playerListener.onError();
                }
                return false;
            }
        });
    }

    public StandardPlayer getPlayer() {
        return player;
    }

    public int getCurArticleId() {
        return curArticleId;
    }

    public void setCurArticleId(int curArticleId) {
        this.curArticleId = curArticleId;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void startPlay(Article article, boolean modeChange) {
        if (article != null && article.getId() == curArticleId && !modeChange) {
        } else if (article != null) {
            if (!StudyManager.getInstance().getApp().equals("101")) {
                LocalInfoOp localInfoOp = new LocalInfoOp();
                StudyManager.getInstance().setStartTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                localInfoOp.updateSee(article.getId(), article.getApp());
                ReadCountAddRequest.exeRequest(ReadCountAddRequest.generateUrl(article.getId(), "music"), null);
            }
            String netUrl = getUrl(article);
            String playPath;
            if (netUrl.startsWith("http")) {
                playPath = proxy.getProxyUrl(netUrl);
            } else {
                playPath = netUrl;
            }
            setNotification();
            try {
                player.reset();
            } catch (Exception e) {
                e.printStackTrace();
            }
            player.setVideoPath(playPath);
        }
    }

    public void next(boolean finish) {
        if (finish) {
            StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 1);
        } else {
            StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0);
        }
        StudyManager.getInstance().next();
    }

    public void before() {
        StudyRecordUtil.recordStop(StudyManager.getInstance().getLesson(), 0);
        StudyManager.getInstance().before();
    }

    public String getUrl(Article article) {
        String url;
        StringBuilder localUrl = new StringBuilder();
        File localFile;
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                if (StudyManager.getInstance().getMusicType() == 0) {
                    url = DownloadService.getSongUrl(article.getApp(), article.getMusicUrl());
                    localUrl.append(ConstantManager.musicFolder).append(File.separator).append(article.getId()).append(".mp3");
                    localFile = new File(localUrl.toString());
                } else {
                    url = DownloadService.getAnnouncerUrl(article.getId(), article.getSoundUrl());
                    localUrl.append(ConstantManager.musicFolder).append(File.separator).append(article.getId()).append("s.mp3");
                    localFile = new File(localUrl.toString());
                }
                if (localFile.exists()) {
                    return localUrl.toString();
                } else {
                    return url;
                }
            case "101":
                return article.getMusicUrl();
            default:
                url = DownloadService.getSongUrl(article.getApp(), article.getMusicUrl());
                localUrl.append(ConstantManager.musicFolder).append(File.separator).append(article.getApp()).append("-").append(article.getId()).append(".mp3");
                localFile = new File(localUrl.toString());
                if (localFile.exists()) {
                    return localUrl.toString();
                } else {
                    return url;
                }
        }
    }

    private void setNotification() {
        String url;
        if (StudyManager.getInstance().getApp().equals("209")) {
            url = "http://staticvip.iyuba.com/images/song/" + StudyManager.getInstance().getCurArticle().getPicUrl();
        } else {
            url = StudyManager.getInstance().getCurArticle().getPicUrl();
        }
        NotificationUtil.getInstance().updateNotification(url);
    }

    public void registerBroadcastReceiver() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        if (player != null && player.isPlaying()) {
                            sendBroadcast(new Intent("iyumusic.pause"));
                        }
                        break;
                    default:
                        break;
                }
            }
        };
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Service.TELEPHONY_SERVICE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        IntentFilter ifr = new IntentFilter("android.intent.action.HEADSET_PLUG");
        headsetPlugReceiver = new HeadsetPlugReceiver();
        registerReceiver(headsetPlugReceiver, ifr);
    }

    private void unRegisterBroadcastReceiver() {
        HeadSetUtil.getInstance().close(getApplicationContext());
        unregisterReceiver(headsetPlugReceiver);
        if (phoneStateListener != null) {
            TelephonyManager tm = (TelephonyManager) this.getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onClick() {
        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.pause"));
    }

    @Override
    public void onDoubleClick() {
        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.next"));
    }

    @Override
    public void onThreeClick() {
        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.before"));
    }
}
