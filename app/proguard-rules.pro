# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Program Files\adt-bundle\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
#-optimizationpasses 5
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarning
-keepattributes Exceptions
-keepattributes Signature
-keepattributes EnclosingMethod
-keepattributes *Annotation*
-keepattributes InnerClasses
-keepattributes Deprecated
-keepattributes SourceFile
-keepattributes LineNumberTable

-assumenosideeffects class android.util.Log {
    public static *** v(...);
    public static *** i(...);
    public static *** d(...);
    public static *** w(...);
    public static *** e(...);
}
# 保持哪些类不被混淆
-keep class android.** {*; }
-keep public class * extends android.view
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.pm
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keepclasseswithmembernames class * {
    native <methods>;
}
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}
-keepclassmembers class **.R$* {
    public static <fields>;
}
# 保持自定义控件类不被混淆
 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet);
 }
 -keepclasseswithmembers class * {
     public <init>(android.content.Context, android.util.AttributeSet, int);
 }
#volley混淆配置
-dontwarn com.android.volley.jar.**
-keep class com.android.volley.**{*;}
#glide
-keep public class * implements  com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}
-keep class com.bumptech.** {
    *;
}

#mob sms
-keep class cn.smssdk.**{*;}
-keep class com.mob.**{*;}

-dontwarn com.mob.**
-dontwarn cn.smssdk.**
#webview
-keepattributes *JavascriptInterface*
-keepclassmembers  class  com.iyuba.music.activity.WebViewActivity$*{
    *;
}
#avloading
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

#alipay
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
#有道广告
-keep class com.youdao.sdk.** { *;}
#播放状态指示器
-keep class com.wnafee.vector.**{*;}
#科大讯飞评分
-keep class com.iflytek.**{*;}
#mipush
-keep class com.iyuba.music.receiver.MipushMessageReceiver {*;}
-dontwarn com.xiaomi.push.**
#umeng分享
-dontwarn android.webkit.WebView
-dontwarn com.umeng.**
-keep public class javax.**
-keep public class android.webkit.**
-dontwarn android.support.v4.**
-keep public interface com.tencent.**
-keep public interface com.umeng.socialize.**
-keep public interface com.umeng.socialize.sensor.**
-keep public interface com.umeng.scrshot.**
-keep class com.android.dingtalk.share.ddsharemodule.** { *; }
-keep public class com.umeng.socialize.* {*;}
-keep class com.umeng.scrshot.**
-keep public class com.tencent.** {*;}
-keep class com.umeng.socialize.sensor.**
-keep class com.umeng.socialize.handler.**
-keep class com.umeng.socialize.handler.*
-keep class com.umeng.weixin.handler.**
-keep class com.umeng.weixin.handler.*
-keep class com.umeng.qq.handler.**
-keep class com.umeng.qq.handler.*
-keep class UMMoreHandler{*;}
-keep class com.tencent.mm.sdk.modelmsg.WXMediaMessage {*;}
-keep class com.tencent.mm.sdk.modelmsg.** implements   com.tencent.mm.sdk.modelmsg.WXMediaMessage$IMediaObject {*;}
-keep class com.tencent.mm.sdk.** {
 *;
}
-keep class com.tencent.mm.opensdk.** {
 *;
}
-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.sina.** {*;}
-dontwarn com.sina.**

-keep class com.jesgoo.sdk.**{
     public protected *;
}
# 广场包注解
#-dontwarn retrofit2.**
#-keepclasseswithmembers class * {
#    @retrofit2.http.* <methods>;
#}
#
#-keep class okhttp3.** { *; }
#-keep interface okhttp3.** { *; }
#-dontwarn okhttp3.**
#
#-keep class javax.xml.stream.** { *; }



