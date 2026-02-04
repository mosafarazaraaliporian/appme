# توضیح GitHub Actions Workflows

## 📋 دو Workflow داریم:

### 1. `build.yml` - Build معمولی
**زمان اجرا:**
- در هر push به `main` یا `master`
- در هر pull request
- به صورت دستی (workflow_dispatch)

**کارها:**
- Build Debug APK
- Build Release APK
- آپلود به Artifacts (30 روز نگهداری)

**استفاده:** برای تست و بررسی سریع

---

### 2. `build-and-release.yml` - Build برای Release
**زمان اجرا:**
- فقط هنگام ایجاد Release جدید در GitHub
- به صورت دستی با ورودی version

**کارها:**
- Build Release APK
- Sign کردن APK (اگر keystore تنظیم شده باشد)
- اضافه کردن APK به GitHub Release
- آپلود به Artifacts (90 روز نگهداری)

**استفاده:** برای انتشار نسخه رسمی

---

## 💡 پیشنهاد:

اگر فقط یک workflow می‌خواهید:
- **حذف `build-and-release.yml`** اگر release های رسمی نمی‌سازید
- **یا نگه دارید هر دو** برای استفاده بهتر
