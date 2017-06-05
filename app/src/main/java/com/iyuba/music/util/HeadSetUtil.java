package com.iyuba.music.util;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

import com.iyuba.music.listener.OnHeadSetListener;
import com.iyuba.music.manager.RuntimeManager;
import com.iyuba.music.manager.ConfigManager;
import com.iyuba.music.receiver.MediaButtonReceiver;

/**
 * Created by 10202 on 2017-04-11.
 */

public class HeadSetUtil {
    private MyOnAudioFocusChangeListener onAudioFocusChangeListener;
    private OnHeadSetListener headSetListener;

    private HeadSetUtil() {
        onAudioFocusChangeListener = new MyOnAudioFocusChangeListener();
    }

    public static HeadSetUtil getInstance() {
        return HeadSetUtil.SingleInstanceHelper.instance;
    }

    public void open(Context context, OnHeadSetListener headSetListener) {
        this.headSetListener = headSetListener;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
        if (ConfigManager.getInstance().isMediaButton()) {
            ComponentName name = new ComponentName(context.getPackageName(), MediaButtonReceiver.class.getName());
            audioManager.registerMediaButtonEventReceiver(name);
        }
    }

    /**
     * 关闭耳机线控监听
     *
     * @param context
     */
    public void close(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(onAudioFocusChangeListener);
        if (ConfigManager.getInstance().isMediaButton()) {
            ComponentName name = new ComponentName(context.getPackageName(), MediaButtonReceiver.class.getName());
            audioManager.unregisterMediaButtonEventReceiver(name);
        }
        this.headSetListener = null;
    }

    /**
     * 获取耳机单击双击接口
     *
     * @return
     */
    public OnHeadSetListener getOnHeadSetListener() {
        return headSetListener;
    }

    private static class MyOnAudioFocusChangeListener implements
            AudioManager.OnAudioFocusChangeListener {
        private boolean changeByAudioListener;

        @Override
        public void onAudioFocusChange(int focusChange) {
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_LOSS:
                    if (RuntimeManager.getApplication().getPlayerService().getPlayer().isPlaying()) {
                        // 因为会长时间失去，所以直接暂停
                        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.pause"));
                    }
//                    mPausedByTransientLossOfFocus = false;
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                    if (RuntimeManager.getApplication().getPlayerService().getPlayer().isPlaying()) {
                        // 短暂失去焦点，先暂停。同时将标志位置成重新获得焦点后就开始播放
                        changeByAudioListener = true;
                        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.pause"));
//                        mPausedByTransientLossOfFocus = true;
                    }
                    break;
                case AudioManager.AUDIOFOCUS_GAIN:
                    // 重新获得焦点，且符合播放条件，开始播放
                    if (!RuntimeManager.getApplication().getPlayerService().getPlayer().isPlaying() && changeByAudioListener) {
//                        mPausedByTransientLossOfFocus = false;
                        changeByAudioListener = false;
                        RuntimeManager.getContext().sendBroadcast(new Intent("iyumusic.pause"));
                    }
                    break;
            }
        }
    }

    private static class SingleInstanceHelper {
        private static HeadSetUtil instance = new HeadSetUtil();
    }
}
