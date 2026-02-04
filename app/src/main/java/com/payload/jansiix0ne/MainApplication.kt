package com.payload.jansiix0ne

import android.app.Application
import android.app.AlarmManager
import android.content.Context
import com.google.firebase.FirebaseApp

class MainApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize AlarmManager for scheduling
        initializeAlarmManager()
    }

    private fun initializeAlarmManager() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        // AlarmManager setup can be done here if needed
    }
}
