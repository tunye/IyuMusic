package com.iyuba.music.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.iyuba.music.MusicApplication;
import com.iyuba.music.R;
import com.iyuba.music.download.DownloadService;
import com.iyuba.music.entity.article.Article;
import com.iyuba.music.entity.article.LocalInfoOp;
import com.iyuba.music.entity.article.StudyRecordUtil;
import com.iyuba.music.listener.IPlayerListener;
import com.iyuba.music.manager.ConstantManager;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.StudyManager;
import com.iyuba.music.network.NetWorkState;
import com.iyuba.music.receiver.HeadsetPlugReceiver;
import com.iyuba.music.request.newsrequest.ReadCountAddRequest;
import com.iyuba.music.util.DateFormat;
import com.iyuba.music.widget.CustomToast;
import com.iyuba.music.widget.player.StandardPlayer;

import java.io.File;
import java.util.Calendar;

/**
 * Created by 10202 on 2015/12/22.
 */
public class PlayerService extends Service {
    private AudioManager audioManager;
    private StandardPlayer player;
    private Article curArticle;
    private MyBinder myBinder = new MyBinder();
    private MyOnAudioFocusChangeListener mListener;
    private PhoneStateListener phoneStateListener;
    private boolean mPausedByTransientLossOfFocus;
    private HeadsetPlugReceiver headsetPlugReceiver;

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerBroadcastReceiver();
        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(mListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        mListener = new MyOnAudioFocusChangeListener();
        init();
        ((MusicApplication) getApplication()).setPlayerService(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        flags = Service.START_FLAG_REDELIVERY;
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        audioManager.abandonAudioFocus(mListener);
        player.stopPlayback();
        player = null;
        unRegisterBroadcastReceiver();
        stopForeground(true);
        stopSelf();
    }

    public void init() {
        player = new StandardPlayer(RuntimeManager.getContext());
        curArticle = new Article();
        curArticle.setId(0);
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

    public Article getCurArticle() {
        return curArticle;
    }

    public void setCurArticle(Article curArticle) {
        this.curArticle = curArticle;
    }

    public boolean isPlaying() {
        return player != null && player.isPlaying();
    }

    public void startPlay(Article article, boolean modeChange) {
        if (article.getId() == curArticle.getId() && !modeChange) {
        } else {
            if (!StudyManager.instance.getApp().equals("101")) {
                LocalInfoOp localInfoOp = new LocalInfoOp();
                StudyManager.instance.setStartTime(DateFormat.formatTime(Calendar.getInstance().getTime()));
                localInfoOp.updateSee(article.getId(), article.getApp());
                ReadCountAddRequest.exeRequest(ReadCountAddRequest.generateUrl(article.getId(), "music"), null);
            }
            String playPath = getUrl(article);
            if (playPath.contains("http")) {
                if (NetWorkState.getInstance().isConnectByCondition(NetWorkState.ALL_NET)) {
                    player.reset();
                    player.setVideoPath(playPath);
                    setNotification();
                    if (!NetWorkState.getInstance().isConnectByCondition(NetWorkState.EXCEPT_2G)) {
                        CustomToast.INSTANCE.showToast(R.string.net_speed_slow);
                    }
                } else {
                    CustomToast.INSTANCE.showToast(R.string.no_internet);
                }
            } else {
                player.reset();
                player.setVideoPath(playPath);
                setNotification();
            }
        }
    }

    public void next(boolean finish) {
        if (finish) {
            StudyRecordUtil.recordStop(StudyManager.instance.getLesson(), 1);
        } else {
            StudyRecordUtil.recordStop(StudyManager.instance.getLesson(), 0);
        }
        StudyManager.instance.next();
    }

    public void before() {
        StudyRecordUtil.recordStop(StudyManager.instance.getLesson(), 0);
        StudyManager.instance.before();
    }

    public String getUrl(Article article) {
        String url;
        StringBuilder localUrl = new StringBuilder();
        File localFile;
        switch (StudyManager.instance.getApp()) {
            case "209":
                if (StudyManager.instance.getMusicType() == 0) {
                    url = DownloadService.getSongUrl(article.getApp(), article.getMusicUrl());
                    localUrl.append(ConstantManager.instance.getMusicFolder()).append(File.separator).append(article.getId()).append(".mp3");
                    localFile = new File(localUrl.toString());
                } else {
                    url = DownloadService.getAnnouncerUrl(article.getId(), article.getSoundUrl());
                    localUrl.append(ConstantManager.instance.getMusicFolder()).append(File.separator).append(article.getId()).append("s.mp3");
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
                localUrl.append(ConstantManager.instance.getMusicFolder()).append(File.separator).append(article.getApp()).append("-").append(article.getId()).append(".mp3");
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
        if (StudyManager.instance.getApp().equals("209")) {
            url = "http://staticvip.iyuba.com/images/song/" + StudyManager.instance.getCurArticle().getPicUrl();
        } else {
            url = StudyManager.instance.getCurArticle().getPicUrl();
        }
        Intent intent = new Intent(RuntimeManager.getContext(), BigNotificationService.class);
        intent.setAction(BigNotificationService.NOTIFICATION_SERVICE);
        intent.putExtra(BigNotificationService.COMMAND, BigNotificationService.COMMAND_SHOW);
        intent.putExtra(BigNotificationService.NOTIFICATION_PIC, url);
        BigNotificationService.INSTANCE.setNotificationCommand(intent);
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
        unregisterReceiver(headsetPlugReceiver);
        if (phoneStateListener != null) {
            TelephonyManager tm = (TelephonyManager) this
                    .getSystemService(Service.TELEPHONY_SERVICE);
            tm.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    public class MyBinder extends Binder {
        public PlayerService getService() {
            return PlayerService.this;
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
                    mPausedByTransientLossOfFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (player.isPlaying()) {
                        // 短暂失去焦点，先暂停。同时将标志位置成重新获得焦点后就开始播放
                        sendBroadcast(new Intent("iyumusic.pause"));
                        mPausedByTransientLossOfFocus = true;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 重新获得焦点，且符合播放条件，开始播放
                    if (!player.isPlaying()) {
                        mPausedByTransientLossOfFocus = false;
                        sendBroadcast(new Intent("iyumusic.pause"));
                    }
                    break;
            }
        }
    }
}
