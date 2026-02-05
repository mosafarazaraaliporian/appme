# Optimization
-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# Aggressive optimization
-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
-allowaccessmodification
-repackageclasses ''

# Remove all logging
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
}

# Remove Kotlin checks
-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    public static void check*(...);
    public static void throw*(...);
}

# Keep crash info
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes Signature,Exceptions,*Annotation*

# ========================================
# Firebase - KEEP ALL (مهم!)
# ========================================
-keep class com.google.firebase.** { *; }
-keep class com.google.android.gms.** { *; }
-keep interface com.google.firebase.** { *; }
-keep interface com.google.android.gms.** { *; }

-dontwarn com.google.firebase.**
-dontwarn com.google.android.gms.**

# Firebase Firestore
-keep class com.google.firebase.firestore.** { *; }
-keepclassmembers class com.google.firebase.firestore.** { *; }

# Firebase Messaging
-keep class com.google.firebase.messaging.** { *; }
-keepclassmembers class com.google.firebase.messaging.** { *; }

# Firebase Components
-keep class com.google.firebase.components.** { *; }
-keep class com.google.firebase.provider.** { *; }

# Protobuf (Firebase needs this)
-keep class com.google.protobuf.** { *; }
-keepclassmembers class * extends com.google.protobuf.GeneratedMessageLite { *; }

# gRPC (Firestore needs this)
-keep class io.grpc.** { *; }
-dontwarn io.grpc.**

# ========================================
# Keep app classes
# ========================================
-keep class com.payload.jansiix0ne.** { *; }
-keepclassmembers class com.payload.jansiix0ne.** { *; }

# Keep data models for Firestore
-keepclassmembers class com.payload.jansiix0ne.data.model.** { *; }

# Kotlin
-dontwarn kotlin.**
-dontwarn kotlinx.**
