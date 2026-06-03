# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep @retrofit2.http.* interface * { <methods>; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Custom NetworkResponse Adapter
-keep class com.adsama.network.adapter.** { *; }
-keep class com.adsama.network.adapter.NetworkResponse { *; }
-keep class com.adsama.network.adapter.NetworkResponse$* { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, EnclosingMethod, InnerClasses, Signature
-keep @kotlinx.serialization.Serializable class * { *; }
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class * {
    public static ** Companion;
}
-keep class **$$serializer { *; }
-keepclassmembers class ** {
    *** serializer(...);
}
-keepclassmembers class ** {
    *** write$Self(...);
}
-keep class **.Descriptor { *; }
