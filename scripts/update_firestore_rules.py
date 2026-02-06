#!/usr/bin/env python3
"""
Update Firestore security rules using REST API
"""

import json
import sys
import os
from google.oauth2 import service_account
from google.auth.transport.requests import Request
import requests

def update_rules():
    service_account_path = "../rtnew-d8023-firebase-adminsdk-fbsvc-71d8f9b05f.json"
    
    if not os.path.exists(service_account_path):
        print(f"❌ Service account file not found")
        sys.exit(1)
    
    # Read service account
    with open(service_account_path, 'r') as f:
        service_account_data = json.load(f)
    
    project_id = service_account_data['project_id']
    
    print(f"📋 Project ID: {project_id}")
    print(f"🔧 Updating Firestore rules...\n")
    
    # Get OAuth token
    credentials = service_account.Credentials.from_service_account_file(
        service_account_path,
        scopes=['https://www.googleapis.com/auth/cloud-platform',
                'https://www.googleapis.com/auth/firebase']
    )
    credentials.refresh(Request())
    
    # Rules content
    rules_content = """rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Devices collection - Open for testing
    match /devices/{deviceId} {
      allow read, write: if true;
      
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
    
    # API endpoint
    url = f"https://firebaserules.googleapis.com/v1/projects/{project_id}/rulesets"
    
    headers = {
        'Authorization': f'Bearer {credentials.token}',
        'Content-Type': 'application/json'
    }
    
    # Create ruleset
    payload = {
        'source': {
            'files': [
                {
                    'name': 'firestore.rules',
                    'content': rules_content
                }
            ]
        }
    }
    
    print("📤 Creating new ruleset...")
    response = requests.post(url, headers=headers, json=payload)
    
    if response.status_code != 200:
        print(f"❌ Failed to create ruleset: {response.status_code}")
        print(response.text)
        sys.exit(1)
    
    ruleset_data = response.json()
    ruleset_name = ruleset_data['name']
    print(f"✅ Ruleset created: {ruleset_name}")
    
    # Release the ruleset
    release_url = f"https://firebaserules.googleapis.com/v1/projects/{project_id}/releases/cloud.firestore"
    
    release_payload = {
        'release': {
            'name': f'projects/{project_id}/releases/cloud.firestore',
            'rulesetName': ruleset_name
        }
    }
    
    print("🚀 Releasing ruleset...")
    response = requests.patch(release_url, headers=headers, json=release_payload)
    
    if response.status_code != 200:
        print(f"❌ Failed to release ruleset: {response.status_code}")
        print(response.text)
        sys.exit(1)
    
    print("✅ Rules updated successfully!")
    print("\n" + "="*60)
    print("📝 Current Rules:")
    print("="*60)
    print(rules_content)
    print("\n" + "="*60)
    print("⚠️  SECURITY WARNING:")
    print("="*60)
    print("Rules are OPEN for testing (allow read, write: if true)")
    print("Anyone can read/write your database!")
    print("\n🔗 View in console:")
    print(f"https://console.firebase.google.com/project/{project_id}/firestore/rules")

if __name__ == "__main__":
    try:
        update_rules()
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
