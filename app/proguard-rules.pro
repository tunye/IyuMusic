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
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarning
-keepattributes Signature
-keepattributes EnclosingMethod
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

#gson混淆配置
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.examples.android.model.** { *; }
-keep class com.google.gson.JsonObject { *; }
#友盟混淆配置
-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keep public class com.iyuba.music.R$*{
public static final int *;
}
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**

-keepattributes *Annotation*

-keep class com.taobao.** {*;}
-keep class org.android.** {*;}
-keep class anet.channel.** {*;}
-keep class com.umeng.** {*;}
-keep class com.xiaomi.** {*;}
-keep class com.huawei.** {*;}
-keep class org.apache.thrift.** {*;}

-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}

-keep public class **.R$*{
   public static final int *;
}
-keep public class android.webkit.**
-keep public class javax.**
#mob sms
-keep class android.net.http.SslError
-keep class android.webkit.**{*;}
-keep class cn.sharesdk.**{*;}
-keep class cn.smssdk.**{*;}
-keepattributes InnerClasses,LineNumberTable
-keep class **.R$* {
    *;
}
-keep class com.mob.**{*;}
#webview
-keepattributes *JavascriptInterface*
-keepclassmembers  class  com.iyuba.music.activity.WebViewActivity$*{
    *;
}
#avloading
-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }

-keep class com.tencent.open.TDialog$*
-keep class com.tencent.open.TDialog$* {*;}
-keep class com.tencent.open.PKDialog
-keep class com.tencent.open.PKDialog {*;}
-keep class com.tencent.open.PKDialog$*
-keep class com.tencent.open.PKDialog$* {*;}
-keep class com.tencent.** {*;}
-dontwarn com.tencent.**
-keep class com.sina.** {*;}
-dontwarn com.sina.**



