plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.payload.jansiix0ne"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.payload.jansiix0ne"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // پشتیبانی از ARM32 و ARM64 (همه گوشی‌ها)
        ndk {
            abiFilters += listOf("armeabi-v7a", "arm64-v8a")
        }
        
        // فقط زبان انگلیسی (حذف ترجمه‌های اضافی)
        resourceConfigurations += listOf("en")
        
        // حذف density های اضافی
        vectorDrawables.useSupportLibrary = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            
            // بهینه‌سازی اضافی
            ndk {
                debugSymbolLevel = "NONE"
            }
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
    
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    
    kotlinOptions {
        jvmTarget = "11"
    }
    
    buildFeatures {
        compose = true
        // غیرفعال کردن ویژگی‌های غیرضروری
        buildConfig = false
        aidl = false
        renderScript = false
        resValues = false
        shaders = false
    }
    
    packaging {
        resources {
            excludes += setOf(
                "/META-INF/{AL2.0,LGPL2.1}",
                "/META-INF/*.kotlin_module",
                "**/kotlin/**",
                "**/*.txt",
                "**/*.version",
                "**/*.properties",
                "/kotlin/**",
                "META-INF/DEPENDENCIES",
                "META-INF/LICENSE",
                "META-INF/LICENSE.txt",
                "META-INF/license.txt",
                "META-INF/NOTICE",
                "META-INF/NOTICE.txt",
                "META-INF/notice.txt",
                "META-INF/ASL2.0",
                "META-INF/*.properties",
                "META-INF/*.version",
                "META-INF/proguard/*",
                "META-INF/com.android.tools/**",
                "META-INF/maven/**",
                "META-INF/services/**",
                "**/*.proto",
                "**/*.bin",
                "DebugProbesKt.bin",
                "kotlin-tooling-metadata.json"
            )
        }
        jniLibs {
            useLegacyPackaging = false
        }
        dex {
            useLegacyPackaging = false
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.appcompat)
    implementation(libs.material)
    
    // Jetpack Compose
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    debugImplementation(libs.compose.ui.tooling.preview)
    
    // Lifecycle
    implementation(libs.lifecycle.runtime.ktx)
    
    // Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    
    // WorkManager
    implementation(libs.work.runtime.ktx)
    
    // Coroutines
    implementation(libs.kotlinx.coroutines.android)
    
    // DataStore
    implementation(libs.datastore.preferences)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}