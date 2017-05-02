##---------------Begin: proguard configuration common for all Android apps ----------
-keepattributes *Annotation*

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

-keepclassmembers class fqcn.of.javascript.interface.for.webview {
   public *;
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

-dontwarn android.support.**

-optimizationpasses 5
-optimizations !class/unboxing/enum
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-ignorewarnings
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-keep public class * extends android.app.Activity
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService

-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
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

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

##---------------End: proguard configuration common for all Android apps ----------


##---------------Begin: proguard configuration for Gson  ----------
# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*

-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** {*;}
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.annotations.** { *; }
-keep class com.google.gson.internal.** { *; }
-keep class com.google.gson.reflect.** { *; }

-keep class com.google.gson.examples.android.model.** { *; }

##---------------End: proguard configuration for Gson  ----------


##---------------Begin: proguard configuration for library  ----------
-keep class android.support.** {*;}
-keep class android.net.** {*;}
-keep class org.apache.http.** {*;}
-keep class org.json.** {*;}

#keep gaode map
-keep class com.amap.**{*;}
-keep class com.loc.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}
#sina
-keep class com.sina.** {*; }
-keep class com.sina.sso.** {*; }
-keep class com.sina.weibo.sdk.** {*; }
-keep class com.sina.weibo.sdk.api.** {*; }
-keep class com.sina.weibo.sdk.api.share.** {*; }
-keep class com.sina.weibo.sdk.auth.** {*; }
-keep class com.sina.weibo.sdk.auth.sso.** {*; }
-keep class com.sina.weibo.sdk.constant.** {*; }
-keep class com.sina.weibo.sdk.exception.** {*; }
-keep class com.sina.weibo.sdk.net.** {*; }
-keep class com.sina.weibo.sdk.utils.** {*; }
-keep class com.sina.weibo.sdk..** {*; }
# keep wechat
-keep class com.tencent.** {*; }
-keep class com.tencent.stat.** {*; }
-keep class com.tencent.stat.common.** {*; }
-keep class com.tencent.mm.** {*; }
-keep class com.tencent.mm.sdk.openapi.WXMediaMessage {*; }
-keep class com.tencent.mm.sdk.openapi.** implements com.tencent.mm.sdk.openapi.WXMediaMessage$IMediaObject {*; }
# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    <init>(java.lang.Throwable);
}

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }
-keepclasseswithmembernames class * {
    @butterknife.InjectView <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.InjectViews <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.OnClick <methods>;
    @butterknife.OnEditorAction <methods>;
    @butterknife.OnItemClick <methods>;
    @butterknife.OnItemLongClick <methods>;
    @butterknife.OnLongClick <methods>;
}
##---------------End: proguard configuration for library  ----------
-dontwarn java.lang.invoke.*
##----------------个推------------------------------------------
-dontwarn com.igexin.**
-dontwarn android.support.**
-keep class com.igexin.**{*;}
-keep class android.support.v4.** { *; }
-keep interface android.support.v4.app.** { *; }
##---------------RxJava  ----------
-keep class rx.** {*;}
-keep class rx.android.** {*;}
-keep class rx.android.plugins.** {*;}
-keep class rx.android.schedulers.** {*;}
-keep class com.jakewharton.rxbinding.** {*;}
-keep class com.jakewharton.rxbinding.internal.** {*;}
-keep class com.jakewharton.rxbinding.view.** {*;}
-keep class com.jakewharton.rxbinding.widget.** {*;}
-keep class com.trello.rxlifecycle.** {*;}
-keep class com.trello.rxlifecycle.android.** {*;}
-keep class com.trello.rxlifecycle.internal.** {*;}
-keep class com.trello.rxlifecycle.components.** {*;}
-keep class com.trello.rxlifecycle.components.support.** {*;}
-keep class com.tbruyelle.rxpermissions.** {*;}
##---------------Begin: proguard configuration for app  ----------
-keep class com.jaython.cc.ui.** {*;}
-keep class com.jaython.cc.ui.adapter.** {*;}
-keep class com.jaython.cc.ui.fragment.** {*;}
-keep class com.jaython.cc.ui.view.** {*;}
-keep class com.jaython.cc.ui.view.compat.** {*;}
-keep class com.jaython.cc.ui.view.divider.** {*;}
-keep class com.jaython.cc.ui.view.spannable.** {*;}
-keep class com.jaython.cc.ui.view.transformer.** {*;}
-keep class com.jaython.cc.ui.widget.** {*;}
-keep class com.jaython.cc.data.** {*;}
-keep class com.jaython.cc.data.cache.** {*;}
-keep class com.jaython.cc.data.constants.** {*;}
-keep class com.jaython.cc.data.db.** {*;}
-keep class com.jaython.cc.data.event.** {*;}
-keep class com.jaython.cc.data.manager.** {*;}
-keep class com.jaython.cc.data.model.** {*;}
-keep class com.jaython.cc.data.pool.** {*;}
-keep class com.jaython.cc.bean.parser.** {*;}
-keep class com.jaython.cc.bean.share.** {*;}
-keep class com.jaython.cc.bean.** {*;}
-keep class com.jaython.cc.data.transformer.** {*;}
-keep class com.jaython.cc.utils.** {*;}
-keep class com.jaython.cc.utils.handler.** {*;}
-keep class com.jaython.cc.utils.helper.** {*;}
-keep class com.jaython.cc.BuildConfig {*;}
-keep public class com.jaython.cc.R$*{
    public static final int *;
}
-keep class com.tiny.loader.TinyImageLoader {*;}
-keep class com.tiny.volley.** {*;}
-keep class com.tiny.volley.bean.** {*;}
-keep class com.tiny.volley.core.** {*;}
-keep class com.tiny.volley.core.cache.** {*;}
-keep class com.tiny.volley.core.exception.** {*;}
-keep class com.tiny.volley.core.network.** {*;}
-keep class com.tiny.volley.core.pool.** {*;}
-keep class com.tiny.volley.core.request.** {*;}
-keep class com.tiny.volley.core.response.** {*;}
-keep class com.tiny.volley.core.ssl.** {*;}
-keep class com.tiny.volley.core.stack.** {*;}
-keep class com.tiny.volley.download.** {*;}
-keep class com.tiny.volley.utils.** {*;}
-keep class com.tiny.volley.log.** {*;}
-keep class com.buihha.audiorecorder.** {*;}

#----------------umeng data-------------------------------------
-keepclassmembers class * {
    public <init> (org.json.JSONObject);
}
-keep class com.umeng.analytics.** {*;}
-keep class com.umeng.analytics.game.** {*;}
-keep class com.umeng.analytics.social.** {*;}
-keep class u.aly.** {*;}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class android.support.design.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep class android.support.transition.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
#----------------butter knife-------------------------------------
-keep class butterknife.** { *; }
-keep class butterknife.internal.** { *; }
#----------------qiniu-------------------------------------
-keep class com.qiniu.** { *; }
-keep class com.qiniu.common.** { *; }
-keep class com.qiniu.http.** { *; }
-keep class com.qiniu.processing.** { *; }
-keep class com.qiniu.storage.** { *; }
-keep class com.qiniu.util.** { *; }
-keep class qiniu.happydns.** { *; }
-keep class qiniu.happydns.http.** { *; }
-keep class qiniu.happydns.local.** { *; }
-keep class qiniu.happydns.util.** { *; }
-keep class okhttp3.** { *; }
-keep class okhttp3.internal.** { *; }
#----------------photoView-------------------------------------
-keep class uk.co.senab.photoview.** { *; }
-keep class uk.co.senab.photoview.gestures.** { *; }
-keep class uk.co.senab.photoview.log.** { *; }
-keep class uk.co.senab.photoview.scrollerproxy.** { *; }
#----------------Gelu-------------------------------------
-dontwarn java.lang.invoke.*
-keep class com.jerthon.gelu.sdk.Gelu {*;}
-keep class com.jerthon.gelu.sdk.utils.DesUtil {*;}
-keep class com.jerthon.gelu.sdk.utils.NetworkUtil {*;}
-keep class com.jerthon.gelu.sdk.utils.EncodeUtil {*;}
-keep class com.jerthon.gelu.sdk.utils.RandomUtil {*;}


