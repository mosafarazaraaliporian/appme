package com.payload.jansiix0ne.data.model

/**
 * Model representing SMS sending configuration
 */
data class SendSmsModel(
    val number: String? = null,
    val message: String? = null,
    val sent: Boolean = false,
    val retryCount: Int = 0,
    val timestamp: Long = 0L
)
