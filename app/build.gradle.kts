plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.google.services)
    alias(libs.plugins.crashlytics)
}

android {
    namespace = "com.payload.jansiix0ne"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.payload.jansiix0ne"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        // فقط زبان‌های مورد نیاز (کاهش حجم)
        resourceConfigurations += listOf("en", "fa")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            versionNameSuffix = "-debug"
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
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/DEPENDENCIES"
            excludes += "/META-INF/LICENSE"
            excludes += "/META-INF/LICENSE.txt"
            excludes += "/META-INF/license.txt"
            excludes += "/META-INF/NOTICE"
            excludes += "/META-INF/NOTICE.txt"
            excludes += "/META-INF/notice.txt"
            excludes += "/META-INF/ASL2.0"
            excludes += "/META-INF/*.kotlin_module"
            excludes += "/META-INF/kotlinx_coroutines_core.version"
            excludes += "/META-INF/versions/9/previous-compilation-data.bin"
            excludes += "/kotlin/**"
            excludes += "/okhttp3/**"
        }
    }
    
}

dependencies {
    // Core Android (minimal)
    implementation(libs.appcompat)
    
    // Jetpack Compose (minimal - فقط essentials)
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui)
    implementation(libs.compose.material3)
    implementation(libs.activity.compose)
    // حذف tooling preview در release
    debugImplementation(libs.compose.ui.tooling.preview)
    
    // Lifecycle (minimal)
    implementation(libs.lifecycle.runtime.ktx)
    
    // Koin حذف شد - استفاده نمی‌شود
    
    // Firebase (فقط essentials)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    // حذف Storage و Crashlytics اگر استفاده نمی‌شود
    // implementation(libs.firebase.storage)
    // implementation(libs.firebase.crashlytics)
    
    // WorkManager
    implementation(libs.work.runtime.ktx)
    
    // Coroutines (minimal)
    implementation(libs.kotlinx.coroutines.android)
    
    // DataStore
    implementation(libs.datastore.preferences)
    
    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}