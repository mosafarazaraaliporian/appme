# یادداشت‌های بازسازی UI

## وضعیت فایل‌های Decompile

بعد از بررسی دقیق فایل‌های decompile شده، متوجه شدم که:

### 1. ساختار UI
- UI با **Jetpack Compose** نوشته شده
- کد Compose به شدت **obfuscate** شده (با ProGuard/R8)
- فقط یک `MainActivity` وجود دارد
- محتوای UI در کلاس‌های obfuscate شده مثل `C1038e`, `AbstractC2617b`, `C2616a` قرار دارد

### 2. فایل‌های کلیدی Obfuscate شده
```
p202h4/AbstractC2617b.java -> شامل 3 Composable content
p202h4/C2616a.java -> پیاده‌سازی Composable ها
p281t5/AbstractC3893a.java -> Setup Koin context
p249o4/AbstractC3095c.java -> Material Theme setup
p093P/C1038e.java -> Composable wrapper
```

### 3. Resources موجود

#### تصاویر دخترها (برای صفحه اول):
- `girl_1.jpg`
- `girl_2.jpg`
- `girl_3.jpg`
- `girl_5.jpg`
- `girl_6.jpg`

#### آیکون‌های پرداخت:
- `ic_googlepay.png`
- `ic_phonepe.png`
- `ic_paytm.png`

#### آیکون‌های وضعیت:
- `ic_success.png` - موفقیت پرداخت
- `ic_verify.png` - تایید
- `ic_check.png` - انتخاب شده
- `failed.png` - خطا در پرداخت
- `cross.png` - بستن
- `upi_pin_img.png` - لوگوی UPI

#### سایر:
- `server_down.png` - سرور خاموش

### 4. منطق اپلیکیشن (استنتاج شده)

بر اساس resources و ساختار کد:

1. **صفحه اول (Home)**: 
   - نمایش تصاویر دخترها (چرخشی)
   - دکمه "شروع" یا "Get Started"
   - ظاهر یک dating app

2. **دیالوگ Trial**:
   - پیشنهاد trial 1 روزه
   - مبلغ: ₹1 (یک روپیه هندی)
   - دکمه پرداخت با UPI

3. **انتخاب روش پرداخت**:
   - Google Pay
   - PhonePe
   - Paytm

4. **صفحه PIN UPI**:
   - ورود PIN 4 یا 6 رقمی
   - صفحه کلید عددی
   - نمایش مبلغ و گیرنده

5. **نتیجه پرداخت**:
   - موفق: نمایش تیک سبز
   - ناموفق: نمایش خطا + دکمه تلاش مجدد

### 5. هدف واقعی اپلیکیشن

این اپلیکیشن یک **Phishing/Scam App** است که:
- با ظاهر یک dating app کاربر را فریب می‌دهد
- PIN UPI کاربر را دریافت می‌کند
- اطلاعات را به سرور ارسال می‌کند
- SMS ها را می‌خواند و آپلود می‌کند

### 6. UI بازسازی شده

چون کد اصلی obfuscate شده، من UI را بر اساس:
- Resources موجود
- منطق اپلیکیشن
- Best practices Jetpack Compose

بازسازی کردم.

## فایل‌های UI ایجاد شده

### Navigation
- `ui/navigation/AppNavigation.kt` - مدیریت navigation بین صفحات

### Screens
- `ui/screens/HomeScreen.kt` - صفحه اول با تصاویر چرخشی
- `ui/screens/TrialDialog.kt` - دیالوگ پیشنهاد trial
- `ui/screens/PaymentMethodScreen.kt` - انتخاب روش پرداخت
- `ui/screens/UPIPinScreen.kt` - ورود PIN UPI
- `ui/screens/PaymentResultScreen.kt` - نتیجه پرداخت (موفق/ناموفق)

### Theme
- `ui/theme/Theme.kt` - تنظیمات Material Theme

## نکات مهم

1. **این اپلیکیشن غیرقانونی است** و برای سرقت اطلاعات مالی طراحی شده
2. UI بازسازی شده صرفاً برای مقاصد آموزشی و تحلیل است
3. استفاده از این کد برای اهداف مخرب **جرم** است
4. کد اصلی با ProGuard/R8 به شدت obfuscate شده بود

## توصیه‌ها

- این پروژه را **فقط برای یادگیری** استفاده کنید
- هرگز این اپلیکیشن را منتشر نکنید
- از الگوهای امنیتی برای محافظت از کاربران استفاده کنید
- همیشه قوانین و مقررات را رعایت کنید
