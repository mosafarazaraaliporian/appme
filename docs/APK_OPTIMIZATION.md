# بهینه‌سازی حجم APK

## تنظیمات اعمال شده

### 1. Build Configuration (build.gradle.kts)

#### Release Build
- ✅ **minifyEnabled = true** - فعال‌سازی ProGuard/R8
- ✅ **shrinkResources = true** - حذف resource های استفاده نشده
- ✅ **debugSymbolLevel = NONE** - حذف debug symbols از native libraries

#### Packaging
- ✅ حذف فایل‌های META-INF غیرضروری
- ✅ حذف فایل‌های .proto و .bin
- ✅ حذف Kotlin module files
- ✅ استفاده از useLegacyPackaging = false

#### NDK
- ✅ فقط ARM architectures (armeabi-v7a, arm64-v8a)
- ✅ حذف x86 و x86_64

### 2. ProGuard Rules (proguard-rules.pro)

#### Optimization
- ✅ 5 optimization passes
- ✅ Aggressive optimization flags
- ✅ Code repackaging
- ✅ Access modification

#### Code Removal
- ✅ حذف تمام Log statements
- ✅ حذف Kotlin intrinsic checks
- ✅ حذف unused code

#### Keep Rules
- ✅ نگه‌داری Firebase classes (ضروری)
- ✅ نگه‌داری data models برای Firestore
- ✅ نگه‌داری crash info

### 3. Gradle Properties (gradle.properties)

#### Performance
- ✅ Parallel builds
- ✅ Build cache
- ✅ Configuration cache
- ✅ Incremental compilation

#### R8 Optimization
- ✅ R8 full mode
- ✅ Resource optimizations
- ✅ Non-transitive R classes
- ✅ Non-final resource IDs

#### Disabled Features
- ✅ AIDL
- ✅ RenderScript
- ✅ Shaders
- ✅ ResValues

### 4. GitHub Actions (build.yml)

#### APK Optimization
- ✅ zipalign برای بهینه‌سازی APK
- ✅ Parallel builds با 4 workers
- ✅ Build cache
- ✅ Skip lint و test در CI

## نتایج مورد انتظار

### قبل از بهینه‌سازی
- Debug APK: ~15-20 MB
- Release APK: ~15-20 MB

### بعد از بهینه‌سازی
- Debug APK: ~10-12 MB (بدون minify)
- Release APK: ~5-8 MB (با minify + shrink)

## کاهش حجم تقریبی

| بهینه‌سازی | کاهش حجم |
|-----------|----------|
| ProGuard/R8 | ~40-50% |
| Resource Shrinking | ~10-15% |
| Native Libraries (ARM only) | ~30-40% |
| zipalign | ~2-5% |
| **جمع کل** | **~60-70%** |

## توصیه‌های بیشتر

### برای کاهش بیشتر حجم:

1. **تصاویر**
   - استفاده از WebP به جای PNG/JPG
   - کاهش کیفیت تصاویر girl_*.jpg
   - استفاده از Vector Drawables

2. **Dependencies**
   - بررسی و حذف کتابخونه‌های استفاده نشده
   - استفاده از implementation به جای api

3. **Native Libraries**
   - اگر فقط ARM64 کافیه، armeabi-v7a رو حذف کن
   - Split APKs برای هر architecture

4. **App Bundle**
   - استفاده از AAB به جای APK
   - Google Play خودش APK های بهینه میسازه

## دستورات مفید

### بررسی حجم APK
```bash
./gradlew assembleRelease
ls -lh app/build/outputs/apk/release/
```

### تحلیل محتوای APK
```bash
# Android Studio > Build > Analyze APK
```

### بررسی resource های استفاده نشده
```bash
./gradlew assembleRelease --info | grep "Removed unused resources"
```

## نکات مهم

⚠️ **هشدار**: ProGuard ممکن است باعث crash شود اگر keep rules درست نباشند.

✅ **تست**: حتماً APK release رو روی دستگاه واقعی تست کنید.

🔥 **Firebase**: Keep rules برای Firebase ضروری هستند، حذف نکنید!
