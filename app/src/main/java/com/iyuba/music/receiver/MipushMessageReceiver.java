package com.iyuba.music.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.music.activity.WelcomeActivity;
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
        Log.e(TAG, "onReceivePassThroughMessage is called. " + message.toString());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        Log.e(TAG, "onNotificationMessageClicked is called. " + message.toString());
        //exePushData(context, message);
    }

    private static void exePushData(String url) {
        Uri uri ;
        if (TextUtils.isEmpty(url)) {
             uri = Uri.parse("http://www.360.com");
        }else{
            uri = Uri.parse(url);
        }
        Intent intent = new Intent();
        if (uri.getScheme().equals("iyumusic")) {
            String path = uri.getPath();
            switch (uri.getHost()) {
                case "broadcastor":
                    switch (path) {
                        case "1":
                            System.out.print("主播1");
                            break;
                        case "2":
                            break;
                        case "3":
                            System.out.print("主播3");
                            break;
                    }
                    break;
                case "dailyNew":
                    System.out.print("每日最新");
                    break;
                case "song":
                    System.out.print("跳转歌曲");
                    break;
                case "account":
                    System.out.print("公众号");
                    break;
                case "downloadApk":
                    System.out.print("下载");
                    break;
            }
        } else {
            System.out.print("非法");
            //intent = new Intent(context, WelcomeActivity.class);
        }
//        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        context.startActivity(intent);
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        Log.e(TAG, "onNotificationMessageArrived is called. " + message.toString());
        Toast.makeText(context, "您有新的推送到达", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG, "onCommandResult is called. " + message.toString());
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        Log.e(TAG, "onReceiveRegisterResult is called. " + message.toString());
    }
}
