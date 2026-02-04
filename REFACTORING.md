# بازنویسی منطق برنامه

## ✅ کارهای انجام شده:

### 1. Models (Data Classes)
- ✅ `SmsModel.kt` - مدل SMS با استفاده از Firebase Timestamp
- ✅ `UserModel.kt` - اطلاعات کاربر
- ✅ `DeviceModel.kt` - اطلاعات دستگاه
- ✅ `SmsForwardingConfig.kt` - تنظیمات فوروارد SMS
- ✅ `SimModel.kt` - اطلاعات SIM کارت
- ✅ `ForwardingModel.kt` - تنظیمات فوروارد تماس
- ✅ `SendSmsModel.kt` - مدل ارسال SMS

### 2. Repository Layer
- ✅ `FirestoreRepository.kt` - عملیات Firestore (ذخیره SMS، اطلاعات دستگاه)
- ✅ `SmsForwardingRepository.kt` - مدیریت تنظیمات فوروارد با DataStore

### 3. Utilities
- ✅ `SmsHelper.kt` - Helper برای عملیات SMS (خواندن، ارسال، دریافت Device ID)
- ✅ `SmsForwarder.kt` - کلاس فوروارد SMS

### 4. Workers
- ✅ `SmsUploadWorker.kt` - آپلود SMS به Firestore
- ✅ `RegisterUserWorker.kt` - ثبت اطلاعات دستگاه
- ✅ `UnifiedWatchdogWorker.kt` - اطمینان از اجرای UnifiedService

### 5. Services
- ✅ `UnifiedService.kt` - سرویس پس‌زمینه با Foreground Service
- ✅ `MyFirebaseMessagingService.kt` - مدیریت FCM

### 6. Broadcast Receivers
- ✅ `SmsReceiver.kt` - دریافت SMS و ارسال به Worker
- ✅ `BootReceiver.kt` - راه‌اندازی سرویس بعد از بوت

### 7. Main Components
- ✅ `MainActivity.kt` - Activity اصلی با Compose
- ✅ `MainApplication.kt` - Application class با Koin
- ✅ `appModule.kt` - Koin Dependency Injection Module
- ✅ `Theme.kt` - Material3 Theme

## 📋 ساختار جدید:

```
com.payload.jansiix0ne/
├── data/
│   ├── model/          # Data models
│   └── repository/      # Repository layer
├── util/               # Utility classes
├── worker/             # WorkManager workers
├── services/           # Background services
├── broadcast/          # Broadcast receivers
├── di/                 # Dependency Injection (Koin)
├── ui/
│   └── theme/          # Compose theme
├── MainActivity.kt
└── MainApplication.kt
```

## 🔄 تغییرات اصلی:

1. **از Java به Kotlin**: تمام کدها به Kotlin تبدیل شدند
2. **نام‌گذاری واضح**: نام‌های obfuscated با نام‌های واضح جایگزین شدند
3. **ساختار تمیز**: استفاده از Clean Architecture principles
4. **Coroutines**: استفاده از Kotlin Coroutines برای async operations
5. **DataStore**: استفاده از DataStore به جای SharedPreferences برای تنظیمات
6. **Material3**: استفاده از Material3 برای UI

## ⚠️ نکات مهم:

1. فایل `google-services.json` را از Firebase Console دانلود و در `app/` قرار دهید
2. تمام import های obfuscated با کتابخانه‌های واقعی جایگزین شدند
3. UI باید با Jetpack Compose ساخته شود (در MainActivity)
4. ممکن است نیاز به تنظیمات اضافی برای Firestore Security Rules باشد

## 🚀 مراحل بعدی:

1. اضافه کردن UI با Compose
2. تست کردن تمام قابلیت‌ها
3. اضافه کردن Error Handling بهتر
4. اضافه کردن Logging بهتر
5. تست روی دستگاه واقعی
