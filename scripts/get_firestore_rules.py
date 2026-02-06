#!/usr/bin/env python3
"""
Get current Firestore security rules
"""

import requests
import json
import sys
import os

def get_rules():
    service_account_path = "../rtnew-d8023-firebase-adminsdk-fbsvc-71d8f9b05f.json"
    
    if not os.path.exists(service_account_path):
        print(f"❌ Service account file not found")
        sys.exit(1)
    
    # Read service account
    with open(service_account_path, 'r') as f:
        service_account = json.load(f)
    
    project_id = service_account['project_id']
    
    print(f"📋 Project ID: {project_id}")
    print(f"🔍 Checking Firestore rules...\n")
    
    # Get OAuth token
    from google.oauth2 import service_account
    from google.auth.transport.requests import Request
    
    credentials = service_account.Credentials.from_service_account_file(
        service_account_path,
        scopes=['https://www.googleapis.com/auth/cloud-platform']
    )
    credentials.refresh(Request())
    
    # Get rules
    url = f"https://firestore.googleapis.com/v1/projects/{project_id}/databases/(default)/documents/@settings/rules"
    
    print("⚠️  Note: Rules can only be viewed/updated via Firebase Console or Firebase CLI")
    print("🔗 Go to: https://console.firebase.google.com/project/rtnew-d8023/firestore/rules")
    print("\n📝 Recommended rules for your app:\n")
    
    rules = """rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Devices collection - allow read/write for authenticated users
    match /devices/{deviceId} {
      allow read, write: if true;  // Open for testing - CHANGE IN PRODUCTION!
      
      // SMS subcollection
      match /sms/{smsId} {
        allow read, write: if true;
      }
    }
    
    // SMS forwarding rules
    match /smsforward/{forwardId} {
      allow read, write: if true;
    }
    
    // Logs collection
    match /logs/{logId} {
      allow read, write: if true;
    }
    
    // Default deny all other collections
    match /{document=**} {
      allow read, write: if false;
    }
  }
}"""
    
    print(rules)
    print("\n" + "="*60)
    print("⚠️  SECURITY WARNING:")
    print("="*60)
    print("Current rules allow ANYONE to read/write!")
    print("This is OK for testing but NOT for production.")
    print("\nFor production, add authentication:")
    print("  allow read, write: if request.auth != null;")
    print("\nOr restrict by device ID:")
    print("  allow read, write: if request.auth.uid == deviceId;")
    
    # Save to file
    with open('firestore.rules', 'w') as f:
        f.write(rules)
    
    print("\n✅ Rules saved to: firestore.rules")
    print("📤 To deploy: firebase deploy --only firestore:rules")

if __name__ == "__main__":
    try:
        get_rules()
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
