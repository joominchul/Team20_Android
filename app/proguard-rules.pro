# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Kakao SDK
-keep class com.kakao.** { *; }
-keep class com.kakao.sdk.** { *; }
-dontwarn com.kakao.**
-dontwarn com.kakao.sdk.**
-keep class retrofit2.** { *; }
-keep class okhttp3.** { *; }

# Hilt
-keep class dagger.hilt.internal.** { *; }
-keep class dagger.hilt.android.internal.lifecycle.** { *; }
-keep class androidx.hilt.** { *; }
-dontwarn dagger.hilt.**
-dontwarn dagger.hilt.android.**
-dontwarn androidx.hilt.**
-keepattributes RuntimeVisibleAnnotations
-keep class * extends dagger.hilt.internal.GeneratedComponent

# Google Play Services
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

# Facebook Shimmer
-keep class com.facebook.shimmer.** { *; }
-dontwarn com.facebook.shimmer.**

# AndroidX 및 Jetpack
-keep public class androidx.** { *; }
-dontwarn androidx.**

# Gson 관련 클래스 보존
-keep class com.google.gson.** { *; }
-keep class retrofit2.** { *; }

# Gson 관련 규칙
-keep class com.google.gson.** { *; }
-keep class com.example.potatoservice.model.remote.** { *; }
-keep class com.example.potatoservice.model.** { *; }

# Gson의 TypeAdapter를 사용하는 경우 해당 클래스도 난독화되지 않도록 해야 함
-keep class com.example.potatoservice.model.DoubleToIntAdapter { *; }

# 모든 Gson에서 직렬화 및 역직렬화되는 클래스들에 대해 필드 이름이 변경되지 않도록
-keepnames class * {
    @com.google.gson.annotations.SerializedName <fields>;
}



# Suppress warnings for missing classes during R8 minification
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

