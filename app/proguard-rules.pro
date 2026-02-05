# Maximum optimization
-optimizationpasses 7
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose
-dontpreverify

# Aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification
-repackageclasses ''
-mergeinterfacesaggressively

# Remove all logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
    public static *** wtf(...);
    public static *** println(...);
}

# Remove Kotlin checks
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}

# Remove debug code
-assumenosideeffects class * {
    public void debug(...);
    public void verbose(...);
    public void trace(...);
}

# Keep crash info only
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Minimal attributes
-keepattributes Signature,Exceptions,*Annotation*

# Firebase minimal
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Keep app classes
-keep class com.payload.jansiix0ne.** { *; }

# Remove unused Kotlin metadata
-dontwarn kotlin.**
-dontwarn kotlinx.**

# Obfuscate everything else
-obfuscationdictionary proguard-dict.txt
-classobfuscationdictionary proguard-dict.txt
-packageobfuscationdictionary proguard-dict.txt
