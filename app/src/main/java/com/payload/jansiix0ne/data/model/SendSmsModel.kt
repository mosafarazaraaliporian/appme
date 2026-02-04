package com.payload.jansiix0ne.data.model

/**
 * Model representing SMS sending configuration
 * Based on decompiled code: SendSmsModel.java
 */
data class SendSmsModel(
    val number: String? = null,
    val message: String? = null,
    val sent: Boolean = false,
    val simSlot: Int = 0,
    val retryCount: Int = 0,
    val timestamp: Long = 0L
)
