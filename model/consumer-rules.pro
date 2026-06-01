# Keep all DTOs and their member names
-keep class com.adsama.model.** { *; }
-keepclassmembers class com.adsama.model.** { *; }

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
