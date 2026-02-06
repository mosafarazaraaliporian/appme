#!/usr/bin/env python3
"""
Clear all Firestore collections
Requires: pip install firebase-admin
"""

import firebase_admin
from firebase_admin import credentials, firestore
import sys
import os

def delete_collection(db, collection_name, batch_size=100):
    """Delete all documents in a collection"""
    collection_ref = db.collection(collection_name)
    docs = collection_ref.limit(batch_size).stream()
    deleted = 0

    for doc in docs:
        doc.reference.delete()
        deleted += 1

    if deleted >= batch_size:
        return delete_collection(db, collection_name, batch_size)
    
    return deleted

def main():
    # Path to service account key
    service_account_path = "../rtnew-d8023-firebase-adminsdk-fbsvc-71d8f9b05f.json"
    
    if not os.path.exists(service_account_path):
        print(f"❌ Service account file not found: {service_account_path}")
        print("Please make sure the file is in the correct location.")
        sys.exit(1)
    
    try:
        # Initialize Firebase Admin
        cred = credentials.Certificate(service_account_path)
        firebase_admin.initialize_app(cred)
        
        # Get Firestore client
        db = firestore.client()
        
        print("🔥 Connected to Firestore!")
        print("📋 Listing all collections...")
        
        # Get all collections
        collections = db.collections()
        collection_names = [col.id for col in collections]
        
        if not collection_names:
            print("✅ No collections found. Database is already empty!")
            return
        
        print(f"\n📦 Found {len(collection_names)} collections:")
        for name in collection_names:
            print(f"  - {name}")
        
        print("\n⚠️  WARNING: This will delete ALL data!")
        print("🗑️  Starting deletion in 3 seconds...")
        
        import time
        time.sleep(3)
        
        # Delete each collection
        total_deleted = 0
        for collection_name in collection_names:
            print(f"\n🗑️  Deleting collection: {collection_name}")
            deleted = delete_collection(db, collection_name)
            total_deleted += deleted
            print(f"   ✅ Deleted {deleted} documents")
        
        print(f"\n✅ Successfully deleted {total_deleted} documents from {len(collection_names)} collections!")
        print("🎉 Firestore database is now empty!")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        sys.exit(1)

if __name__ == "__main__":
    main()
