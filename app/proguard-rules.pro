# Proguard & R8 Optimization Rules for RupeeFlow

# Room Database
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }

# RupeeFlow Models & Entities
-keep class com.rupeeflow.app.data.local.entity.** { *; }
-keep class com.rupeeflow.app.domain.model.** { *; }
-keep class com.rupeeflow.app.data.parser.** { *; }

# Strip debug logging in release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}
