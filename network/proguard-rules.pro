# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keep @retrofit2.http.* interface * { <methods>; }
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }

# OkHttp
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }

# Kotlinx Serialization
-keepattributes *Annotation*, EnclosingMethod, InnerClasses
-keepclassmembers class ** {
    @kotlinx.serialization.Serializable <fields>;
}
-keepclassmembers class ** {
    public static ** Companion;
}
-keep class **$$serializer { *; }

# Keep DTOs in model module
-keep class com.adsama.model.** { *; }

# Keep the network service interface
-keep interface com.adsama.network.WeatherService { *; }

# Custom NetworkResponse Adapter
-keep class com.adsama.network.adapter.** { *; }
-keep class com.adsama.network.adapter.NetworkResponse { *; }
-keep class com.adsama.network.adapter.NetworkResponse$* { *; }
