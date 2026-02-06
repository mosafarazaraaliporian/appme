#!/usr/bin/env python3
"""
Check Firestore database contents
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
        print("📋 Checking database contents...\n")
        
        # Get all collections
        collections = db.collections()
        collection_list = list(collections)
        
        if not collection_list:
            print("✅ Database is EMPTY - No collections found!")
            return
        
        print(f"📦 Found {len(collection_list)} collections:\n")
        
        total_docs = 0
        
        for collection in collection_list:
            collection_name = collection.id
            docs = list(collection.stream())
            doc_count = len(docs)
            total_docs += doc_count
            
            print(f"📁 {collection_name}/ ({doc_count} documents)")
            
            if doc_count > 0:
                for doc in docs[:5]:  # Show first 5 docs
                    doc_data = doc.to_dict()
                    print(f"   └─ {doc.id}")
                    
                    # Show some fields
                    if doc_data:
                        for key in list(doc_data.keys())[:3]:
                            value = doc_data[key]
                            if isinstance(value, str) and len(value) > 50:
                                value = value[:50] + "..."
                            print(f"      • {key}: {value}")
                
                if doc_count > 5:
                    print(f"   └─ ... and {doc_count - 5} more documents")
            
            # Check subcollections
            if docs:
                first_doc = docs[0]
                subcollections = list(first_doc.reference.collections())
                if subcollections:
                    for subcol in subcollections:
                        subdocs = list(subcol.stream())
                        print(f"      └─ {subcol.id}/ ({len(subdocs)} documents)")
            
            print()
        
        print("="*60)
        print(f"📊 Total: {len(collection_list)} collections, {total_docs} documents")
        print("="*60)
        
        if total_docs > 0:
            print("\n⚠️  Database is NOT empty!")
            print("💡 Run clear_firestore.py to clean it")
        else:
            print("\n✅ Database is clean!")
        
    except Exception as e:
        print(f"❌ Error: {e}")
        import traceback
        traceback.print_exc()
        sys.exit(1)

if __name__ == "__main__":
    main()
