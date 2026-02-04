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
    }
    

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = false
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
        }
    }
    
}

dependencies {
    // Core Android (minimal)
    implementation(libs.appcompat)
    
    // Material Components (برای theme)
    implementation(libs.material)
    
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