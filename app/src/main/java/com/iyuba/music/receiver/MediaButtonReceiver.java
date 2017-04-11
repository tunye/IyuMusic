package com.iyuba.music.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.iyuba.music.listener.OnHeadSetListener;
import com.iyuba.music.util.HeadSetUtil;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by 10202 on 2017-04-11.
 */

public class MediaButtonReceiver extends BroadcastReceiver {
    private MyHandler myHandler;
    private Timer timer = null;
    public OnHeadSetListener headSetListener = null;
    private static MTask myTimer = null;
    private static int clickCount;

    public MediaButtonReceiver() {
        this.headSetListener = HeadSetUtil.getInstance().getOnHeadSetListener();
        timer = new Timer(true);
        myHandler = new MyHandler(this);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) return;  // 判断是不是耳机按键事件。
        KeyEvent event = intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);  // 判断有没有耳机按键事件
        if (event == null) return;
        //过滤按下事件
        if (event.getAction() != KeyEvent.ACTION_UP) return;
        if (event.getKeyCode() == KeyEvent.KEYCODE_HEADSETHOOK) {
            if (clickCount == 0) {                              // 单击
                clickCount++;
                myTimer = new MTask();
                timer.schedule(myTimer, 500);
            } else if (clickCount == 1) {                       // 双击
                clickCount++;
            } else if (clickCount == 2) {                       // 三连击
                clickCount = 0;
                myTimer.cancel();
                headSetListener.onThreeClick();
            }
        }
        //终止广播(不让别的程序收到此广播，免受干扰)
        abortBroadcast();
    }

    private class MTask extends TimerTask {
        @Override
        public void run() {
            if (clickCount == 1) {
                myHandler.sendEmptyMessage(1);
            } else if (clickCount == 2) {
                myHandler.sendEmptyMessage(2);
            }
            clickCount = 0;
        }
    }

    private static class MyHandler extends Handler {
        WeakReference<MediaButtonReceiver> mReceiver;

        MyHandler(MediaButtonReceiver receiver) {
            mReceiver = new WeakReference<>(receiver);
        }

        @Override
        public void handleMessage(Message msg) {
            MediaButtonReceiver receiver = mReceiver.get();
            switch (msg.what) {
                case 1:
                    receiver.headSetListener.onClick();
                    break;
                case 2:
                    receiver.headSetListener.onDoubleClick();
                    break;
                case 3:
                    receiver.headSetListener.onThreeClick();
                    break;
            }
        }
    }
}
