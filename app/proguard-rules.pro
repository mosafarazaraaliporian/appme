# Optimization flags
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Remove all logging in release
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Keep crash reporting info
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Keep only essential attributes
-keepattributes *Annotation*
-keepattributes Signature
-keepattributes Exceptions

# Firebase - keep only essentials
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Kotlin - aggressive optimization
-dontwarn kotlin.**
-dontwarn kotlinx.**
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}

# Keep app classes
-keep class com.payload.jansiix0ne.** { *; }

# Remove debug and verbose code
-assumenosideeffects class * {
    public void debug(...);
    public void verbose(...);
}

# Optimize
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification
-repackageclasses ''
