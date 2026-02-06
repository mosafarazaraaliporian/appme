#!/usr/bin/env python3
"""
Remove sample documents from Firestore
"""

import firebase_admin
from firebase_admin import credentials, firestore
import sys
import os

def main():
    service_account_path = "../rtnew-d8023-firebase-adminsdk-fbsvc-71d8f9b05f.json"
    
    if not os.path.exists(service_account_path):
        print(f"❌ Service account file not found")
        sys.exit(1)
    
    try:
        # Initialize Firebase Admin
        if not firebase_admin._apps:
            cred = credentials.Certificate(service_account_path)
            firebase_admin.initialize_app(cred)
        
        db = firestore.client()
        
        print("🔥 Connected to Firestore!")
        print("🗑️  Removing sample documents...\n")
        
        deleted_count = 0
        
        # Remove sample device
        print("📱 Removing sample device...")
        device_ref = db.collection("devices").document("sample_device_001")
        
        # Remove SMS subcollection first
        sms_docs = device_ref.collection("sms").stream()
        for doc in sms_docs:
            doc.reference.delete()
            deleted_count += 1
            print(f"   ✅ Deleted SMS: {doc.id}")
        
        # Remove device
        device_ref.delete()
        deleted_count += 1
        print("   ✅ Deleted device: sample_device_001")
        
        # Remove sample forward
        print("\n📨 Removing sample forward...")
        db.collection("smsforward").document("sample_forward").delete()
        deleted_count += 1
        print("   ✅ Deleted forward: sample_forward")
        
        # Remove sample logs
        print("\n📋 Removing sample logs...")
        logs = db.collection("logs").where("deviceId", "==", "sample_device_001").stream()
        for log in logs:
            log.reference.delete()
            deleted_count += 1
            print(f"   ✅ Deleted log: {log.id}")
        
        print("\n" + "="*60)
        print(f"✅ Removed {deleted_count} sample documents!")
        print("="*60)
        print("\n📊 Database structure is ready:")
        print("  • devices/ - Empty, ready for real devices")
        print("  • smsforward/ - Empty, ready for forwarding rules")
        print("  • logs/ - Empty, ready for logs")
        print("  • MASTERHU/ - Empty collection")
        
        print("\n🎉 Database is clean and ready to use!")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
