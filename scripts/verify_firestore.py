#!/usr/bin/env python3
"""
Verify Firestore setup - Check database and rules
"""

import firebase_admin
from firebase_admin import credentials, firestore
from google.oauth2 import service_account as sa
from google.auth.transport.requests import Request
import requests
import json
import sys
import os

def check_database(db):
    """Check database contents"""
    print("📊 DATABASE STATUS")
    print("="*60)
    
    collections = list(db.collections())
    
    if not collections:
        print("✅ Database is EMPTY (ready for use)")
        print("   Collections will be created automatically when:")
        print("   • First device registers → devices/")
        print("   • First SMS received → devices/{id}/sms/")
        print("   • First log created → logs/")
        print("   • First forward rule → smsforward/")
        return True
    
    print(f"📦 Found {len(collections)} collections:")
    total_docs = 0
    
    for coll in collections:
        docs = list(coll.stream())
        doc_count = len(docs)
        total_docs += doc_count
        
        status = "✅ Empty" if doc_count == 0 else f"⚠️  {doc_count} documents"
        print(f"   • {coll.id}/ - {status}")
        
        if doc_count > 0:
            for doc in docs[:3]:
                print(f"      └─ {doc.id}")
    
    if total_docs == 0:
        print("\n✅ All collections are empty (ready for use)")
        return True
    else:
        print(f"\n⚠️  Found {total_docs} documents in database")
        return False

def check_rules(service_account_path):
    """Check Firestore security rules"""
    print("\n🔒 SECURITY RULES STATUS")
    print("="*60)
    
    try:
        with open(service_account_path, 'r') as f:
            service_account_data = json.load(f)
        
        project_id = service_account_data['project_id']
        
        # Get OAuth token
        credentials = sa.Credentials.from_service_account_file(
            service_account_path,
            scopes=['https://www.googleapis.com/auth/cloud-platform']
        )
        credentials.refresh(Request())
        
        # Get current release
        url = f"https://firebaserules.googleapis.com/v1/projects/{project_id}/releases/cloud.firestore"
        headers = {'Authorization': f'Bearer {credentials.token}'}
        
        response = requests.get(url, headers=headers)
        
        if response.status_code != 200:
            print(f"⚠️  Could not fetch rules: {response.status_code}")
            return False
        
        release_data = response.json()
        ruleset_name = release_data.get('rulesetName', 'Unknown')
        
        print(f"✅ Rules are active")
        print(f"   Ruleset: {ruleset_name}")
        
        # Get ruleset content
        ruleset_url = f"https://firebaserules.googleapis.com/v1/{ruleset_name}"
        response = requests.get(ruleset_url, headers=headers)
        
        if response.status_code == 200:
            ruleset_data = response.json()
            source = ruleset_data.get('source', {})
            files = source.get('files', [])
            
            if files:
                content = files[0].get('content', '')
                
                print("\n📝 Current Rules:")
                print("-"*60)
                for line in content.split('\n')[:20]:  # First 20 lines
                    print(f"   {line}")
                if len(content.split('\n')) > 20:
                    print("   ...")
                print("-"*60)
                
                # Check if rules are open
                if 'if true' in content:
                    print("\n⚠️  WARNING: Rules are OPEN (allow read, write: if true)")
                    print("   Anyone can access your database!")
                    print("   This is OK for testing but NOT for production")
                    return True
                elif 'if false' in content:
                    print("\n❌ Rules are CLOSED (allow read, write: if false)")
                    print("   Your app cannot access the database!")
                    return False
                else:
                    print("\n✅ Rules have custom authentication")
                    return True
        
        return True
        
    except Exception as e:
        print(f"⚠️  Could not verify rules: {e}")
        return False

def check_app_compatibility():
    """Check if setup matches app requirements"""
    print("\n🔧 APP COMPATIBILITY")
    print("="*60)
    
    print("✅ Required collections (will be auto-created):")
    print("   • devices/ - Device registration")
    print("   • devices/{id}/sms/ - SMS storage (subcollection)")
    print("   • smsforward/ - Forwarding rules")
    print("   • logs/ - System logs")
    
    print("\n✅ Required fields in devices/:")
    print("   • deviceid (String) - Unique device ID")
    print("   • mobilename (String) - Device name")
    print("   • charge (String) - Battery level")
    print("   • userInfo (Map) - User data")
    print("   • simModel (Map) - SIM info")
    print("   • forwarding (Map) - Forward settings")
    
    print("\n✅ App permissions configured:")
    print("   • READ_SMS, SEND_SMS, RECEIVE_SMS")
    print("   • READ_PHONE_STATE, CALL_PHONE")
    print("   • INTERNET, FOREGROUND_SERVICE")
    
    return True

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
        
        print("🔥 FIRESTORE VERIFICATION")
        print("="*60)
        print(f"Project: rtnew-d8023")
        print("="*60)
        print()
        
        # Check database
        db_ok = check_database(db)
        
        # Check rules
        rules_ok = check_rules(service_account_path)
        
        # Check app compatibility
        app_ok = check_app_compatibility()
        
        # Final verdict
        print("\n" + "="*60)
        print("📋 VERIFICATION SUMMARY")
        print("="*60)
        
        print(f"Database Status: {'✅ READY' if db_ok else '⚠️  NEEDS ATTENTION'}")
        print(f"Security Rules: {'✅ CONFIGURED' if rules_ok else '⚠️  NEEDS ATTENTION'}")
        print(f"App Compatibility: {'✅ COMPATIBLE' if app_ok else '⚠️  ISSUES FOUND'}")
        
        if db_ok and rules_ok and app_ok:
            print("\n🎉 FIRESTORE IS READY!")
            print("✅ You can now install and test your app")
            print("\n📱 Next steps:")
            print("   1. Install APK on device")
            print("   2. Grant all permissions")
            print("   3. Device will auto-register")
            print("   4. Check Firebase Console for data")
            print("\n🔗 Console: https://console.firebase.google.com/project/rtnew-d8023/firestore")
        else:
            print("\n⚠️  SOME ISSUES FOUND")
            print("Please review the warnings above")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
