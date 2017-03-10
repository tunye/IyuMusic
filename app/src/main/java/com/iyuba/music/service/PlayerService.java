package com.iyuba.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.receiver.HeadsetPlugReceiver;
import com.iyuba.music.request.newsrequest.ReadCountAddRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.player.StandardPlayer;

import java.io.File;
import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/22.
 */
public class PlayerService extends Service {
    private AudioManager audioManager;
    private StandardPlayer player;
    private int curArticle;
    private MyOnAudioFocusChangeListener onAudioFocusChangeListener;
    private PhoneStateListener phoneStateListener;
    private HeadsetPlugReceiver headsetPlugReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
        onAudioFocusChangeListener = new MyOnAudioFocusChangeListener();
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        init();
        ((MusicApplication) getApplication()).setPlayerService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NotificationUtil.NOTIFICATION_ID, NotificationUtil.getInstance().initNotification());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBroadcastReceiver();
//        seems shoule do this code.
//        stopForeground(true);
        player.stopPlayback();
        player = null;
    }

    public void init() {
        player = new StandardPlayer(RuntimeManager.getContext());
        curArticle = 0;
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
                next(true);
                if (((MusicApplication) RuntimeManager.getApplication()).isAppointForeground("MainActivity")) {
                    Intent i = new Intent("com.iyuba.music.main");
                    i.putExtra("message", "change");
                    sendBroadcast(i);
                } else if (((MusicApplication) RuntimeManager.getApplication()).isAppointForeground("LocalMusicActivity")) {
                    Intent i = new Intent("com.iyuba.music.localmusic");
                    i.putExtra("message", "change");
                    sendBroadcast(i);
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

    public int getCurArticle() {
        return curArticle;
    }

    public void setCurArticle(int curArticle) {
        this.curArticle = curArticle;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void startPlay(Article article, boolean modeChange) {
        if (article.getId() == curArticle && !modeChange) {
        } else {
            if (!StudyManager.getInstance().getApp().equals("101")) {
                LocalInfoOp localInfoOp = new LocalInfoOp();
                StudyManager.getInstance().setStartTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                localInfoOp.updateSee(article.getId(), article.getApp());
                ReadCountAddRequest.exeRequest(ReadCountAddRequest.generateUrl(article.getId(), "music"), null);
            }
            String playPath = getUrl(article);
            if (playPath.startsWith("http")) {
                try {
                    player.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                player.setVideoPath(playPath);
                setNotification();
            } else {
                try {
                    player.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                player.setVideoPath(playPath);
                setNotification();
            }
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

    public boolean isOnlineArticle(Article article) {
        String playPath = getUrl(article);
        return playPath.startsWith("http");
    }

    private String getUrl(Article article) {
        String url;
        StringBuilder localUrl = new StringBuilder();
        File localFile;
        switch (StudyManager.getInstance().getApp()) {
            case "209":
                if (StudyManager.getInstance().getMusicType() == 0) {
                    url = DownloadService.getSongUrl(article.getApp(), article.getMusicUrl());
                    localUrl.append(ConstantManager.getInstance().getMusicFolder()).append(File.separator).append(article.getId()).append(".mp3");
                    localFile = new File(localUrl.toString());
                } else {
                    url = DownloadService.getAnnouncerUrl(article.getId(), article.getSoundUrl());
                    localUrl.append(ConstantManager.getInstance().getMusicFolder()).append(File.separator).append(article.getId()).append("s.mp3");
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
                localUrl.append(ConstantManager.getInstance().getMusicFolder()).append(File.separator).append(article.getApp()).append("-").append(article.getId()).append(".mp3");
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
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        unregisterReceiver(headsetPlugReceiver);
        if (phoneStateListener != null) {
            TelephonyManager tm = (TelephonyManager) this
                    .getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private class MyOnAudioFocusChangeListener implements
            AudioManager.OnAudioFocusChangeListener {
        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (player.isPlaying()) {
                        //player.pause();// 因为会长时间失去，所以直接暂停
                        sendBroadcast(new Intent("iyumusic.pause"));
                    }
//                    mPausedByTransientLossOfFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (player.isPlaying()) {
                        // 短暂失去焦点，先暂停。同时将标志位置成重新获得焦点后就开始播放
                        sendBroadcast(new Intent("iyumusic.pause"));
//                        mPausedByTransientLossOfFocus = true;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 重新获得焦点，且符合播放条件，开始播放
                    if (!player.isPlaying()) {
//                        mPausedByTransientLossOfFocus = false;
                        sendBroadcast(new Intent("iyumusic.pause"));
                    }
                    break;
            }
        }
    }
}
