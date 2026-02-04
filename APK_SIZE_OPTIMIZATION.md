# بهینه‌سازی حجم APK

## 🔍 دلایل حجم بیشتر APK ما:

### 1. **Minify/R8 غیرفعال است** ⚠️
در `build.gradle.kts`:
```kotlin
isMinifyEnabled = false  // ❌ این باعث حجم بیشتر می‌شود
```

### 2. **Dependencies اضافی**
ما dependencies بیشتری اضافه کرده‌ایم:
- Jetpack Compose کامل (BOM)
- Koin (Dependency Injection)
- Lifecycle کامل
- DataStore
- WorkManager

### 3. **Debug Build**
Debug builds حجم بیشتری دارند (شامل debug symbols)

### 4. **Kotlin Runtime**
Kotlin stdlib و runtime اضافه می‌شود

## ✅ راه‌حل‌ها:

### 1. فعال کردن Minify و R8
### 2. استفاده از ProGuard rules
### 3. حذف dependencies غیرضروری
### 4. استفاده از Split APKs
### 5. بهینه‌سازی Resources
