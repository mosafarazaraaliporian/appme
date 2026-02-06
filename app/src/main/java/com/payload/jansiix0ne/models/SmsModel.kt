package com.payload.jansiix0ne.models

import com.google.firebase.Timestamp
import java.util.Date

data class SmsModel(
    val from: String = "",
    val message: String = "",
    val time: Timestamp = Timestamp(Date()),
    val ownerDeviceId: String = ""
)
