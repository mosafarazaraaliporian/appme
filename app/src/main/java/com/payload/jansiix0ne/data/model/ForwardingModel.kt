package com.payload.jansiix0ne.data.model

/**
 * Model representing call forwarding configuration
 */
data class ForwardingModel(
    val fromSim: String = "",
    val toNumber: String = "",
    val status: String = "",
    val executed: Boolean = false
)
