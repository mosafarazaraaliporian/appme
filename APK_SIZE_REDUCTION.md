# کاهش حجم APK - راه‌حل‌های اعمال شده

## 🔍 مشکل:
APK اصلی: ~5MB  
APK ما: ~15MB (3 برابر!)

## ✅ راه‌حل‌های اعمال شده:

### 1. حذف Dependencies غیرضروری
- ❌ حذف `material` (استفاده نمی‌شود)
- ❌ حذف `lifecycle-viewmodel-compose` (ViewModel نداریم)
- ❌ حذف `koin-androidx-compose` (فقط در Activity استفاده می‌شود)
- ❌ حذف `firebase-storage` (استفاده نمی‌شود)
- ❌ حذف `firebase-crashlytics` (اختیاری)

### 2. محدود کردن زبان‌ها
```kotlin
resourceConfigurations += listOf("en", "fa")
```
- فقط انگلیسی و فارسی
- حذف تمام زبان‌های دیگر از AppCompat و libraries

### 3. بهینه‌سازی Compose
- حذف dynamic color (نیاز به API 31+)
- استفاده از static color schemes
- حذف imports اضافی

### 4. بهینه‌سازی ProGuard
- Keep rules دقیق‌تر
- حذف Log statements
- بهینه‌سازی Kotlin

### 5. Debug Build
- Minify غیرفعال برای debug (سریع‌تر)
- فقط Release minify می‌شود

## 📊 نتیجه مورد انتظار:
- حجم Release APK: ~6-8MB (کاهش 50-60%)
- حجم Debug APK: ~10-12MB (کاهش 20-30%)

## 🔧 اگر هنوز بزرگ است:
1. استفاده از App Bundle به جای APK
2. حذف کامل Compose و استفاده از View System
3. استفاده از Firebase Lite
4. بررسی APK Analyzer برای پیدا کردن فایل‌های بزرگ
