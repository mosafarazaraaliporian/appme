# راهنمای استفاده از APK Analyzer

## 🔍 APK Analyzer چیست؟
ابزاری در Android Studio برای تحلیل ساختار و حجم APK و پیدا کردن فایل‌های بزرگ.

## 📋 روش‌های استفاده:

### روش 1: از Android Studio (ساده‌ترین)

1. **Build APK:**
   ```bash
   ./gradlew assembleDebug
   # یا
   ./gradlew assembleRelease
   ```

2. **باز کردن APK Analyzer:**
   - در Android Studio: `Build` → `Analyze APK...`
   - یا `Ctrl+Shift+A` (Windows/Linux) / `Cmd+Shift+A` (Mac)
   - تایپ کنید: "Analyze APK"
   - فایل APK را انتخاب کنید: `app/build/outputs/apk/debug/app-debug.apk`

3. **تحلیل:**
   - حجم هر فایل/پوشه را می‌بینید
   - بزرگترین فایل‌ها در بالا هستند
   - می‌توانید مقایسه کنید با APK اصلی

### روش 2: از Command Line (سریع‌تر)

```bash
# نصب bundletool (اگر ندارید)
# یا استفاده از aapt2 که در Android SDK است

# پیدا کردن مسیر aapt2
# Windows: %LOCALAPPDATA%\Android\Sdk\build-tools\[version]\aapt2.exe
# Linux/Mac: $ANDROID_HOME/build-tools/[version]/aapt2

# تحلیل APK
aapt2 dump badging app/build/outputs/apk/debug/app-debug.apk

# یا استفاده از unzip برای دیدن محتویات
unzip -l app/build/outputs/apk/debug/app-debug.apk | sort -k1 -rn | head -20
```

### روش 3: استفاده از Python Script (خودکار)

می‌توانید یک script بنویسید که:
- APK را extract کند
- حجم هر فایل را محاسبه کند
- بزرگترین فایل‌ها را نشان دهد

## 📊 چه چیزهایی را بررسی کنیم:

### 1. DEX Files (کد)
- `classes.dex`, `classes2.dex`, ...
- باید کوچک باشند (با minify)
- اگر بزرگ است: dependencies اضافی داریم

### 2. Resources
- `res/` - تصاویر، layouts، strings
- `resources.arsc` - compiled resources
- اگر بزرگ است: تصاویر یا زبان‌های اضافی

### 3. Native Libraries
- `lib/` - native libraries (.so files)
- معمولاً بزرگ هستند
- بررسی کنید آیا همه لازم هستند

### 4. Assets
- `assets/` - فایل‌های اضافی
- اگر وجود دارد، بررسی کنید

### 5. META-INF
- باید کوچک باشد
- اگر بزرگ است: فایل‌های اضافی داریم

## 🎯 مثال استفاده:

```bash
# 1. Build APK
./gradlew assembleRelease

# 2. Extract و بررسی
cd app/build/outputs/apk/release
unzip -q app-release.apk -d apk_contents
cd apk_contents

# 3. پیدا کردن بزرگترین فایل‌ها
find . -type f -exec du -h {} + | sort -rh | head -20

# 4. بررسی DEX files
ls -lh classes*.dex

# 5. بررسی native libraries
du -sh lib/*

# 6. بررسی resources
du -sh res/*
```

## 💡 نکات مهم:

1. **مقایسه با APK اصلی:**
   - هر دو را در APK Analyzer باز کنید
   - تفاوت‌ها را ببینید

2. **بزرگترین فایل‌ها:**
   - معمولاً: DEX files، native libraries، resources
   - اگر DEX بزرگ است: dependencies اضافی
   - اگر resources بزرگ است: تصاویر یا زبان‌های اضافی

3. **بهینه‌سازی:**
   - بعد از پیدا کردن فایل‌های بزرگ، آنها را بهینه کنید
   - Dependencies اضافی را حذف کنید
   - تصاویر را فشرده کنید
   - زبان‌های اضافی را حذف کنید

## 🔧 Script خودکار:

می‌توانید یک script بنویسید که این کارها را خودکار انجام دهد.
