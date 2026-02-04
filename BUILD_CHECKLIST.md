# ✅ چک‌لیست Build

## مشکلات رفع شده:

1. ✅ AndroidManifest: category از INFO به LAUNCHER تغییر یافت
2. ✅ SmsForwarder: imports اضافی حذف شد
3. ✅ همه فایل‌های Java قدیمی پاک شدند
4. ✅ همه imports درست هستند
5. ✅ Package names درست هستند

## ⚠️ قبل از Build:

### 1. فایل google-services.json
- [x] فایل `google-services.json` در `app/` قرار دارد ✅
- [x] Package name درست است: `com.payload.jansiix0ne` ✅

### 2. Firebase Setup
- [ ] Firebase Project ایجاد شده باشد
- [ ] Firestore Database فعال باشد
- [ ] Firebase Cloud Messaging فعال باشد

### 3. Dependencies
همه dependencies در `libs.versions.toml` و `build.gradle.kts` تنظیم شده‌اند ✅

### 4. AndroidManifest
- ✅ همه permissions اضافه شده‌اند
- ✅ همه services و receivers ثبت شده‌اند
- ✅ MainActivity به عنوان LAUNCHER تنظیم شده

## 📋 ساختار پروژه:

```
myapp/
├── app/
│   ├── build.gradle.kts ✅
│   ├── google-services.json ⚠️ (نیاز به اضافه کردن)
│   └── src/main/
│       ├── AndroidManifest.xml ✅
│       └── java/com/payload/jansiix0ne/
│           ├── MainActivity.kt ✅
│           ├── MainApplication.kt ✅
│           ├── broadcast/ ✅
│           ├── data/ ✅
│           ├── di/ ✅
│           ├── services/ ✅
│           ├── ui/ ✅
│           ├── util/ ✅
│           └── worker/ ✅
├── build.gradle.kts ✅
└── gradle/libs.versions.toml ✅
```

## 🚀 Build Commands:

```bash
# Clean build
./gradlew clean

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease
```

## ⚠️ نکات مهم:

1. **google-services.json**: بدون این فایل، Firebase کار نمی‌کند
2. **Firestore Rules**: باید Security Rules را در Firebase Console تنظیم کنید
3. **Permissions**: همه permissions در runtime درخواست می‌شوند
4. **Min SDK**: 28 (Android 9.0)
5. **Target SDK**: 36 (Android 16)

## ✅ وضعیت فعلی:

- ✅ همه فایل‌های Kotlin نوشته شده‌اند
- ✅ همه imports درست هستند
- ✅ AndroidManifest درست است
- ✅ build.gradle.kts درست است
- ⚠️ نیاز به google-services.json

**پروژه آماده Build است (بعد از اضافه کردن google-services.json)**
