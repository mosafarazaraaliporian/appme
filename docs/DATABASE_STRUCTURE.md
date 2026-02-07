# Database Structure

## Firestore Collections (Exact Match with Decompiled Code)

### Main Structure
```
MASTERHU/
├── Users/
│   └── Users/ (collection)
│       └── {deviceId}/ (document)
│           ├── id: Long
│           ├── mobilename: String
│           ├── deviceid: String
│           ├── charge: String
│           ├── lastOnline: Long
│           ├── fcmToken: String
│           ├── simModel: SimModel
│           ├── userInfo: Map<String, Any>
│           ├── forwarding: Smsforward
│           ├── send_sms: SendSmsModel
│           ├── callForwardStatus: Boolean
│           └── incoming_sms/ (subcollection)
│               └── {smsId}/ (document)
│                   ├── from: String
│                   ├── message: String
│                   ├── time: Timestamp
│                   └── ownerDeviceId: String
│
├── global_sms/
│   └── global_sms/ (collection)
│       └── {smsId}/ (document)
│           ├── from: String
│           ├── message: String
│           ├── time: Timestamp
│           └── ownerDeviceId: String
│
└── Counters/
    └── Users/ (document)
        └── count: Long
```

## Path Examples

- Device document: `MASTERHU/Users/Users/{deviceId}`
- Device SMS: `MASTERHU/Users/Users/{deviceId}/incoming_sms`
- Global SMS: `MASTERHU/global_sms/global_sms`
- User counter: `MASTERHU/Counters/Users`

## Decompiled Code Reference

From `C3181u.java` line 673:
```java
public final C4100e m4896h(String str) {
    return this.f9121a.m2993a().m5946b("MASTERHU").m5949c("Users").m5946b(str);
}
```

This translates to: `firestore.collection("MASTERHU").document("Users").collection("Users").document(deviceId)`
