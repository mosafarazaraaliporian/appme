# TODO List

## فوری (High Priority)

- [ ] تست کامل اپلیکیشن روی دستگاه واقعی
- [ ] بررسی و تست تمام permissions
- [ ] تست Firebase connectivity
- [ ] تست SMS sending/receiving
- [ ] بررسی WorkManager tasks

## متوسط (Medium Priority)

- [ ] پیاده‌سازی UI کامل با Compose
- [ ] اضافه کردن error handling بهتر
- [ ] پیاده‌سازی logging system
- [ ] اضافه کردن unit tests
- [ ] بهینه‌سازی battery usage
- [ ] پیاده‌سازی retry mechanism برای network calls

## کم‌اهمیت (Low Priority)

- [ ] اضافه کردن analytics
- [ ] پیاده‌سازی crash reporting
- [ ] بهبود UI/UX
- [ ] اضافه کردن dark mode
- [ ] پیاده‌سازی settings screen
- [ ] اضافه کردن localization

## بهبودها (Improvements)

- [ ] استفاده از Dependency Injection (Koin/Hilt)
- [ ] پیاده‌سازی Clean Architecture کامل
- [ ] اضافه کردن ViewModel ها
- [ ] استفاده از StateFlow/SharedFlow
- [ ] پیاده‌سازی offline-first approach
- [ ] اضافه کردن encryption برای sensitive data

## امنیت (Security)

- [ ] بررسی و رفع مشکلات امنیتی
- [ ] اضافه کردن ProGuard rules
- [ ] Obfuscation برای release build
- [ ] بررسی permissions و کاهش آن‌ها در صورت امکان
- [ ] پیاده‌سازی secure storage برای sensitive data

## Documentation

- [ ] نوشتن KDoc برای تمام کلاس‌ها
- [ ] ایجاد API documentation
- [ ] نوشتن راهنمای استفاده
- [ ] ایجاد architecture diagram

## Testing

- [ ] نوشتن Unit Tests
- [ ] نوشتن Integration Tests
- [ ] نوشتن UI Tests
- [ ] تست روی دستگاه‌های مختلف
- [ ] تست روی نسخه‌های مختلف Android

## Performance

- [ ] بهینه‌سازی Firestore queries
- [ ] کاهش memory usage
- [ ] بهینه‌سازی battery consumption
- [ ] بررسی و رفع memory leaks
- [ ] بهینه‌سازی startup time

## نکات مهم

### قبل از Release
1. تست کامل روی دستگاه واقعی
2. بررسی تمام permissions
3. تست Firebase integration
4. بررسی ProGuard rules
5. تست روی Android versions مختلف (28-36)

### Firebase Setup
1. ایجاد پروژه Firebase
2. فعال‌سازی Firestore
3. فعال‌سازی Firebase Messaging
4. تنظیم Security Rules
5. دانلود google-services.json

### Build & Release
1. تنظیم signing config
2. ایجاد release keystore
3. تنظیم ProGuard
4. Build release APK/AAB
5. تست release build
