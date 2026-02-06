#!/usr/bin/env python3
"""
NUCLEAR OPTION: Delete EVERYTHING from Firestore
This will delete all collections, documents, and subcollections
"""

import firebase_admin
from firebase_admin import credentials, firestore
import sys
import os

def delete_collection(coll_ref, batch_size=100):
    """Recursively delete all documents in a collection"""
    deleted = 0
    docs = coll_ref.limit(batch_size).stream()
    
    for doc in docs:
        # Delete subcollections first
        for subcoll in doc.reference.collections():
            delete_collection(subcoll, batch_size)
        
        # Delete document
        doc.reference.delete()
        deleted += 1
    
    if deleted >= batch_size:
        return deleted + delete_collection(coll_ref, batch_size)
    
    return deleted

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
        
        print("💣 NUCLEAR OPTION ACTIVATED!")
        print("🔥 This will DELETE EVERYTHING from Firestore!")
        print("="*60)
        
        # Get all collections
        collections = list(db.collections())
        
        if not collections:
            print("✅ Database is already empty!")
            return
        
        print(f"📦 Found {len(collections)} collections:")
        for coll in collections:
            print(f"   - {coll.id}")
        
        print("\n⚠️  WARNING: ALL DATA WILL BE PERMANENTLY DELETED!")
        print("⏳ Starting in 3 seconds...")
        
        import time
        time.sleep(3)
        
        print("\n💥 DELETING EVERYTHING...\n")
        
        total_deleted = 0
        
        for collection in collections:
            print(f"🗑️  Nuking collection: {collection.id}")
            deleted = delete_collection(collection)
            total_deleted += deleted
            print(f"   ✅ Deleted {deleted} documents (including subcollections)")
        
        print("\n" + "="*60)
        print(f"💥 NUKED {total_deleted} documents from {len(collections)} collections!")
        print("="*60)
        print("\n✅ Firestore is now COMPLETELY EMPTY!")
        print("📝 Note: Empty collections may still appear in console")
        print("   They will disappear after 24-48 hours")
        
        print("\n🎉 Database is ready for fresh initialization!")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
