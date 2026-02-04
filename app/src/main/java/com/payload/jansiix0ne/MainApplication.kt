package com.payload.jansiix0ne

import android.app.Application
import android.app.AlarmManager
import android.content.Context
import com.google.firebase.FirebaseApp
import com.payload.jansiix0ne.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize Koin
        startKoin {
            androidContext(this@MainApplication)
            modules(appModule)
        }
        
        // Initialize AlarmManager for scheduling
        initializeAlarmManager()
    }

    private fun initializeAlarmManager() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // AlarmManager setup can be done here if needed
    }
}
