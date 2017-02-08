package com.iyuba.music.widget.player;

/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.Service;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.widget.MediaController;

import java.io.IOException;

/**
 * 视频框架
 *
 * @author chentong
 *         <p/>
 *         <p/>
 *         Displays a video file. The VideoView class can load images from
 *         various sources (such as resources or content providers), takes care
 *         of computing its measurement from the video so that it can be used in
 *         any layout manager, and provides various display options such as
 *         scaling and tinting.
 */
public class StandardPlayer implements MediaController.MediaPlayerControl {
    private Context mContext;
    // settable by the client
    private Uri mUri;
    private int currVolume;
    private int mDuration;
    // All the stuff we need for playing and showing a video
    private MediaPlayer mMediaPlayer = null;
    private boolean mIsPrepared = false;
    private int mCurrentBufferPercentage;
    private AudioManager audioManager;
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Message message;
            switch (msg.what) {
                case 0:
                    currVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                    message = new Message();
                    message.what = 1;
                    message.arg1 = 0;
                    handler.sendMessage(message);
                    break;
                case 1:
                    message = new Message();
                    message.what = 1;
                    message.arg1 = msg.arg1 + 1;
                    if (message.arg1 < 7) {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currVolume / 6 * (6 - message.arg1), 0);
                        handler.sendMessageDelayed(message, 150);
                    } else {
                        handler.sendEmptyMessage(2);
                    }
                    break;
                case 2:
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currVolume, 0);
                    mMediaPlayer.pause();
                    break;
                case 3:
                    currVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
                    message = new Message();
                    message.what = 4;
                    message.arg1 = 0;
                    handler.sendMessage(message);
                    mMediaPlayer.start();
                    break;
                case 4:
                    message = new Message();
                    message.what = 4;
                    message.arg1 = msg.arg1 + 1;
                    if (message.arg1 < 7) {
                        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currVolume / 7 * (message.arg1), 0);
                        handler.sendMessageDelayed(message, 150);
                    } else {
                        handler.sendEmptyMessage(5);
                    }
                    break;
                case 5:
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, currVolume, 0);
                    break;
            }
            return false;
        }
    });
    private MediaPlayer.OnPreparedListener mOnPreparedListener;
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        public void onPrepared(MediaPlayer mp) {
            // briefly show the mediacontroller
            mIsPrepared = true;
            if (mOnPreparedListener != null) {
                mOnPreparedListener.onPrepared(mMediaPlayer);
            }
        }
    };
    private OnErrorListener mOnErrorListener;
    private OnErrorListener mErrorListener = new OnErrorListener() {
        public boolean onError(MediaPlayer mp, int framework_err, int impl_err) {
            /* If an error handler has been supplied, use it and finish. */
            if (mOnErrorListener != null) {
                if (mOnErrorListener.onError(mMediaPlayer, framework_err,
                        impl_err)) {
                    return true;
                }
            }
            return true;
        }
    };
    private OnCompletionListener mOnCompletionListener;
    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        public void onCompletion(MediaPlayer mp) {
            if (mOnCompletionListener != null) {
                mOnCompletionListener.onCompletion(mMediaPlayer);
            }
        }
    };

    private MediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        public void onBufferingUpdate(MediaPlayer mp, int percent) {
            if (mOnBufferingUpdateListener != null) {
                mOnBufferingUpdateListener.onBufferingUpdate(mp, percent);
            }
            mCurrentBufferPercentage = percent;
        }
    };

    public StandardPlayer(Context context) {
        mContext = context;
        mMediaPlayer = new MediaPlayer();
        audioManager = (AudioManager) mContext.getSystemService(Service.AUDIO_SERVICE);
    }

    public void setVideoPath(String path) {
        setVideoURI(Uri.parse(path));
    }

    private void setVideoURI(Uri uri) {
        mUri = uri;
        openVideo();
    }

    public void stopPlayback() {
        if (mMediaPlayer != null) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }
    }

    private void openVideo() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
        } else {
            mMediaPlayer = new MediaPlayer();
        }
        try {
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mIsPrepared = false;
            mDuration = -1;
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mCurrentBufferPercentage = 0;
            mMediaPlayer.setDataSource(mContext, mUri);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Register a callback to be invoked when the media file is loaded and ready
     * to go.
     *
     * @param l The callback that will be run
     */
    public void setOnPreparedListener(MediaPlayer.OnPreparedListener l) {
        mOnPreparedListener = l;
    }

    /**
     * Register a callback to be invoked when the media file is loaded and ready
     * to go.
     *
     * @param l The callback that will be run
     */
    public void setOnBufferingUpdateListener(MediaPlayer.OnBufferingUpdateListener l) {
        mOnBufferingUpdateListener = l;
    }

    /**
     * Register a callback to be invoked when the end of a media file has been
     * reached during playback.
     *
     * @param l The callback that will be run
     */
    public void setOnCompletionListener(OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    /**
     * Register a callback to be invoked when an error occurs during playback or
     * setup. If no listener is specified, or if the listener returned false,
     * VideoView will inform the user of any errors.
     *
     * @param l The callback that will be run
     */
    public void setOnErrorListener(OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void start() {
        if (mIsPrepared) {
            mMediaPlayer.start();
        }
    }

    public void pause() {
        if (mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
        }
    }

    public void startDelay() {
        if (mIsPrepared) {
            handler.sendEmptyMessage(3);
        }
    }

    public void pauseDelay() {
        if (mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                handler.sendEmptyMessage(0);
            }
        }
    }

    public void reset() {
        if (mIsPrepared) {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            mMediaPlayer.reset();
        }
    }

    public int getDuration() {
        if (mIsPrepared) {
            mDuration = mMediaPlayer.getDuration();
            return mDuration;
        }
        mDuration = -1;
        return mDuration;
    }

    public int getCurrentPosition() {
        if (mIsPrepared) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int msec) {
        if (mIsPrepared) {
            mMediaPlayer.seekTo(msec);
        }
    }

    public boolean isPlaying() {
        return mIsPrepared && mMediaPlayer.isPlaying();
    }

    public boolean isPrepared() {
        return mIsPrepared;
    }

    public int getBufferPercentage() {
        return mCurrentBufferPercentage;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    @Override
    public boolean canSeekBackward() {
        return false;
    }

    @Override
    public boolean canSeekForward() {
        return false;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }
}
