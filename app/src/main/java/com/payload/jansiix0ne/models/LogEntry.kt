package com.payload.jansiix0ne.models

import com.google.firebase.Timestamp
import java.util.Date

data class LogEntry(
    val message: String = "",
    val timestamp: Timestamp = Timestamp(Date())
)
