package com.payload.jansiix0ne.data.model

import com.google.firebase.Timestamp
import java.util.Date

/**
 * Model representing an SMS message
 */
data class SmsModel(
    val from: String,
    val message: String,
    val time: Timestamp,
    val ownerDeviceId: String
) {
    constructor(from: String, message: String, date: Date, ownerDeviceId: String) :
            this(from, message, Timestamp(date), ownerDeviceId)
}
