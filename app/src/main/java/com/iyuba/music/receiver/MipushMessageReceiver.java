package com.iyuba.music.receiver;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.music.activity.NullActivity;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

/**
 * 1、PushMessageReceiver 是个抽象类，该类继承了 BroadcastReceiver。<br/>
 * 2、需要将自定义的 MipushMessageReceiver 注册在 AndroidManifest.xml 文件中：
 * <pre>
 * {@code
 *  <receiver
 *      android:name="com.xiaomi.mipushdemo.MipushMessageReceiver"
 *      android:exported="true">
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.RECEIVE_MESSAGE" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.MESSAGE_ARRIVED" />
 *      </intent-filter>
 *      <intent-filter>
 *          <action android:name="com.xiaomi.mipush.ERROR" />
 *      </intent-filter>
 *  </receiver>
 *  }</pre>
 * 3、MipushMessageReceiver 的 onReceivePassThroughMessage 方法用来接收服务器向客户端发送的透传消息。<br/>
 * 4、MipushMessageReceiver 的 onNotificationMessageClicked 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法会在用户手动点击通知后触发。<br/>
 * 5、MipushMessageReceiver 的 onNotificationMessageArrived 方法用来接收服务器向客户端发送的通知消息，
 * 这个回调方法是在通知消息到达客户端时触发。另外应用在前台时不弹出通知的通知消息到达客户端也会触发这个回调函数。<br/>
 * 6、MipushMessageReceiver 的 onCommandResult 方法用来接收客户端向服务器发送命令后的响应结果。<br/>
 * 7、MipushMessageReceiver 的 onReceiveRegisterResult 方法用来接收客户端向服务器发送注册命令后的响应结果。<br/>
 * 8、以上这些方法运行在非 UI 线程中。
 *
 * @author mayixiang
 */
public class MipushMessageReceiver extends PushMessageReceiver {
    public static final String TAG = "MipushMessageReceiver";

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        Log.d(TAG, "onReceivePassThroughMessage is called. " + message.toString());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.d(TAG, "onNotificationMessageClicked is called. " + message.toString());
        NullActivity.exePushData(context, message.getContent());
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.d(TAG, "onNotificationMessageArrived is called. " + message.toString());
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.d(TAG, "onCommandResult is called. " + message.toString());
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.d(TAG, "onReceiveRegisterResult is called. " + message.toString());
    }
}
