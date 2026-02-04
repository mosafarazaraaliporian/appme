# راهنمای راه‌اندازی پروژه

## ✅ کارهای انجام شده:

1. ✅ کپی فایل‌های Java از decompile
2. ✅ به‌روزرسانی AndroidManifest.xml
3. ✅ اضافه کردن Dependencies
4. ✅ تنظیم build.gradle.kts

## 📋 کارهای باقی‌مانده:

### 1. اضافه کردن google-services.json
- فایل `google-services.json` را از Firebase Console دانلود کنید
- آن را در `app/` قرار دهید

### 2. رفع Import Errors
فایل‌های decompiled دارای import های obfuscated هستند که باید جایگزین شوند:
- `p000A.*` → کتابخانه‌های واقعی
- `p110R4.*` → Kotlin stdlib
- `p046H.*` → Compose Runtime
- و غیره...

### 3. ساخت UI با Compose
- فایل `C1038e` در MainActivity باید با یک Composable function واقعی جایگزین شود
- UI را با Jetpack Compose بسازید

### 4. تنظیم Koin Modules
- Module های Koin را در `MainApplication` تنظیم کنید
- Repository ها و ViewModels را register کنید

### 5. تست و Debug
- خطاهای compile را رفع کنید
- تست کنید

## 📦 Dependencies اضافه شده:

- ✅ Jetpack Compose
- ✅ Firebase (Firestore, Messaging, Storage, Crashlytics)
- ✅ Koin (Dependency Injection)
- ✅ WorkManager
- ✅ Coroutines
- ✅ DataStore

## ⚠️ نکات مهم:

1. این فایل‌ها decompiled هستند و نیاز به refactor دارند
2. Import های obfuscated باید با کتابخانه‌های واقعی جایگزین شوند
3. UI باید از ابتدا با Compose نوشته شود
4. Firebase Project ID و API Key را در google-services.json تنظیم کنید
