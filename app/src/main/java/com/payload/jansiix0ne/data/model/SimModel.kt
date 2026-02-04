package com.payload.jansiix0ne.data.model

/**
 * Model representing SIM card information
 */
data class SimModel(
    val slotIndex: Int = 0,
    val phoneNumber: String? = null,
    val carrierName: String? = null,
    val subscriptionId: Int? = null
)
