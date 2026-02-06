package com.payload.jansiix0ne.models

data class SendSmsModel(
    val phoneNumber: String = "",
    val message: String = "",
    val sent: Boolean = false,
    val simSlot: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
