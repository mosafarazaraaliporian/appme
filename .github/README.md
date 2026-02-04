# GitHub Actions Workflows

این پروژه شامل دو workflow برای build خودکار است:

## 1. Build Workflow (`build.yml`)

این workflow در هر push یا pull request اجرا می‌شود و:
- ✅ پروژه را build می‌کند (Debug و Release)
- ✅ APK های build شده را به عنوان artifact آپلود می‌کند
- ✅ در صورت خطا، لاگ‌ها را آپلود می‌کند

### نحوه استفاده:
1. به Actions tab در GitHub بروید
2. workflow "Build Android App" را انتخاب کنید
3. APK های build شده را از بخش Artifacts دانلود کنید

## 2. Build and Release Workflow (`build-and-release.yml`)

این workflow برای release های رسمی استفاده می‌شود:
- ✅ Release APK را build می‌کند
- ✅ APK را sign می‌کند (اگر keystore تنظیم شده باشد)
- ✅ APK را به GitHub Release اضافه می‌کند

### تنظیمات لازم برای Sign کردن:

در GitHub Repository Settings > Secrets اضافه کنید:
- `KEYSTORE_BASE64`: Keystore file به صورت base64
- `KEYSTORE_PASSWORD`: رمز keystore
- `KEY_PASSWORD`: رمز key
- `KEY_ALIAS`: نام alias

### نحوه استفاده:
1. یک Release جدید در GitHub ایجاد کنید
2. یا از Actions > Build and Release > Run workflow استفاده کنید

## 📦 Artifacts

APK های build شده به مدت 30 روز (build) یا 90 روز (release) در دسترس هستند.
