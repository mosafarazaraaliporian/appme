# SMS Forwarding App

برنامه Android برای دریافت، ذخیره و فوروارد SMS با استفاده از Firebase.

## ✅ وضعیت پروژه

پروژه آماده Build است! همه فایل‌های لازم در جای خود قرار دارند.

## 📋 ویژگی‌ها

- ✅ دریافت خودکار SMS
- ✅ ذخیره SMS در Firebase Firestore
- ✅ فوروارد SMS به شماره مشخص شده
- ✅ سرویس پس‌زمینه دائمی
- ✅ راه‌اندازی خودکار بعد از بوت
- ✅ دریافت پیام‌های FCM برای بیدار کردن دستگاه

## 🏗️ ساختار پروژه

```
myapp/
├── app/
│   ├── google-services.json ✅
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       └── java/com/payload/jansiix0ne/
│           ├── MainActivity.kt
│           ├── MainApplication.kt
│           ├── broadcast/      # SMS و Boot Receivers
│           ├── data/          # Models و Repositories
│           ├── di/            # Koin Dependency Injection
│           ├── services/      # Background Services
│           ├── ui/            # Compose UI
│           ├── util/          # Helper Classes
│           └── worker/        # WorkManager Workers
```

## 🚀 Build

```bash
# Clean
./gradlew clean

# Build Debug
./gradlew assembleDebug

# Build Release
./gradlew assembleRelease
```

## 📦 Dependencies

- Jetpack Compose
- Firebase (Firestore, Messaging, Storage, Crashlytics)
- Koin (Dependency Injection)
- WorkManager
- Coroutines
- DataStore

## ⚙️ تنظیمات

### Firebase
- ✅ `google-services.json` در `app/` قرار دارد
- ⚠️ Firestore Security Rules را در Firebase Console تنظیم کنید

### Permissions
همه permissions در runtime درخواست می‌شوند:
- READ_SMS
- SEND_SMS
- RECEIVE_SMS
- READ_PHONE_STATE
- CALL_PHONE
- POST_NOTIFICATIONS (Android 13+)

## 📝 نکات مهم

1. **Min SDK**: 28 (Android 9.0)
2. **Target SDK**: 36 (Android 16)
3. **Package Name**: `com.payload.jansiix0ne`
4. **Firebase Project**: `rtnew-d8023`

## ✅ چک‌لیست Build

- [x] همه فایل‌های Kotlin نوشته شده‌اند
- [x] `google-services.json` در `app/` قرار دارد
- [x] AndroidManifest درست است
- [x] build.gradle.kts درست است
- [x] همه dependencies تنظیم شده‌اند

**پروژه آماده Build است! 🎉**
