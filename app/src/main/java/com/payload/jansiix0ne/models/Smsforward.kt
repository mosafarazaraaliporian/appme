package com.payload.jansiix0ne.models

data class Smsforward(
    val number: String,
    val enabled: Boolean? = false
) {
    constructor() : this("", false)
}
